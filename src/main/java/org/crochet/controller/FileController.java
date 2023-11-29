package org.crochet.controller;

import org.crochet.response.ApiResponse;
import org.crochet.service.contact.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FirebaseService firebaseService;

    @PostMapping("/upload-single-file")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> uploadFile(@RequestPart("file") MultipartFile file) {
        String fileName = firebaseService.uploadFile(file);
        return ResponseEntity.ok(new ApiResponse(true, "Upload file successfully"));
    }

    @PostMapping("/upload-multiple-files")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> uploadMultipleFiles(@RequestPart("files") MultipartFile[] files) {
        var fileNames = firebaseService.uploadFiles(files);
        return ResponseEntity.ok(fileNames);
    }

    @GetMapping("/get-file")
    public ResponseEntity<byte[]> getFile(@RequestParam("fileName") String fileName) {
        var file = firebaseService.getFile(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(file);
    }
}
