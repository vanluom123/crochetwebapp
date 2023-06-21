package com.example.springsocial.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "littlecrochet.rabbitmq")
public class RabbitMQProperties {
  private Connection connection;
  private DeadLetterProperties deadLetter;
  private String exchange;
  private String queue;
  private String routingkey;
  private String uri;
  private String username;
  private String password;
  private String virtualHost;

  @Data
  public static class DeadLetterProperties {
    private String deadLetterExchange;
    private String deadLetterQueue;
    private String deadLetterRoutingKey;
    private String deadLetterExchangeHeader;
    private String deadLetterRoutingKeyHeader;
  }

  @Data
  public static class Connection {
    private Long channelCheckoutTimeout;
    private Integer connectionTimeout;
  }
}
