package org.crochet.service.abstraction;

public interface EmailSender {
  void send(String to, String subject, String content);
}
