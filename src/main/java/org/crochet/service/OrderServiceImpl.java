package org.crochet.service;

import org.crochet.repository.OrderRepository;
import org.crochet.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepo;

    public void createOrder(OrderRequest orderRequest) {
    }
}
