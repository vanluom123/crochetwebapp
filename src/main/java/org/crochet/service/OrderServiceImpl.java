package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.repository.OrderRepository;
import org.crochet.request.OrderRequest;
import org.crochet.service.abstraction.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderRepository orderRepository;

  public void createOrder(OrderRequest orderRequest) {
  }
}
