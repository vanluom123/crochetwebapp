package org.crochet.controller;

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

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PatternFileResponse>> create(@RequestPart MultipartFile[] files,
                                                            @RequestParam("patternId") String patternId) {
        var responses = patternFileService.create(files, patternId);
        return ResponseEntity.ok(responses);
    }
}
