package org.crochet.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.crochet.payload.request.SettingRequest;
import org.crochet.payload.response.SettingResponse;
import org.crochet.service.SettingService;
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
@RequestMapping("/setting")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class SettingController {
    private final SettingService settingService;

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping
    public List<SettingResponse> getSetting() {
        return settingService.getAll();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/create")
    public String create(@RequestBody SettingRequest request) {
        settingService.create(request);
        return "Create success";
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/update")
    public String update(@RequestBody SettingRequest request) {
        settingService.update(request);
        return "Update success";
    }
}
