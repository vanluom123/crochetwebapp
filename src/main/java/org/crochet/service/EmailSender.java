package org.crochet.service;

public interface EmailSender {
    void send(String to, String subject, String content);
}
