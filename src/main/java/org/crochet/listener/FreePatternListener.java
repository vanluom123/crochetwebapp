package org.crochet.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.crochet.model.FreePattern;
import org.crochet.repository.SavingChartRepo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FreePatternListener {
    private final SavingChartRepo scRepo;

    @PrePersist
    @PreUpdate
    public void setIsSaved(FreePattern freePattern) {
        boolean exist = scRepo.existsByFreePatternId(freePattern.getId());
        freePattern.setSaved(exist);
    }
}
