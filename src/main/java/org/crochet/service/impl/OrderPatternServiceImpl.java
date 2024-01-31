package org.crochet.service.impl;

import lombok.SneakyThrows;
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

    @SneakyThrows
    @Transactional
    @Override
    public PaymentOrder createPayment(String patternId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ResourceNotFoundException("User hasn't signed in");
        }
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not existed in database"));

        var pattern = patternRepository.findById(UUID.fromString(patternId))
                .orElseThrow(() -> new RuntimeException("Pattern not found"));

        var orderResponseDTO = payPalService.createPayment(pattern.getPrice());

        var order = Order.builder()
                .user(user)
                .build();
        order = orderRepository.save(order);

        var orderPatternDetail = OrderPatternDetail.builder()
                .order(order)
                .pattern(pattern)
                .transactionId(orderResponseDTO.getPayId())
                .status(OrderStatus.valueOf(orderResponseDTO.getStatus()))
                .orderDate(Date.from(Instant.now()))
                .build();
        orderPatternDetailRepository.save(orderPatternDetail);

        return orderResponseDTO;
    }

    @SneakyThrows
    @Transactional
    @Override
    public String completePayment(String transactionId) {
        var completeOrder = payPalService.completePayment(transactionId);
        var orderPatternDetail = orderPatternDetailRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Order not existed"));
        orderPatternDetail.setStatus(OrderStatus.valueOf(completeOrder.getStatus()));
        orderPatternDetailRepository.save(orderPatternDetail);
        return "Payment success";
    }
}
