package org.crochet.repository;

import org.crochet.model.BaseEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

import static org.crochet.enumerator.QueryOperator.OPERATORS;
import static org.springframework.data.jpa.domain.Specification.where;

public class Specifications {
    public static <T extends BaseEntity> Specification<T> getSpecificationFromFilters(List<Filter> filters) {
        Specification<T> spec = where(null);
        if (filters == null || filters.isEmpty()) {
            return spec;
        }
        for (Filter input : filters) {
            if (input.getField() == null || input.getField().isEmpty()) {
                continue;
            }
            if (input.getOperator() == null || !OPERATORS.contains(input.getOperator())) {
                continue;
            }
            if (input.getValue() == null || input.getValue().isEmpty()) {
                if (ObjectUtils.isEmpty(input.getValues())) {
                    continue;
                }
            }
            spec = spec.and(createSpecification(input));
        }
        return spec;
    }

    private static <T extends BaseEntity> Specification<T> createSpecification(Filter input) {
        return switch (input.getOperator()) {
            case EQUALS -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(input.getField()),
                            castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue()));
            case NOT_EQUALS -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.notEqual(root.get(input.getField()),
                            castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue()));
            case GREATER_THAN -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.gt(root.get(input.getField()),
                            (Number) castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue()));
            case LESS_THAN -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.lt(root.get(input.getField()),
                            (Number) castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue()));
            case LIKE -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(input.getField()), "%" + input.getValue() + "%");
            case IN -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.in(root.get(input.getField()))
                            .value(castToRequiredType(root.get(input.getField()).getJavaType(), input.getValues()));
        };
    }

    private static <E extends Enum<E>> Object castToRequiredType(Class<?> fieldType, String value) {
        try {
            if (fieldType.isAssignableFrom(Double.class)) {
                return Double.valueOf(value);
            } else if (fieldType.isAssignableFrom(Integer.class)) {
                return Integer.valueOf(value);
            } else if (fieldType.isAssignableFrom(Long.class)) {
                return Long.valueOf(value);
            } else if (fieldType.isAssignableFrom(Boolean.class)) {
                return Boolean.valueOf(value);
            } else if (Enum.class.isAssignableFrom(fieldType)) {
                @SuppressWarnings("unchecked")
                Class<E> enumType = (Class<E>) fieldType;
                return Enum.valueOf(enumType, value);
            } else if (fieldType.isAssignableFrom(String.class)) {
                return value;
            } else {
                throw new IllegalArgumentException("Unsupported field type: " + fieldType);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to cast value: " + value + " to type: " + fieldType, e);
        }
    }

    private static Object castToRequiredType(Class<?> fieldType, List<String> value) {
        List<Object> lists = new ArrayList<>();
        for (String s : value) {
            lists.add(castToRequiredType(fieldType, s));
        }
        return lists;
    }
}
