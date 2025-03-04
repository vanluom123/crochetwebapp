package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.crochet.payload.request.UserProfileRequest;
import org.crochet.payload.response.ResponseData;
import org.crochet.payload.response.UserProfileResponse;
import org.crochet.service.UserProfileService;
import org.crochet.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user-profile")
@ResponseBody
public class UserProfileController {

    private final UserProfileService userProfileService;

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user profile")
    @ApiResponse(responseCode = "200", description = "User profile",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserProfileResponse.class)))
    @GetMapping
    public ResponseData<UserProfileResponse> loadUserProfile(@RequestParam("userId") String userId) {
        var res = userProfileService.loadUserProfile(userId);
        return ResponseUtil.success(res, "Load user profile success");
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user profile")
    @ApiResponse(responseCode = "200", description = "User profile updated",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserProfileResponse.class)))
    @PutMapping
    public ResponseData<UserProfileResponse> updateUserProfile(@RequestBody UserProfileRequest request) {
        var res = userProfileService.updateUserProfile(request);
        return ResponseUtil.success(res, "Update user profile success");
    }
}
