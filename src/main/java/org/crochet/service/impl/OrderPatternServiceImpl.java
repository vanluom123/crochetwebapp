package org.crochet.service.impl;

import org.crochet.client.paypal.PaymentOrder;
import org.crochet.enumerator.OrderStatus;
import org.crochet.exception.DecoratedRuntimeException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.LOGIN_TO_ORDER_PATTERN_MESSAGE;
import static org.crochet.constant.MessageConstant.ORDER_PATTERN_DETAIL_NOT_FOUND_FOR_TRANSACTION_ID_MESSAGE;
import static org.crochet.constant.MessageConstant.PATTERN_NOT_FOUND_MESSAGE;
import static org.crochet.constant.MessageConstant.PAYMENT_SUCCESS_MESSAGE;
import static org.crochet.constant.MessageConstant.USER_NOT_FOUND_MESSAGE;

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
    public PaymentOrder createPayment(UserPrincipal principal, String patternId) {
        if (principal == null) {
            throw new ResourceNotFoundException(LOGIN_TO_ORDER_PATTERN_MESSAGE,
                    MAP_CODE.get(LOGIN_TO_ORDER_PATTERN_MESSAGE));
        }

        // Fetch the user from the database using the ID from the UserPrincipal
        // If the user is not found, throw a ResourceNotFoundException
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE,
                        MAP_CODE.get(USER_NOT_FOUND_MESSAGE)));

        // Fetch the pattern from the database using the provided pattern ID
        // If the pattern is not found, throw a ResourceNotFoundException
        var pattern = patternRepository.findById(UUID.fromString(patternId))
                .orElseThrow(() -> new ResourceNotFoundException(PATTERN_NOT_FOUND_MESSAGE,
                        MAP_CODE.get(PATTERN_NOT_FOUND_MESSAGE)));

        // Create a payment order using the price of the pattern
        var paymentOrder = payPalService.createPayment(pattern.getPrice());

        // Build a new order object using the fetched user
        Order order = new Order()
                .setUser(user);
        order = orderRepository.save(order);

        // Build a new order pattern detail object using the order, pattern, and payment order
        // Set the order date to the current instant
        OrderPatternDetail detail = new OrderPatternDetail()
                .setOrder(order)
                .setPattern(pattern)
                .setTransactionId(paymentOrder.getPayId())
                .setStatus(OrderStatus.valueOf(paymentOrder.getStatus()))
                .setOrderDate(LocalDateTime.now());
        orderPatternDetailRepository.save(detail);

        // Return the payment order
        return paymentOrder;
    }

    @Transactional
    @Override
    public String completePayment(String transactionId) {
        var completeOrder = payPalService.completePayment(transactionId);
        var orderPatternDetail = orderPatternDetailRepository.findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new DecoratedRuntimeException(ORDER_PATTERN_DETAIL_NOT_FOUND_FOR_TRANSACTION_ID_MESSAGE + transactionId,
                                MAP_CODE.get(ORDER_PATTERN_DETAIL_NOT_FOUND_FOR_TRANSACTION_ID_MESSAGE))
                );
        orderPatternDetail.setStatus(OrderStatus.valueOf(completeOrder.getStatus()));
        orderPatternDetailRepository.save(orderPatternDetail);
        return PAYMENT_SUCCESS_MESSAGE;
    }
}
