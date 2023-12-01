package org.crochet.service.contact;

import org.crochet.response.PatternFileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PatternFileService {
    List<PatternFileResponse> create(MultipartFile[] files, String patternId);
}
