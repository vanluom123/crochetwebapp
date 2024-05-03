package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.constant.AppConstant;
import org.crochet.payload.request.UserUpdateRequest;
import org.crochet.payload.response.UserPaginationResponse;
import org.crochet.payload.response.UserResponse;
import org.crochet.repository.Filter;
import org.crochet.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('ADMIN')")
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
            @Parameter(description = "Search name or email address (default: null)")
            @RequestParam(value = "searchText", required = false) String searchText,
            @Parameter(description = "List of filters")
            @RequestBody(required = false) List<Filter> filters) {
        var response = userService.getAll(pageNo, pageSize, sortBy, sortDir, searchText, filters);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserUpdateRequest request) {
        userService.updateUser(request);
        return ResponseEntity.ok("User updated successfully");
    }

    @GetMapping("/detail")
    public ResponseEntity<UserResponse> getDetail(@Parameter(description = "User ID")
                                                  @RequestParam String id) {
        var user = userService.getDetail(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestParam String id) {
        userService.deleteUser(id);
    }
}
