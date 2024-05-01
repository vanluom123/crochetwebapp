package org.crochet.service;

import org.crochet.payload.request.EmailRequest;

public interface EmailSender {
    void send(String to, String subject, String content);

    void send(EmailRequest emailRequest);
}
