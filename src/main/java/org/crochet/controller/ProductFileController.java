package org.crochet.controller;

import org.crochet.response.ProductFileResponse;
import org.crochet.service.contact.FirebaseService;
import org.crochet.service.contact.ProductFileService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/product-file")
public class ProductFileController {
    @Autowired
    private ProductFileService productFileService;

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductFileResponse>> create(@RequestPart MultipartFile[] files,
                                                            @RequestParam("productId") String productId) {
        var fileNames = firebaseService.uploadFiles(files);
        var responses = productFileService.create(fileNames, productId);
        return ResponseEntity.ok(responses);
    }
}
