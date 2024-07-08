package org.crochet.repository;


import lombok.Getter;
import lombok.Setter;
import org.crochet.enumerator.FilterLogic;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FilterNode {
    private FilterLogic logic;
    private FilterCriteria criteria;
    private List<FilterNode> children;

    public FilterNode(FilterLogic logic) {
        this.logic = logic;
        this.children = new ArrayList<>();
    }

    public FilterNode(FilterCriteria criteria) {
        this.criteria = criteria;
    }

    public void addAllChildren(List<FilterNode> children) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.addAll(children);
    }
}
