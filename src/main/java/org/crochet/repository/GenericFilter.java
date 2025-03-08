package org.crochet.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.crochet.enums.FilterLogic;
import org.crochet.enums.FilterOperation;
import org.crochet.model.Category;
import org.crochet.payload.request.Filter;
import org.crochet.util.ObjectUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Optimized GenericFilter for building dynamic JPA specifications
 *
 * @param <T> Entity type
 */
public class GenericFilter<T> {

    private final FilterNode rootNode;
    private static final Map<FilterOperation, PredicateBuilder> PREDICATE_BUILDERS = new EnumMap<>(FilterOperation.class);
    private static final Map<Class<?>, ValueParser<?>> VALUE_PARSERS = new ConcurrentHashMap<>();
    private static List<DateTimeFormatter> DATE_TIME_FORMATTERS;

    static {
        // Initialize predicate builders map
        initPredicateBuilders();

        // Initialize value parsers
        initValueParsers();
    }

    /**
     * Creates a GenericFilter from an array of Filter objects.
     */
    public static <T> GenericFilter<T> create(Filter[] filters) {
        GenericFilter<T> genericFilter = new GenericFilter<>();
        if (ObjectUtils.isEmpty(filters)) {
            return genericFilter;
        }

        FilterNode rootNode = genericFilter.getRoot();
        List<FilterNode> filterNodes = Arrays.stream(filters)
                .filter(filter -> filter != null && ObjectUtils.isNotEmpty(filter.getFilterCriteria()))
                .map(GenericFilter::createFilterNode)
                .collect(Collectors.toList());
        rootNode.addAllChildren(filterNodes);

        return genericFilter;
    }

    /**
     * Private constructor
     */
    private GenericFilter() {
        this.rootNode = new FilterNode(FilterLogic.ALL);
    }

    /**
     * Get root node
     */
    private FilterNode getRoot() {
        return rootNode;
    }

    /**
     * Build specification
     */
    public Specification<T> build() {
        return (root, query, criteriaBuilder) -> buildPredicate(rootNode, root, query, criteriaBuilder);
    }

    /**
     * Creates a FilterNode from a Filter object
     */
    private static FilterNode createFilterNode(Filter filter) {
        FilterNode parentNode = new FilterNode(filter.getFilterLogic());
        List<FilterNode> childNodes = filter.getFilterCriteria().stream()
                .map(FilterNode::new)
                .collect(Collectors.toList());
        parentNode.addAllChildren(childNodes);
        return parentNode;
    }

    /**
     * Build predicate recursively from filter node
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
            return criteriaBuilder.conjunction();
        }

        Predicate[] predicateArray = predicates.toArray(new Predicate[0]);
        return node.getLogic() == FilterLogic.ALL
                ? criteriaBuilder.and(predicateArray)
                : criteriaBuilder.or(predicateArray);
    }

    /**
     * Create a predicate for a single filter criteria
     */
    private Predicate createPredicate(FilterCriteria criteria, Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        PredicateBuilder builder = PREDICATE_BUILDERS.get(criteria.getOperation());
        if (builder == null) {
            throw new IllegalArgumentException("Unsupported operation: " + criteria.getOperation());
        }

        return builder.build(criteria, root, query, cb, this);
    }

    /**
     * Get path from root by dot notation key
     */
    <Y> Path<Y> getPath(Root<?> root, String key) {
        String[] parts = key.split("\\.");
        Path<Y> path = root.get(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            path = path.get(parts[i]);
        }
        return path;
    }

    /**
     * Parse value according to field type
     */
    Object parseValue(Root<?> root, FilterCriteria criteria) {
        Path<?> path = getPath(root, criteria.getKey());
        Class<?> fieldType = path.getJavaType();
        Object value = criteria.getValue();

        if (value == null) {
            return null;
        }

        ValueParser<?> parser = VALUE_PARSERS.get(fieldType);
        if (parser != null) {
            return parser.parse(value);
        }

        return value;
    }

    /**
     * Get predicate for category and its children
     */
    private Predicate getByCategoryAndChildren(List<String> categoryIds,
                                               Root<?> root,
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

    /**
     * Parse date/time string to LocalDateTime
     */
    private static LocalDateTime parseDateTime(String value, boolean isDateTime) {
        if (DATE_TIME_FORMATTERS == null) {
            initDateTimeFormatters();
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                if (isDateTime) {
                    return LocalDateTime.parse(value, formatter);
                } else {
                    return LocalDate.parse(value, formatter).atStartOfDay();
                }
            } catch (DateTimeParseException e) {
                // Continue to next formatter
            }
        }
        throw new IllegalArgumentException("Unable to parse date/time: " + value);
    }

    /**
     * Initialize date/time formatters lazily
     */
    private static synchronized void initDateTimeFormatters() {
        if (DATE_TIME_FORMATTERS == null) {
            DATE_TIME_FORMATTERS = List.of(
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                    DateTimeFormatter.ISO_DATE_TIME,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                    DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm:ss"),
                    DateTimeFormatter.ofPattern("dd-MM-yyy"),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
                    DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"),
                    DateTimeFormatter.ISO_LOCAL_DATE,
                    DateTimeFormatter.ISO_DATE,
                    DateTimeFormatter.BASIC_ISO_DATE
                    // Reduced number of formatters for better performance
            );
        }
    }

