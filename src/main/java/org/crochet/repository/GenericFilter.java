package org.crochet.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.crochet.enumerator.FilterLogic;
import org.crochet.model.Category;
import org.crochet.payload.request.Filter;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * GenericFilter class
 *
 * @param <T> BaseEntity
 */
public class GenericFilter<T> {

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"),
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ISO_DATE,
            DateTimeFormatter.ISO_TIME,
            DateTimeFormatter.BASIC_ISO_DATE,
            DateTimeFormatter.ISO_LOCAL_TIME,
            DateTimeFormatter.ISO_INSTANT,
            DateTimeFormatter.ISO_OFFSET_DATE,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_TIME,
            DateTimeFormatter.ISO_ORDINAL_DATE,
            DateTimeFormatter.ISO_WEEK_DATE,
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.RFC_1123_DATE_TIME
    );

    private final FilterNode rootNode;

    /**
     * Creates a GenericFilter object from an array of Filter objects.
     *
     * @param filters An array of Filter objects containing filter logic and criteria.
     * @return A GenericFilter object with the specified filters applied.
     */
    public static <T> GenericFilter<T> create(Filter[] filters) {
        GenericFilter<T> genericFilter = new GenericFilter<>();
        if (isEmpty(filters)) {
            return genericFilter;
        }

        FilterNode rootNode = genericFilter.getRoot();
        List<FilterNode> filterNodes = Stream.of(filters)
                .filter(GenericFilter::hasFilterCriteria)
                .map(GenericFilter::createFilterNode)
                .toList();
        rootNode.addAllChildren(filterNodes);

        return genericFilter;
    }

    /**
     * Constructor
     */
    public GenericFilter() {
        this.rootNode = new FilterNode(FilterLogic.ALL);
    }

    /**
     * Get root
     *
     * @return FilterNode
     */
    public FilterNode getRoot() {
        return rootNode;
    }

    /**
     * Build
     *
     * @return Specification
     */
    public Specification<T> build() {
        return (root, query, criteriaBuilder) -> buildPredicate(rootNode, root, query, criteriaBuilder);
    }

    /**
     * Checks if the array of filters is empty or null.
     *
     * @param filters An array of filters to check.
     * @return true if the filters array is null or has no elements; false otherwise.
     */
    private static boolean isEmpty(Filter[] filters) {
        return filters == null || filters.length == 0;
    }

    /**
     * Determines if a given filter contains any filter criteria.
     *
     * @param filter the Filter object to be checked.
     * @return true if the filter contains criteria; false otherwise.
     */
    private static boolean hasFilterCriteria(Filter filter) {
        return !filter.getFilterCriteria().isEmpty();
    }

    /**
     * Creates a FilterNode from a given Filter object.
     *
     * @param filter the Filter object containing filter logic and criteria.
     * @return a FilterNode representing the root of the filter tree with its child nodes.
     */
    private static FilterNode createFilterNode(Filter filter) {
        FilterNode parentNode = new FilterNode(filter.getFilterLogic());
        List<FilterNode> childNodes = filter.getFilterCriteria().stream()
                .map(FilterNode::new)
                .toList();
        parentNode.addAllChildren(childNodes);
        return parentNode;
    }

    /**
     * Build predicate
     *
     * @param node            FilterNode
     * @param root            Root
     * @param criteriaBuilder CriteriaBuilder
     * @return Predicate
     */
    private Predicate buildPredicate(FilterNode node, Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        for (FilterNode child : node.getChildren()) {
            if (child.getCriteria() != null) {
                predicates.add(createPredicate(child.getCriteria(), root, query, criteriaBuilder));
            } else {
                predicates.add(buildPredicate(child, root, query, criteriaBuilder));
            }
        }

        if (predicates.isEmpty()) {
            return null;
        }

        return node.getLogic() == FilterLogic.ALL
                ? criteriaBuilder.and(predicates.toArray(new Predicate[0]))
                : criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    }

    /**
     * Create predicate
     *
     * @param criteria        FilterCriteria
     * @param root            Root
     * @param criteriaBuilder CriteriaBuilder
     * @return Predicate
     */
    private Predicate createPredicate(FilterCriteria criteria, Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return switch (criteria.getOperation()) {
            case EQUAL -> criteriaBuilder.equal(getPath(root, criteria.getKey()), parseValue(root, criteria));
            case NOT_EQUAL -> criteriaBuilder.notEqual(getPath(root, criteria.getKey()), parseValue(root, criteria));
            case GREATER_THAN ->
                    criteriaBuilder.greaterThan(getPath(root, criteria.getKey()), (Comparable) parseValue(root, criteria));
            case LESS_THAN ->
                    criteriaBuilder.lessThan(getPath(root, criteria.getKey()), (Comparable) parseValue(root, criteria));
            case GREATER_THAN_OR_EQUAL ->
                    criteriaBuilder.greaterThanOrEqualTo(getPath(root, criteria.getKey()), (Comparable) parseValue(root, criteria));
            case LESS_THAN_OR_EQUAL ->
                    criteriaBuilder.lessThanOrEqualTo(getPath(root, criteria.getKey()), (Comparable) parseValue(root, criteria));
            case LIKE ->
                    criteriaBuilder.like(criteriaBuilder.lower(getPath(root, criteria.getKey())), "%" + criteria.getValue().toString().toLowerCase() + "%");
            case IN -> {
                if (criteria.getKey().equals("category.id")) {
                    yield getByCategoryAndChildren((List<String>) criteria.getValue(), root, query, criteriaBuilder);
                }
                yield getPath(root, criteria.getKey()).in((Collection<?>) criteria.getValue());
            }
            case BETWEEN -> {
                if (criteria.getValue() instanceof List<?> values && values.size() == 2) {
                    yield criteriaBuilder.between(getPath(root, criteria.getKey()),
                            (Comparable) parseValue(root, new FilterCriteria(criteria.getKey(), values.get(0), criteria.getOperation())),
                            (Comparable) parseValue(root, new FilterCriteria(criteria.getKey(), values.get(1), criteria.getOperation())));
                }
                throw new IllegalArgumentException("Between operation requires a list of two values");
            }
        };
    }

    /**
     * Get path
     *
     * @param root Root
     * @param key  String
     * @param <Y>  Object
     * @return Path
     */
    private <Y> Path<Y> getPath(Root<T> root, String key) {
        String[] split = key.split("\\.");
        Path<Y> path = root.get(split[0]);
        for (int i = 1; i < split.length; i++) {
            path = path.get(split[i]);
        }
        return path;
    }

    /**
     * Parse value
     *
     * @param root     Root
     * @param criteria FilterCriteria
     * @return Object
     */
    private Object parseValue(Root<T> root, FilterCriteria criteria) {
        Class<?> fieldType = getPath(root, criteria.getKey()).getJavaType();
        Object value = criteria.getValue();

        if (isEnumType(fieldType, value)) {
            return parseEnumValue(fieldType, (String) value);
        } else if (isDateTimeType(fieldType, value)) {
            return parseDateTime((String) value, fieldType.isAssignableFrom(LocalDateTime.class));
        }
        return value;
    }

    /**
     * Determines if a given field type is an enumeration and the value is a string.
     *
     * @param fieldType the Class object representing the field type.
     * @param value the value to check.
     * @return true if the field type is an enumeration and the value is a string; false otherwise.
     */
    private boolean isEnumType(Class<?> fieldType, Object value) {
        return fieldType.isEnum() && value instanceof String;
    }

    /**
     * Parses the provided string value into an Enum of the specified field type.
     *
     * @param fieldType the Class object representing the Enum type.
     * @param value the string value to be converted to an Enum constant.
     * @return the Enum constant corresponding to the provided string value.
     */
    private Object parseEnumValue(Class<?> fieldType, String value) {
        return Enum.valueOf((Class<Enum>) fieldType, value);
    }

    /**
     * Determines if a given field type is a LocalDateTime or LocalDate and the value is a string.
     *
     * @param fieldType the Class object representing the field type.
     * @param value the value to check.
     * @return true if the field type is a LocalDateTime or LocalDate and the value is a string; false otherwise.
     */
    private boolean isDateTimeType(Class<?> fieldType, Object value) {
        return (LocalDateTime.class.isAssignableFrom(fieldType) || LocalDate.class.isAssignableFrom(fieldType)) && value instanceof String;
    }

    /**
     * Parse DateTime
     *
     * @param dateTimeString String
     * @param isDateTime     Boolean
     * @return LocalDateTime
     */
    private static LocalDateTime parseDateTime(String dateTimeString, boolean isDateTime) {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                if (isDateTime) {
                    return LocalDateTime.parse(dateTimeString, formatter);
                } else {
                    return LocalDate.parse(dateTimeString, formatter).atStartOfDay();
                }
            } catch (DateTimeParseException e) {
                // Continue to the next formatter
            }
        }
        throw new IllegalArgumentException("Unable to parse date time: " + dateTimeString);
    }

    /**
     * Get by category and children
     *
     * @param categoryIds The list of category ids
     * @param root        Root
     * @param query       CriteriaQuery
     * @param cb          CriteriaBuilder
     * @return Predicate
     */
    private Predicate getByCategoryAndChildren(List<String> categoryIds,
                                               Root<T> root,
                                               CriteriaQuery<?> query,
                                               CriteriaBuilder cb) {
        Subquery<String> subquery = query.subquery(String.class);
        Root<Category> categoryRoot = subquery.from(Category.class);

        subquery.select(categoryRoot.get("id"))
                .where(cb.or(
                        categoryRoot.get("id").in(categoryIds),
                        categoryRoot.get("parent").get("id").in(categoryIds)
                ));

        return root.get("category").get("id").in(subquery);
    }
}