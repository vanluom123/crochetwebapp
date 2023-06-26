package com.example.springsocial.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Component
@Slf4j
public class RabbitMQConsumer {
  private final Channel channel;
  private final ObjectMapper mapper;

  /**
   * Constructs a RabbitMQConsumer with the provided Channel dependency.
   *
   * @param channel The Channel dependency.
   */
  @Autowired
  public RabbitMQConsumer(Channel channel) {
    this.channel = channel;
    mapper = new ObjectMapper();
  }


  /**
   * Consumes a message from the specified queue and deserializes it to the provided class type.
   *
   * @param queueName  The name of the queue to consume from.
   * @param classType  The class type to deserialize the message into.
   * @param <T>        The type of the object to deserialize the message into.
   * @return The deserialized object from the consumed message.
   * @throws CompletionException If there is an error during consumption or deserialization.
   */
  public <T> T consume(String queueName, Class<T> classType) {
    // Create a CompletableFuture to handle the asynchronous result
    CompletableFuture<T> future = new CompletableFuture<>();

    try {
      // Consume a message from the specified queue
      channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
        // Convert the message body to a string
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

        try {
          // Deserialize the message to the provided class type
          T object = mapper.readValue(message, classType);
          // Complete the CompletableFuture with the deserialized object
          future.complete(object);
          log.info("Received message: " + message);
        } catch (Exception e) {
          // Complete the CompletableFuture exceptionally if there is an error during deserialization
          future.completeExceptionally(e);
        }
      }, consumerTag -> {
        // Empty consumerTag callback
      });
    } catch (IOException e) {
      // Complete the CompletableFuture exceptionally if there is an error during consumption
      future.completeExceptionally(e);
    }

    // Wait for the CompletableFuture to complete and return the result
    return future.join();
  }

}
