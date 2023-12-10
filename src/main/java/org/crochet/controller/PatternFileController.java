package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.response.PatternFileResponse;
import org.crochet.service.contact.PatternFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/pattern-file")
public class PatternFileController {
    private final PatternFileService patternFileService;

    public PatternFileController(PatternFileService patternFileService) {
        this.patternFileService = patternFileService;
    }

    @Operation(summary = "Create pattern files")
    @ApiResponse(responseCode = "200", description = "Pattern files created successfully",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<List<PatternFileResponse>> create(
            @RequestPart("files") MultipartFile[] files,
            @Parameter(description = "ID of the pattern to associate the files with")
            @RequestParam("patternId") String patternId) {
        var responses = patternFileService.create(files, patternId);
        return ResponseEntity.ok(responses);
    }
}
