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
import org.crochet.payload.response.CollectionResponse;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.payload.response.UserResponse;
import org.crochet.service.CollectionService;
import org.crochet.service.FreePatternService;
import org.crochet.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.crochet.constant.AppConstant.SUCCESS;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final FreePatternService freePatternService;
    private final CollectionService collectionService;

    public UserController(UserService userService,
                          FreePatternService freePatternService,
                          CollectionService collectionService) {
        this.userService = userService;
        this.freePatternService = freePatternService;
        this.collectionService = collectionService;
    }

    @Operation(summary = "Get paginated list of users")
    @ApiResponse(responseCode = "200", description = "List of users",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaginationResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    public ResponseData<PaginationResponse<UserResponse>> getAll(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "offset", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER, required = false) int offset,
            @Parameter(description = "Page size (default: 48)")
            @RequestParam(value = "limit", defaultValue = AppConstant.DEFAULT_PAGE_SIZE, required = false) int limit,
            @Parameter(description = "Sort by field (default: createdDate)")
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction (default: DESC)")
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @Parameter(description = "The list of filters") @RequestBody(required = false) Filter[] filters) {
        var response = userService.getAll(offset, limit, sortBy, sortDir, filters);
        return ResponseData.<PaginationResponse<UserResponse>>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(response)
                .build();
    }

    @Operation(summary = "Get paginated free patterns by user",
            description = "Allows users and admins to fetch paginated free patterns associated with a specific user.")
    @ApiResponse(responseCode = "200",
            description = "Paginated free patterns retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaginationResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{userId}/free-pattern")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    public ResponseData<PaginationResponse<FreePatternResponse>> getFreePatternByUser(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @Parameter(description = "Page size (default: 48)")
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @Parameter(description = "Sort by field (default: createdDate)")
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction (default: DESC)")
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @Parameter(description = "The list of filters")
            @RequestBody(required = false) Filter[] filters,
            @Parameter(description = "User ID") @PathVariable("userId") String userId) {
        var res = freePatternService.getAllByUser(pageNo, pageSize, sortBy, sortDir, filters, userId);
        return ResponseData.<PaginationResponse<FreePatternResponse>>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(res)
                .build();
    }

    @Operation(summary = "Update a user's details",
            description = "Allows admins to update user details by providing the necessary information.")
    @ApiResponse(responseCode = "200",
            description = "User updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class)))
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<String> updateUser(@RequestBody UserUpdateRequest request) {
        userService.updateUser(request);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("User updated successfully")
                .build();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<UserResponse> getDetail(@Parameter(description = "User ID") @PathVariable("id") String id) {
        var user = userService.getDetail(id);
        return ResponseData.<UserResponse>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(user)
                .build();
    }

    @Operation(summary = "Delete a user", description = "Allows admins to delete a specific user by ID.")
    @ApiResponse(responseCode = "200",
            description = "User deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class)))
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<String> deleteUser(@RequestParam("id") String id) {
        userService.deleteUser(id);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("User deleted successfully")
                .build();
    }

    @Operation(summary = "Delete multiple users",
            description = "Allows admins to delete multiple users by providing a list of user IDs.")
    @ApiResponse(responseCode = "200", description = "Users deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class)))
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/bulk")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<String> deleteMultipleUsers(@RequestBody List<String> ids) {
        userService.deleteMultipleUsers(ids);
        return ResponseData.<String>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Users deleted successfully")
                .build();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}/collections")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    public ResponseData<List<CollectionResponse>> getCollections(@PathVariable("userId") String userId) {
        var res = collectionService.getAllByUserId(userId);
        return ResponseData.<List<CollectionResponse>>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(res)
                .build();
    }
}
