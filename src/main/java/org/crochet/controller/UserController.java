package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.constant.AppConstant;
import org.crochet.payload.request.Filter;
import org.crochet.payload.request.UserUpdateRequest;
import org.crochet.payload.response.ResponseData;
import org.crochet.payload.response.UserPaginationResponse;
import org.crochet.payload.response.UserResponse;
import org.crochet.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.crochet.constant.AppConstant.SUCCESS;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get paginated list of users")
    @ApiResponse(responseCode = "200", description = "List of users",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserPaginationResponse.class)))
    @PostMapping("/pagination")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserPaginationResponse> getAll(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @Parameter(description = "Page size (default: 10)")
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @Parameter(description = "Sort by field (default: id)")
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction (default: ASC)")
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir,
            @Parameter(description = "The list of filters")
            @RequestBody(required = false) Filter[] filters) {
        var response = userService.getAll(pageNo, pageSize, sortBy, sortDir, filters);
        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/update")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<String> updateUser(@RequestBody UserUpdateRequest request) {
        userService.updateUser(request);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data("User updated successfully")
                .build();
    }

    @GetMapping("/detail")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getDetail(@Parameter(description = "User ID")
                                                  @RequestParam String id) {
        var user = userService.getDetail(id);
        return ResponseEntity.ok(user);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/delete")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<String> deleteUser(@RequestParam String id) {
        userService.deleteUser(id);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data("User deleted successfully")
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/delete-multiple")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<String> deleteMultipleUsers(@RequestBody List<String> ids) {
        userService.deleteMultipleUsers(ids);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data("Users deleted successfully")
                .build();
    }
}
