package org.crochet.service;

import org.crochet.payload.request.SavingChartRequest;

public interface SavingChartService {

    void createChart(SavingChartRequest chartRequest);

    void deleteChartById(String id);
}
