package org.crochet.service;

import org.crochet.security.UserPrincipal;

public interface SavingChartService {

    void saveChart(UserPrincipal principal, String freePatternId);

    void deleteChartById(String id);
}
