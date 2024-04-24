package org.crochet.controller;

import lombok.RequiredArgsConstructor;
import org.crochet.payload.response.SettingResponse;
import org.crochet.service.BannerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setting")
@RequiredArgsConstructor
public class SettingController {
    private final BannerService bannerService;

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping
    public SettingResponse getSetting() {
        var banners = bannerService.getAll();
        return SettingResponse.builder()
                .banners(banners)
                .build();
    }
}
