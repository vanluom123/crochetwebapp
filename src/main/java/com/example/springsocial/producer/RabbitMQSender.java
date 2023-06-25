package com.example.springsocial.producer;

import com.example.springsocial.config.RabbitMQProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class RabbitMQSender {
  private final AmqpTemplate amqpTemplate;
  private final RabbitMQProperties rabbitProps;
  private final Channel channel;

  @Autowired
  public RabbitMQSender(AmqpTemplate amqpTemplate, RabbitMQProperties rabbitProps, Channel channel) {
    this.amqpTemplate = amqpTemplate;
    this.rabbitProps = rabbitProps;
    this.channel = channel;
  }

  public <T> void convertAndProduce(T message) {
    amqpTemplate.convertAndSend(rabbitProps.getExchange(), rabbitProps.getRoutingkey(), message);
    log.info("Send msg = " + message);
  }

  public void produce(String message) {
    try {
      produce(message, rabbitProps.getExchange(), rabbitProps.getQueue(), rabbitProps.getRoutingkey());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void produce(String message, String exchange, String queueName, String routingKey) throws IOException {
    channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
    channel.queueDeclare(queueName, true, false, false, null);
    channel.queueBind(queueName, exchange, routingKey, null);
    channel.basicPublish(exchange, routingKey, null, message.getBytes());
  }
}