    /**
     * Initialize predicate builders
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void initPredicateBuilders() {
        PREDICATE_BUILDERS.put(FilterOperation.EQUAL,
                (criteria, root, query, cb, filter) ->
                        cb.equal(filter.getPath(root, criteria.getKey()), filter.parseValue(root, criteria)));

        PREDICATE_BUILDERS.put(FilterOperation.NOT_EQUAL,
                (criteria, root, query, cb, filter) ->
                        cb.notEqual(filter.getPath(root, criteria.getKey()), filter.parseValue(root, criteria)));

        PREDICATE_BUILDERS.put(FilterOperation.GREATER_THAN,
                (criteria, root, query, cb, filter) ->
                        cb.greaterThan(filter.getPath(root, criteria.getKey()),
                                (Comparable) filter.parseValue(root, criteria)));

        PREDICATE_BUILDERS.put(FilterOperation.LESS_THAN,
                (criteria, root, query, cb, filter) ->
                        cb.lessThan(filter.getPath(root, criteria.getKey()),
                                (Comparable) filter.parseValue(root, criteria)));

        PREDICATE_BUILDERS.put(FilterOperation.GREATER_THAN_OR_EQUAL,
                (criteria, root, query, cb, filter) ->
                        cb.greaterThanOrEqualTo(filter.getPath(root, criteria.getKey()),
                                (Comparable) filter.parseValue(root, criteria)));

        PREDICATE_BUILDERS.put(FilterOperation.LESS_THAN_OR_EQUAL,
                (criteria, root, query, cb, filter) ->
                        cb.lessThanOrEqualTo(filter.getPath(root, criteria.getKey()),
                                (Comparable) filter.parseValue(root, criteria)));

        PREDICATE_BUILDERS.put(FilterOperation.LIKE,
                (criteria, root, query, cb, filter) ->
                        cb.like(cb.lower(filter.getPath(root, criteria.getKey())),
                                "%" + criteria.getValue().toString().toLowerCase() + "%"));

        PREDICATE_BUILDERS.put(FilterOperation.IN,
                (criteria, root, query, cb, filter) -> {
                    if ("category.id".equals(criteria.getKey())) {
                        return filter.getByCategoryAndChildren((List<String>) criteria.getValue(), root, query, cb);
                    }
                    return filter.getPath(root, criteria.getKey()).in((Collection<?>) criteria.getValue());
                });

        PREDICATE_BUILDERS.put(FilterOperation.BETWEEN,
                (criteria, root, query, cb, filter) -> {
                    if (!(criteria.getValue() instanceof List<?> values) || values.size() != 2) {
                        throw new IllegalArgumentException("Between operation requires a list of two values");
                    }
                    return cb.between(filter.getPath(root, criteria.getKey()),
                            (Comparable) filter.parseValue(root, new FilterCriteria(criteria.getKey(), values.get(0), criteria.getOperation())),
                            (Comparable) filter.parseValue(root, new FilterCriteria(criteria.getKey(), values.get(1), criteria.getOperation())));
                });

        // New operations
        PREDICATE_BUILDERS.put(FilterOperation.IS_NULL,
                (criteria, root, query, cb, filter) ->
                        cb.isNull(filter.getPath(root, criteria.getKey())));

        PREDICATE_BUILDERS.put(FilterOperation.IS_NOT_NULL,
                (criteria, root, query, cb, filter) ->
                        cb.isNotNull(filter.getPath(root, criteria.getKey())));

        PREDICATE_BUILDERS.put(FilterOperation.STARTS_WITH,
                (criteria, root, query, cb, filter) ->
                        cb.like(cb.lower(filter.getPath(root, criteria.getKey())),
                                criteria.getValue().toString().toLowerCase() + "%"));

        PREDICATE_BUILDERS.put(FilterOperation.ENDS_WITH,
                (criteria, root, query, cb, filter) ->
                        cb.like(cb.lower(filter.getPath(root, criteria.getKey())),
                                "%" + criteria.getValue().toString().toLowerCase()));
    }

    /**
     * Initialize value parsers
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void initValueParsers() {
        // Enum parser
        VALUE_PARSERS.put(Enum.class, value -> {
            if (!(value instanceof String)) return value;
            Class<? extends Enum> enumClass = Enum.class;
            return Enum.valueOf(enumClass, (String) value);
        });

        // LocalDateTime parser
        VALUE_PARSERS.put(LocalDateTime.class, value -> {
            if (!(value instanceof String)) return value;
            return parseDateTime((String) value, true);
        });

        // LocalDate parser
        VALUE_PARSERS.put(LocalDate.class, value -> {
            if (!(value instanceof String)) return value;
            return parseDateTime((String) value, false).toLocalDate();
        });

        // Boolean parser
        VALUE_PARSERS.put(Boolean.class, value -> {
            if (value instanceof Boolean) return value;
            if (value instanceof String) {
                String strVal = ((String) value).toLowerCase();
                return "true".equals(strVal) || "yes".equals(strVal) || "1".equals(strVal);
            }
            if (value instanceof Number) {
                return ((Number) value).intValue() != 0;
            }
            return value;
        });
    }

    /**
     * Predicate builder functional interface
     */
    @FunctionalInterface
    private interface PredicateBuilder {
        Predicate build(FilterCriteria criteria, Root<?> root, CriteriaQuery<?> query,
                        CriteriaBuilder cb, GenericFilter<?> filter);
    }

    /**
     * Value parser functional interface
     */
    @FunctionalInterface
    private interface ValueParser<T> {
        T parse(Object value);
    }
}