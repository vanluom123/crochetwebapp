package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.payload.response.FileResponse;
import org.crochet.service.FirebaseStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/firebase-storage")
public class FirebaseStorageController {
    private final FirebaseStorageService firebaseStorageService;

    public FirebaseStorageController(FirebaseStorageService firebaseStorageService) {
        this.firebaseStorageService = firebaseStorageService;
    }

    @Operation(summary = "Upload multiple files to Firebase Cloud Storage")
    @ApiResponse(responseCode = "200", description = "Upload files successfully", content = @Content(mediaType = "application/json"))
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping(value = "/upload-file", consumes = {"multipart/form-data"})
    public ResponseEntity<List<FileResponse>> uploadMultipleFiles(@RequestPart("files") MultipartFile[] files) {
        var fileResponses = firebaseStorageService.uploadMultipleFiles(files);
        return ResponseEntity.ok(fileResponses);
    }

    @Operation(summary = "Delete multiple files from Firebase Cloud Storage")
    @ApiResponse(responseCode = "200", description = "If all files are deleted, return empty. Otherwise, return files are not deleted.", content = @Content(mediaType = "application/json"))
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping(value = "/delete-multiple-files")
    public ResponseEntity<List<String>> deleteMultipleFiles(@RequestBody List<String> fileNames) {
        var response = firebaseStorageService.deleteMultipleFiles(fileNames);
        return ResponseEntity.ok(response);
    }
}
