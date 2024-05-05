package org.crochet.repository;

import org.crochet.model.AuditTable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.crochet.constant.AppConstant.DATE_PATTERN;
import static org.crochet.enumerator.QueryOperator.OPERATORS;
import static org.springframework.data.jpa.domain.Specification.where;

public class Specifications {

    public static <T extends AuditTable> Specification<T> getSpecificationFromFilters(List<Filter> filters) {
        Specification<T> spec = where(null);
        if (ObjectUtils.isEmpty(filters)) {
            return spec;
        }
        for (Filter input : filters) {
            if (!StringUtils.hasText(input.getField())) {
                continue;
            }
            if (input.getOperator() == null || !OPERATORS.contains(input.getOperator())) {
                continue;
            }
            if (!StringUtils.hasText(input.getValue())) {
                if (ObjectUtils.isEmpty(input.getValues())) {
                    continue;
                }
            }
            spec = spec.or(createSpecification(input));
        }
        return spec;
    }

    private static <T extends AuditTable> Specification<T> createSpecification(Filter input) {
        return switch (input.getOperator()) {
            case EQUALS -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(input.getField()),
                            castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue()));

            case NOT_EQUALS -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.notEqual(root.get(input.getField()),
                            castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue()));

            case GREATER_THAN -> (root, query, criteriaBuilder) -> {
                Comparable comparable = (Comparable) castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue());
                return criteriaBuilder.greaterThan(root.get(input.getField()).as(comparable.getClass()), comparable);
            };

            case GREATER_THAN_OR_EQUALS -> (root, query, criteriaBuilder) -> {
                Comparable comparable = (Comparable) castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue());
                return criteriaBuilder.greaterThanOrEqualTo(root.get(input.getField()).as(comparable.getClass()), comparable);
            };

            case LESS_THAN -> (root, query, criteriaBuilder) -> {
                Comparable comparable = (Comparable) castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue());
                return criteriaBuilder.lessThan(root.get(input.getField()).as(comparable.getClass()), comparable);
            };

            case LESS_THAN_OR_EQUALS -> (root, query, criteriaBuilder) -> {
                Comparable comparable = (Comparable) castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue());
                return criteriaBuilder.lessThanOrEqualTo(root.get(input.getField()).as(comparable.getClass()), comparable);
            };

            case LIKE -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(input.getField())),
                            "%" + input.getValue().toLowerCase() + "%");

            case IN -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.in(root.get(input.getField()))
                            .value(castToRequiredType(root.get(input.getField()).getJavaType(), input.getValues()));
        };
    }

    private static Object castToRequiredType(Class fieldType, String value) {
        try {
            if (fieldType.isAssignableFrom(Double.class)) {
                return Double.valueOf(value);
            } else if (fieldType.isAssignableFrom(Float.class)) {
                return Float.valueOf(value);
            } else if (fieldType.isAssignableFrom(Integer.class)) {
                return Integer.valueOf(value);
            } else if (fieldType.isAssignableFrom(Long.class)) {
                return Long.valueOf(value);
            } else if (fieldType.isAssignableFrom(Short.class)) {
                return Short.valueOf(value);
            } else if (fieldType.isAssignableFrom(Byte.class)) {
                return Byte.valueOf(value);
            } else if (fieldType.isAssignableFrom(Boolean.class)) {
                return Boolean.valueOf(value);
            } else if (Enum.class.isAssignableFrom(fieldType)) {
                return Enum.valueOf(fieldType, value);
            } else if (fieldType.isAssignableFrom(String.class)) {
                return value;
            } else if (fieldType.isAssignableFrom(LocalDate.class)) {
                return LocalDate.parse(value);
            } else if (fieldType.isAssignableFrom(LocalDateTime.class)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
                return LocalDateTime.parse(value, formatter);
            } else {
                throw new IllegalArgumentException("Unsupported field type: " + fieldType);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to cast value: " + value + " to type: " + fieldType, e);
        }
    }

    private static Object castToRequiredType(Class fieldType, List<String> value) {
        List lists = new ArrayList();
        for (String s : value) {
            lists.add(castToRequiredType(fieldType, s));
        }
        return lists;
    }
}
