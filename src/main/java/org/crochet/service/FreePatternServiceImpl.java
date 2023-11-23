package org.crochet.service;

import org.crochet.repository.FreePatternRepository;
import org.crochet.service.abstraction.FreePatternService;
import org.springframework.stereotype.Service;

@Service
public class FreePatternServiceImpl implements FreePatternService {

  private final FreePatternRepository freePatternRepository;

  public FreePatternServiceImpl(FreePatternRepository freePatternRepository) {
    this.freePatternRepository = freePatternRepository;
  }


}
