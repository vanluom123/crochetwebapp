package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.PatternFileMapper;
import org.crochet.model.PatternFile;
import org.crochet.repository.PatternFileRepository;
import org.crochet.repository.PatternRepository;
import org.crochet.response.PatternFileResponse;
import org.crochet.service.contact.PatternFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * PatternFileServiceImpl class
 */
@Service
public class PatternFileServiceImpl implements PatternFileService {
    private final PatternRepository patternRepository;

    private final PatternFileRepository patternFileRepository;

    /**
     * Constructs a new {@code PatternFileServiceImpl} with the specified repositories.
     *
     * @param patternRepository     The repository for handling patterns.
     * @param patternFileRepository The repository for handling pattern files.
     */
    public PatternFileServiceImpl(PatternRepository patternRepository,
                                  PatternFileRepository patternFileRepository) {
        this.patternRepository = patternRepository;
        this.patternFileRepository = patternFileRepository;
    }

    /**
     * Creates pattern files associated with a specified pattern.
     *
     * @param files     An array of {@link MultipartFile} objects representing the uploaded files.
     * @param patternId The identifier of the pattern to which the files will be associated.
     * @return A list of {@link PatternFileResponse} objects representing the created pattern files.
     * @throws ResourceNotFoundException If the specified patternId does not correspond to an existing pattern.
     * @throws RuntimeException          If there is an error while processing the files or saving them to the repository.
     */
    @Transactional
    @Override
    public List<PatternFileResponse> create(MultipartFile[] files, String patternId) {
        var pattern = patternRepository.findById(UUID.fromString(patternId))
                .orElseThrow(() -> new ResourceNotFoundException("Pattern not found"));
        // Map multipart file to pattern file
        List<PatternFile> patternFiles = Stream.of(files)
                .map(file -> {
                    var builder = PatternFile.builder();
                    builder.fileName(file.getOriginalFilename());
                    try {
                        builder.bytes(Base64.getEncoder().encodeToString(file.getBytes()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    builder.pattern(pattern);
                    return builder.build();
                }).toList();
        var results = patternFileRepository.saveAll(patternFiles);
        return PatternFileMapper.INSTANCE.toResponses(results);
    }
}
