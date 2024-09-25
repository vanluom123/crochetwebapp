package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.payload.request.SavingChartRequest;
import org.crochet.repository.FreePatternRepository;
import org.crochet.repository.SavingChartRepo;
import org.crochet.repository.UserRepository;
import org.crochet.service.SavingChartService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingChartServiceImpl implements SavingChartService {
    private final SavingChartRepo savingChartRepo;
    private final UserRepository userRepository;
    private final FreePatternRepository freePatternRepository;

    @Override
    public void createChart(SavingChartRequest chartRequest) {
        String userId = chartRequest.getUserId();
        if (!userRepository.isValidUser(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        String freePatternId = chartRequest.getFreePatternId();
        if (!freePatternRepository.existsById(freePatternId)) {
            throw new ResourceNotFoundException("Free pattern not found");
        }
        try {
            if (savingChartRepo.existsByUserIdAndFreePatternId(userId, freePatternId)) {
                savingChartRepo.deleteByUserIdAndFreePatternId(userId, freePatternId);
            } else {
                savingChartRepo.createSavingChart(freePatternId, userId);
            }
        } catch (Exception e) {
            log.error("Failed to create saving chart");
        }
    }

    @Override
    public void deleteChartById(String id) {
        try {
            savingChartRepo.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete saving chart");
        }
    }
}
