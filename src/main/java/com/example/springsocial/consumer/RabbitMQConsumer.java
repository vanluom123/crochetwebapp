package com.example.springsocial.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class RabbitMQConsumer {
  private final Channel channel;
  private final ObjectMapper mapper;

  @Autowired
  public RabbitMQConsumer(Channel channel) {
    this.channel = channel;
    mapper = new ObjectMapper();
  }


  public <T> T consume(String queueName, Class<T> classType) {
    CompletableFuture<T> future = new CompletableFuture<>();
    try {
      channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        try {
          T object = mapper.readValue(message, classType);
          future.complete(object);
          log.info("Received message: " + message);
        } catch (Exception e) {
          future.completeExceptionally(e);
        }
      }, consumerTag -> {
      });
    } catch (IOException e) {
      future.completeExceptionally(e);
    }
    return future.join();
  }
}
