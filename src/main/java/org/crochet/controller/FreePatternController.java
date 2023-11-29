package org.crochet.controller;

import org.crochet.constant.AppConstant;
import org.crochet.request.FreePatternRequest;
import org.crochet.response.FreePatternResponse;
import org.crochet.response.PaginatedFreePatternResponse;
import org.crochet.service.FirebaseService;
import org.crochet.service.FreePatternService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/free-pattern")
public class FreePatternController {
    private final FreePatternService freePatternService;

    private final FirebaseService firebaseService;

    /**
     * Constructor
     *
     * @param freePatternService FreePatternService
     * @param firebaseService    FirebaseService
     */
    public FreePatternController(FreePatternService freePatternService,
                                 FirebaseService firebaseService) {
        this.freePatternService = freePatternService;
        this.firebaseService = firebaseService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createPattern(@RequestBody FreePatternRequest request) {
        freePatternService.createOrUpdate(request);
        return ResponseEntity.ok("Create pattern successfully");
    }

    @GetMapping("/pagination")
    public ResponseEntity<PaginatedFreePatternResponse> getPatterns(
            @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir,
            @RequestParam(value = "text", required = false) String text) {
        var response = freePatternService.getFreePatterns(pageNo, pageSize, sortBy, sortDir, text);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail")
    public ResponseEntity<FreePatternResponse> getDetail(@RequestParam("id") long id) {
        return ResponseEntity.ok(freePatternService.getDetail(id));
    }
}
