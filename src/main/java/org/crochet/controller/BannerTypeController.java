package org.crochet.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.constant.MessageConstant;
import org.crochet.payload.request.BannerTypeRequest;
import org.crochet.payload.response.BannerTypeResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.service.BannerTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.crochet.constant.AppConstant.SUCCESS;

@RestController
@RequestMapping("/api/v1/banner-types")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "BearerAuth")
public class BannerTypeController {
    final BannerTypeService bannerTypeService;

    public BannerTypeController(BannerTypeService bannerTypeService) {
        this.bannerTypeService = bannerTypeService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/createOrUpdate")
    public ResponseData<BannerTypeResponse> createOrUpdate(@RequestBody BannerTypeRequest request) {
        var res = bannerTypeService.createOrUpdate(request);
        return ResponseData.<BannerTypeResponse>builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message(SUCCESS)
                .data(res)
                .build();
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public ResponseData<String> delete(@RequestParam("id") String id) {
        bannerTypeService.delete(id);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(MessageConstant.MSG_DELETE_SUCCESS)
                .build();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseData<List<BannerTypeResponse>> getAll() {
        var res = bannerTypeService.getAll();
        return ResponseData.<List<BannerTypeResponse>>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(res)
                .build();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseData<BannerTypeResponse> getById(@PathVariable("id") String id) {
        var res = bannerTypeService.getById(id);
        return ResponseData.<BannerTypeResponse>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(res)
                .build();
    }
}
