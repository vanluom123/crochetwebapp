package org.crochet.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.security.CurrentUser;
import org.crochet.security.UserPrincipal;
import org.crochet.service.SavingChartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/saving-free-pattern")
public class SavingChartController {

    private final SavingChartService savingChartService;

    public SavingChartController(SavingChartService savingChartService) {
        this.savingChartService = savingChartService;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public String createSavingChart(@CurrentUser UserPrincipal currentUser,
                                    @RequestParam("free_pattern_id") String freePatternId) {
        savingChartService.saveChart(currentUser, freePatternId);
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
