package com.example.springsocial.util;

import com.example.springsocial.config.RabbitMQProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;


@Component
@Slf4j
public class RabbitUtils {

  private final Channel channel;
  private final AmqpTemplate amqpTemplate;
  private final RabbitMQProperties rabbitProps;
  private final ObjectMapper mapper;

  @Autowired
  public RabbitUtils(Channel channel, AmqpTemplate amqpTemplate, RabbitMQProperties rabbitProps) {
    this.channel = channel;
    this.amqpTemplate = amqpTemplate;
    this.rabbitProps = rabbitProps;
    mapper = new ObjectMapper();
  }

  public static <T> T convertMessageToObject(byte[] msg, Class<T> classType) throws IOException {
    String jsonStr = new String(msg, StandardCharsets.UTF_8);
    return new ObjectMapper().readValue(jsonStr, classType);
  }

  public <T> CompletableFuture<T> consumeAsync(String queueName, Class<T> classType) {
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
    return future;
  }


  public <T> void convertAndSend(T message) {
    amqpTemplate.convertAndSend(rabbitProps.getExchange(), rabbitProps.getRoutingkey(), message);
    log.info("Send msg = " + message);
  }

  public void send(String message) {
    try {
      send(message, rabbitProps.getExchange(), rabbitProps.getQueue(), rabbitProps.getRoutingkey());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void send(String message, String exchange, String queueName, String routingKey) throws IOException {
    channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
    channel.queueDeclare(queueName, true, false, false, null);
    channel.queueBind(queueName, exchange, routingKey, null);
    channel.basicPublish(exchange, routingKey, null, message.getBytes());
  }
}
