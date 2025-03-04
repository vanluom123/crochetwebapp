package org.crochet.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.crochet.enums.ResultCode;
import org.crochet.payload.request.SettingRequest;
import org.crochet.payload.response.ResponseData;
import org.crochet.payload.response.SettingResponse;
import org.crochet.service.SettingService;
import org.crochet.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@ResponseBody
public class SettingController {
    private final SettingService settingService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<SettingResponse> getSetting() {
        return settingService.getAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public ResponseData<String> create(@RequestBody SettingRequest request) {
        settingService.create(request);
        return ResponseUtil.success(ResultCode.MSG_CREATE_OR_UPDATE_SUCCESS.message());
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public ResponseData<String> update(@RequestBody SettingRequest request) {
        settingService.update(request);
        return ResponseUtil.success(ResultCode.MSG_CREATE_OR_UPDATE_SUCCESS.message());
    }
}
