package org.crochet.controller;

import org.crochet.constant.AppConstant;
import org.crochet.request.PatternRequest;
import org.crochet.response.PatternPaginationResponse;
import org.crochet.response.PatternResponse;
import org.crochet.service.abstraction.FirebaseService;
import org.crochet.service.abstraction.PatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/pattern")
public class PatternController {
  @Autowired
  private PatternService patternService;

  @Autowired
  private FirebaseService firebaseService;


  @PostMapping("/create")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> createPattern(@RequestParam("filePath") String filePath,
                                              @RequestBody PatternRequest request) {
    var byteData = firebaseService.getImage(filePath);
    var image = Base64.getEncoder().encodeToString(byteData);
    request.setImage(image);
    patternService.createOrUpdate(request);
    return ResponseEntity.ok("Create pattern successfully");
  }

  @GetMapping("/pagination")
  public ResponseEntity<PatternPaginationResponse> getPatterns(
      @RequestParam(value = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
      @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize,
      @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
      @RequestParam(value = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,
          required = false) String sortDir,
      @RequestParam(value = "text", required = false) String text) {
    var response = patternService.getPatterns(pageNo, pageSize, sortBy, sortDir, text);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/detail")
  public ResponseEntity<PatternResponse> getDetail(@RequestParam("id") long id) {
    return ResponseEntity.ok(patternService.getDetail(id));
  }
}
