package org.crochet.service.contact;

public interface EmailSender {
  void send(String to, String subject, String content);
}
