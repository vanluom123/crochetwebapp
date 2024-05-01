package org.crochet.controller;

import org.crochet.payload.request.EmailRequest;
import org.crochet.service.EmailSender;
import org.crochet.service.impl.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailController {
    final EmailSender emailSender;

    public EmailController(EmailService emailSender) {
        this.emailSender = emailSender;
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailRequest request) {
        emailSender.send(request);
        return "Email sent successfully";
    }
}