package org.crochet.service;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailValidator implements Predicate<String> {
  private static final String EMAIL_REGEX_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

  @Override
  public boolean test(String s) {
    Pattern pattern = Pattern.compile(EMAIL_REGEX_PATTERN);
    Matcher matcher = pattern.matcher(s);
    return matcher.matches();
  }
}
