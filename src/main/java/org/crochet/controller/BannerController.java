package org.crochet.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.payload.request.BannerRequest;
import org.crochet.payload.response.BannerResponse;
import org.crochet.service.BannerService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/banner")
public class BannerController {
    final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @PostMapping("/batchInsertOrUpdate")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public List<BannerResponse> batchInsertOrUpdate(@RequestBody List<BannerRequest> requests) {
        return bannerService.batchInsertOrUpdate(requests);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/getAll")
    public List<BannerResponse> getAll() {
        return bannerService.getAll();
    }
}
