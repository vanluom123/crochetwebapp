package org.crochet.service;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.crochet.enumerator.OrderIntent;
import org.crochet.enumerator.PaymentLandingPage;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.Order;
import org.crochet.model.OrderPatternDetail;
import org.crochet.model.User;
import org.crochet.payload.dto.CapturePaymentResponseDTO;
import org.crochet.payload.dto.MoneyDTO;
import org.crochet.payload.dto.OrderDTO;
import org.crochet.payload.dto.OrderResponseDTO;
import org.crochet.payload.dto.PayPalAppContextDTO;
import org.crochet.payload.dto.PurchaseUnit;
import org.crochet.repository.OrderPatternDetailRepository;
import org.crochet.repository.OrderRepository;
import org.crochet.repository.PatternRepository;
import org.crochet.repository.UserRepository;
import org.crochet.security.UserPrincipal;
import org.crochet.service.contact.OrderPatternService;
import org.crochet.service.contact.PayPalService;
import org.crochet.util.MonoUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderPatternServiceImpl implements OrderPatternService {
    private final PayPalService payPalService;
    private final UserRepository userRepository;
    private final PatternRepository patternRepository;
    private final OrderRepository orderRepository;
    private final OrderPatternDetailRepository orderPatternDetailRepository;
    private final Gson gson;

    public OrderPatternServiceImpl(PayPalService payPalService,
                                   UserRepository userRepository,
                                   PatternRepository patternRepository,
                                   OrderRepository orderRepository,
                                   OrderPatternDetailRepository orderPatternDetailRepository,
                                   Gson gson) {
        this.payPalService = payPalService;
        this.userRepository = userRepository;
        this.patternRepository = patternRepository;
        this.orderRepository = orderRepository;
        this.orderPatternDetailRepository = orderPatternDetailRepository;
        this.gson = gson;
    }

    private static OrderDTO createOrderDTO(String currencyCode, String value) {
        String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        var appContext = PayPalAppContextDTO.builder()
                .returnUrl(baseUri + "/api/checkout/order-pattern/success")
                .brandName("Little Crochet")
                .landingPage(PaymentLandingPage.BILLING)
                .build();
        MoneyDTO moneyDTO = MoneyDTO.builder()
                .currencyCode(currencyCode)
                .value(String.valueOf(value))
                .build();
        PurchaseUnit purchaseUnit = PurchaseUnit.builder()
                .amount(moneyDTO)
                .build();
        List<PurchaseUnit> purchaseUnits = new ArrayList<>();
        purchaseUnits.add(purchaseUnit);
        return OrderDTO.builder()
                .intent(OrderIntent.CAPTURE)
                .applicationContext(appContext)
                .purchaseUnits(purchaseUnits)
                .build();
    }

    @SneakyThrows
    @Transactional
    @Override
    public OrderResponseDTO createPayment(String patternId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ResourceNotFoundException("User hasn't signed in");
        }
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not existed in database"));

        var pattern = patternRepository.findById(UUID.fromString(patternId))
                .orElseThrow(() -> new RuntimeException("Pattern not found"));

        var orderDTO = createOrderDTO(pattern.getCurrencyCode().getValue(),
                String.valueOf(pattern.getPrice()));

        var content = payPalService.createOrder(orderDTO);
        var orderResponseDTO = gson.fromJson(MonoUtils.block(content), OrderResponseDTO.class);

        var order = Order.builder()
                .user(user)
                .build();
        order = orderRepository.save(order);

        var orderPatternDetail = OrderPatternDetail.builder()
                .order(order)
                .pattern(pattern)
                .transactionId(orderResponseDTO.getId())
                .status(orderResponseDTO.getStatus())
                .orderDate(Date.from(Instant.now()))
                .build();
        orderPatternDetailRepository.save(orderPatternDetail);

        return orderResponseDTO;
    }

    @SneakyThrows
    @Transactional
    @Override
    public String capturePayment(String transactionId) {
        var content = payPalService.capturePayment(transactionId);
        var payload = gson.fromJson(MonoUtils.block(content), CapturePaymentResponseDTO.class);
        var orderPatternDetail = orderPatternDetailRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Order not existed"));
        orderPatternDetail.setStatus(payload.getStatus());
        orderPatternDetailRepository.save(orderPatternDetail);
        return "Payment success";
    }
}
