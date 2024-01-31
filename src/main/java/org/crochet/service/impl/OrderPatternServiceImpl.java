package org.crochet.service.impl;

import org.crochet.client.paypal.PaymentOrder;
import org.crochet.enumerator.OrderStatus;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.Order;
import org.crochet.model.OrderPatternDetail;
import org.crochet.model.User;
import org.crochet.repository.OrderPatternDetailRepository;
import org.crochet.repository.OrderRepository;
import org.crochet.repository.PatternRepository;
import org.crochet.repository.UserRepository;
import org.crochet.security.UserPrincipal;
import org.crochet.service.OrderPatternService;
import org.crochet.service.PayPalService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class OrderPatternServiceImpl implements OrderPatternService {
    private final PayPalService payPalService;
    private final UserRepository userRepository;
    private final PatternRepository patternRepository;
    private final OrderRepository orderRepository;
    private final OrderPatternDetailRepository orderPatternDetailRepository;

    public OrderPatternServiceImpl(PayPalService payPalService,
                                   UserRepository userRepository,
                                   PatternRepository patternRepository,
                                   OrderRepository orderRepository,
                                   OrderPatternDetailRepository orderPatternDetailRepository) {
        this.payPalService = payPalService;
        this.userRepository = userRepository;
        this.patternRepository = patternRepository;
        this.orderRepository = orderRepository;
        this.orderPatternDetailRepository = orderPatternDetailRepository;
    }

    @Transactional
    @Override
    public PaymentOrder createPayment(String patternId) {
        // Get the current authentication object from the security context
        var auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authentication object is null or if the principal is not an instance of UserPrincipal
        // If either condition is true, throw a ResourceNotFoundException
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ResourceNotFoundException("Please login to order pattern");
        }

        // Fetch the user from the database using the ID from the UserPrincipal
        // If the user is not found, throw a ResourceNotFoundException
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Fetch the pattern from the database using the provided pattern ID
        // If the pattern is not found, throw a ResourceNotFoundException
        var pattern = patternRepository.findById(UUID.fromString(patternId))
                .orElseThrow(() -> new ResourceNotFoundException("Pattern not found"));

        // Create a payment order using the price of the pattern
        var paymentOrder = payPalService.createPayment(pattern.getPrice());

        // Build a new order object using the fetched user
        var order = Order.builder()
                .user(user)
                .build();

        // Build a new order pattern detail object using the order, pattern, and payment order
        // Set the order date to the current instant
        var orderPatternDetail = OrderPatternDetail.builder()
                .order(order)
                .pattern(pattern)
                .transactionId(paymentOrder.getPayId())
                .status(OrderStatus.valueOf(paymentOrder.getStatus()))
                .orderDate(Date.from(Instant.now()))
                .build();

        // Add the order pattern detail to the order's list of order pattern details
        order.getOrderPatternDetails().add(orderPatternDetail);

        // Save the order to the database
        orderRepository.save(order);

        // Return the payment order
        return paymentOrder;
    }

    @Transactional
    @Override
    public String completePayment(String transactionId) {
        var completeOrder = payPalService.completePayment(transactionId);
        var orderPatternDetail = orderPatternDetailRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Order pattern detail not found for transaction ID " + transactionId));
        orderPatternDetail.setStatus(OrderStatus.valueOf(completeOrder.getStatus()));
        orderPatternDetailRepository.save(orderPatternDetail);
        return "Payment success";
    }
}
