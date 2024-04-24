package org.crochet.controller;

import lombok.RequiredArgsConstructor;
import org.crochet.payload.request.BannerRequest;
import org.crochet.payload.response.BannerResponse;
import org.crochet.service.BannerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/banner")
@RequiredArgsConstructor
public class BannerController {
    private final BannerService bannerService;

    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @PostMapping("/createOrUpdate")
    public BannerResponse createOrUpdate(@RequestBody BannerRequest request) {
        return bannerService.createOrUpdateBanner(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        bannerService.delete(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/getById/{id}")
    public BannerResponse getById(@PathVariable UUID id) {
        return bannerService.getById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/getAll")
    public List<BannerResponse> getAll() {
        return bannerService.getAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/getAllByType/{bannerTypeId}")
    public List<BannerResponse> getAllByType(@PathVariable UUID bannerTypeId) {
        return bannerService.getAllByType(bannerTypeId);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/getAllByType")
    public List<BannerResponse> getAllByType(@RequestParam String bannerTypeName) {
        return bannerService.getAllByType(bannerTypeName);
    }
}
