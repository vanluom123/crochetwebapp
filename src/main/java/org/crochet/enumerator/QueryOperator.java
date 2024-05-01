package org.crochet.enumerator;

import java.util.EnumSet;

public enum QueryOperator {
    GREATER_THAN,
    LESS_THAN,
    EQUALS,
    LIKE,
    NOT_EQUALS,
    IN;
    public static final EnumSet<QueryOperator> OPERATORS = EnumSet.allOf(QueryOperator.class);
}
