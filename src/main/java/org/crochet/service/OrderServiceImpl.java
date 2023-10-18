package org.crochet.service;

import org.crochet.repository.OrderRepository;
import org.crochet.request.OrderRequest;
import org.crochet.service.abstraction.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;

  public OrderServiceImpl(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public void createOrder(OrderRequest orderRequest) {
  }
}
