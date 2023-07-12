package org.crochet.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiquibaseProperties {
    private String changeLog;
}
