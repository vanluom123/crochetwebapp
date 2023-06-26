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

  /**
   * Constructs a new RabbitMQSender object.
   *
   * @param amqpTemplate  The AmqpTemplate object for sending messages to RabbitMQ.
   * @param rabbitProps   The RabbitMQProperties object containing RabbitMQ configuration.
   * @param channel       The Channel object for interacting with RabbitMQ.
   */
  @Autowired
  public RabbitMQSender(AmqpTemplate amqpTemplate, RabbitMQProperties rabbitProps, Channel channel) {
    this.amqpTemplate = amqpTemplate;
    this.rabbitProps = rabbitProps;
    this.channel = channel;
  }


  /**
   * Converts the given message of type T and produces it to the specified RabbitMQ exchange with the provided routing key.
   *
   * @param message The message to be converted and produced.
   * @param <T> The type of the message.
   */
  public <T> void convertAndProduce(T message) {
    // Convert and send the message using AMQP template
    amqpTemplate.convertAndSend(rabbitProps.getExchange(), rabbitProps.getRoutingkey(), message);

    // Log sent message
    log.info("Send msg = " + message);
  }


  /**
   * Publishes a message to RabbitMQ.
   *
   * @param message The message to be published.
   * @throws RuntimeException If an error occurs during message publishing.
   */
  public void produce(String message) {
    try {
      declareExchangeAndQueue();
      channel.basicPublish(rabbitProps.getExchange(), rabbitProps.getRoutingkey(), null, message.getBytes());
      log.info("Sent message: {}", message);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * Declares the exchange, queue, and binding in RabbitMQ.
   *
   * @throws IOException If an error occurs during the declaration process.
   */
  private void declareExchangeAndQueue() throws IOException {
    channel.exchangeDeclare(rabbitProps.getExchange(), BuiltinExchangeType.DIRECT, true);
    channel.queueDeclare(rabbitProps.getQueue(), true, false, false, null);
    channel.queueBind(rabbitProps.getQueue(), rabbitProps.getExchange(), rabbitProps.getRoutingkey());
  }

}
