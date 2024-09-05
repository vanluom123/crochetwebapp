package org.crochet.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.enumerator.FilterLogic;
import org.crochet.repository.FilterCriteria;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Filter {
    private FilterLogic filterLogic;
    private List<FilterCriteria> filterCriteria;
}
