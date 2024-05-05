package org.crochet.enumerator;

import java.util.EnumSet;

public enum QueryOperator {
    GREATER_THAN,
    GREATER_THAN_OR_EQUALS,
    LESS_THAN,
    LESS_THAN_OR_EQUALS,
    EQUALS,
    LIKE,
    NOT_EQUALS,
    IN;
    public static final EnumSet<QueryOperator> OPERATORS = EnumSet.allOf(QueryOperator.class);
}
