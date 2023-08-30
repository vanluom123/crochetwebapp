package org.crochet.service;

import org.crochet.repository.PatternRepository;
import org.crochet.service.abstraction.PatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatternServiceImpl implements PatternService {
  @Autowired
  private PatternRepository patternRepository;

}
