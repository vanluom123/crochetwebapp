package org.crochet.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.payload.request.BannerRequest;
import org.crochet.payload.response.BannerResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.BannerService;
import org.crochet.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banners")
public class BannerController {
    final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseData<List<BannerResponse>> batchInsertOrUpdate(@RequestBody List<BannerRequest> requests) {
        var res = bannerService.batchInsertOrUpdate(requests);
        return ResponseUtil.success(res, HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseData<List<BannerResponse>> getAll() {
        var res = bannerService.getAll();
        return ResponseUtil.success(res);
    }
}
