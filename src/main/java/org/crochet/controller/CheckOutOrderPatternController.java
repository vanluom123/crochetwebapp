package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.crochet.client.paypal.PaymentOrder;
import org.crochet.service.contact.OrderPatternService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout/order-pattern")
public class CheckOutOrderPatternController {
    private final OrderPatternService orderPatternService;

    public CheckOutOrderPatternController(OrderPatternService orderPatternService) {
        this.orderPatternService = orderPatternService;
    }

    @Operation(summary = "Create payment")
    @ApiResponse(responseCode = "201", description = "Payment created successfully",
            content = @Content(mediaType = "application/json"))
    @PostMapping("/create")
    public ResponseEntity<PaymentOrder> createPayment(
            @Parameter(description = "ID of the pattern for which payment is being created")
            @RequestParam("patternId") String patternId) {
        var response = orderPatternService.createPayment(patternId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Handle payment success")
    @ApiResponse(responseCode = "200", description = "Payment success",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(HttpServletRequest httpServletRequest) {
        var transactionId = httpServletRequest.getParameter("token");
        var response = orderPatternService.completePayment(transactionId);
        return ResponseEntity.ok(response);
    }
}
