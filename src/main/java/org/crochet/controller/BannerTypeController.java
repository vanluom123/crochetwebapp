package org.crochet.controller;

import lombok.RequiredArgsConstructor;
import org.crochet.payload.request.BannerTypeRequest;
import org.crochet.payload.response.BannerTypeResponse;
import org.crochet.service.BannerTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bannerType")
@RequiredArgsConstructor
public class BannerTypeController {
    private final BannerTypeService bannerTypeService;

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/createOrUpdate")
    public BannerTypeResponse createOrUpdate(@RequestBody BannerTypeRequest request) {
        return bannerTypeService.createOrUpdate(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        bannerTypeService.delete(id);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/getAll")
    public List<BannerTypeResponse> getAll() {
        return bannerTypeService.getAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/getById/{id}")
    public BannerTypeResponse getById(@PathVariable UUID id) {
        return bannerTypeService.getById(id);
    }
}
