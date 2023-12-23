package org.crochet.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.crochet.payload.dto.OrderResponseDTO;
import org.crochet.service.contact.OrderPatternService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/checkout/order-pattern")
public class CheckOutOrderPatternController {
    private final OrderPatternService orderPatternService;

    public CheckOutOrderPatternController(OrderPatternService orderPatternService) {
        this.orderPatternService = orderPatternService;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createPayment(@RequestParam("patternId") String patternId) {
        var response = orderPatternService.createPayment(patternId);
        var location = response.getLinkAsMap().get("approve");
        return ResponseEntity.created(URI.create(location))
                .build();
    }

    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(HttpServletRequest httpServletRequest) {
        var transactionId = httpServletRequest.getParameter("token");
        var response = orderPatternService.capturePayment(transactionId);
        return ResponseEntity.ok(response);
    }
}
