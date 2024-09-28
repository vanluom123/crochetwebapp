package org.crochet.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.User;
import org.crochet.repository.FreePatternRepository;
import org.crochet.repository.SavingChartRepo;
import org.crochet.repository.UserRepository;
import org.crochet.security.UserPrincipal;
import org.crochet.service.SavingChartService;
import org.springframework.stereotype.Service;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.MSG_FREE_PATTERN_NOT_FOUND;
import static org.crochet.constant.MessageConstant.MSG_USER_LOGIN_REQUIRED;
import static org.crochet.constant.MessageConstant.MSG_USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingChartServiceImpl implements SavingChartService {
    private final SavingChartRepo savingChartRepo;
    private final UserRepository userRepository;
    private final FreePatternRepository freePatternRepository;

    @Transactional
    @Override
    public void saveChart(UserPrincipal principal, String freePatternId) {
        if (principal == null) {
            throw new ResourceNotFoundException(MSG_USER_LOGIN_REQUIRED, MAP_CODE.get(MSG_USER_LOGIN_REQUIRED));
        }
        User user = userRepository.findById(principal.getId()).orElseThrow(
                () -> new ResourceNotFoundException(MSG_USER_NOT_FOUND, MAP_CODE.get(MSG_USER_NOT_FOUND))
        );

        if (!freePatternRepository.existsById(freePatternId)) {
            throw new ResourceNotFoundException(MSG_FREE_PATTERN_NOT_FOUND, MAP_CODE.get(MSG_FREE_PATTERN_NOT_FOUND));
        }

        try {
            if (savingChartRepo.existsByUserIdAndFreePatternId(user.getId(), freePatternId)) {
                savingChartRepo.deleteByUserIdAndFreePatternId(user.getId(), freePatternId);
                freePatternRepository.updateSaved(freePatternId, false);
            } else {
                savingChartRepo.createSavingChart(freePatternId, user.getId());
                freePatternRepository.updateSaved(freePatternId, true);
            }
        } catch (Exception e) {
            log.error("Failed to create saving chart");
        }
    }

    @Transactional
    @Override
    public void deleteChartById(String id) {
        try {
            savingChartRepo.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete saving chart");
        }
    }
}
