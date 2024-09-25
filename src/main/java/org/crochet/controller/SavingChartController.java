package org.crochet.controller;

import org.crochet.payload.request.SavingChartRequest;
import org.crochet.service.SavingChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saving-chart")
public class SavingChartController {

    @Autowired
    private SavingChartService savingChartService;

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public String createSavingChart(@RequestBody SavingChartRequest request) {
        savingChartService.createChart(request);
        return "Create success";
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/delete/{id}")
    public String deleteSavingChart(@PathVariable String id) {
        savingChartService.deleteChartById(id);
        return "Delete success";
    }
}
