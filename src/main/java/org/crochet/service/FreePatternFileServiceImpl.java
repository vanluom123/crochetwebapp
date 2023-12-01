package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FreePatternFileMapper;
import org.crochet.model.FreePatternFile;
import org.crochet.repository.FreePatternFileRepository;
import org.crochet.repository.FreePatternRepository;
import org.crochet.response.FreePatternFileResponse;
import org.crochet.service.contact.FreePatternFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * FreePatternFileServiceImpl class
 */
@Service
public class FreePatternFileServiceImpl implements FreePatternFileService {
    private final FreePatternFileRepository freePatternFileRepository;

    private final FreePatternRepository freePatternRepository;

    /**
     * Constructs a new {@code FreePatternFileServiceImpl} with the specified repositories.
     *
     * @param freePatternFileRepository The repository for handling FreePattern files.
     * @param freePatternRepository     The repository for handling FreePatterns.
     */
    public FreePatternFileServiceImpl(FreePatternFileRepository freePatternFileRepository,
                                      FreePatternRepository freePatternRepository) {
        this.freePatternFileRepository = freePatternFileRepository;
        this.freePatternRepository = freePatternRepository;
    }

    /**
     * Creates FreePattern files associated with a specified FreePattern.
     *
     * @param files         An array of {@link MultipartFile} objects representing the uploaded files.
     * @param freePatternId The identifier of the FreePattern to which the files will be associated.
     * @return A list of {@link FreePatternFileResponse} objects representing the created FreePattern files.
     * @throws ResourceNotFoundException If the specified freePatternId does not correspond to an existing FreePattern.
     * @throws RuntimeException          If there is an error while processing the files or saving them to the repository.
     */
    public List<FreePatternFileResponse> create(MultipartFile[] files, String freePatternId) {
        var freePattern = freePatternRepository.findById(UUID.fromString(freePatternId))
                .orElseThrow(() -> new ResourceNotFoundException("Free pattern not found"));

        var freePatternFiles = Stream.of(files)
                .map(file -> {
                    var builder = FreePatternFile.builder();
                    builder.fileName(file.getOriginalFilename());
                    try {
                        builder.bytes(Base64.getEncoder().encodeToString(file.getBytes()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    builder.freePattern(freePattern);
                    return builder.build();
                }).toList();
        var results = freePatternFileRepository.saveAll(freePatternFiles);
        return FreePatternFileMapper.INSTANCE.toResponses(results);
    }
}
