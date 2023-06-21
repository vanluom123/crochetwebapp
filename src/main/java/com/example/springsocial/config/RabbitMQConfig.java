package com.example.springsocial.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
public class RabbitMQConfig {

  private final RabbitMQProperties rabbitProps;

  @Autowired
  public RabbitMQConfig(RabbitMQProperties rabbitProps) {
    this.rabbitProps = rabbitProps;
  }

  @Bean
  DirectExchange deadLetterExchange() {
    return new DirectExchange(rabbitProps.getDeadLetter().getDeadLetterExchange());
  }

  @Bean
  Queue dlq() {
    return QueueBuilder.durable(rabbitProps.getDeadLetter().getDeadLetterQueue())
        .build();
  }

  @Bean
  Queue queue() {
    return QueueBuilder.durable(rabbitProps.getQueue())
        .withArgument("x-dead-letter-exchange", rabbitProps.getDeadLetter().getDeadLetterExchange())
        .withArgument("x-dead-letter-routing-key", rabbitProps.getDeadLetter().getDeadLetterRoutingKey())
        .build();
  }

  @Bean
  DirectExchange exchange() {
    return new DirectExchange(rabbitProps.getExchange());
  }

  @Bean
  Binding DLQbinding(Queue dlq, DirectExchange deadLetterExchange) {
    return BindingBuilder.bind(dlq).to(deadLetterExchange).with(rabbitProps.getDeadLetter().getDeadLetterRoutingKey());
  }

  @Bean
  Binding binding(Queue queue, DirectExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(rabbitProps.getRoutingkey());
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory cf = new CachingConnectionFactory();
    cf.setUsername(rabbitProps.getUsername());
    cf.setPassword(rabbitProps.getPassword());
    cf.setUri(rabbitProps.getUri());
    cf.setChannelCheckoutTimeout(rabbitProps.getConnection().getChannelCheckoutTimeout());
    cf.setConnectionTimeout(rabbitProps.getConnection().getConnectionTimeout());
    return cf;
  }

  @Primary
  @Bean("rabbitmqTemplate")
  public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RetryTemplate retryTemplate = new RetryTemplate();
    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(3);
    retryTemplate.setRetryPolicy(retryPolicy);

    FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
    backOffPolicy.setBackOffPeriod(5000);
    retryTemplate.setBackOffPolicy(backOffPolicy);

    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    rabbitTemplate.setRetryTemplate(retryTemplate);
    return rabbitTemplate;
  }

  @Bean
  public Channel channel(ConnectionFactory cf) throws IOException, TimeoutException {
    CachingConnectionFactory cacheCF = (CachingConnectionFactory) cf;
    Connection conn = cacheCF.getRabbitConnectionFactory().newConnection();
    return conn.createChannel();
  }

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
    containerFactory.setConnectionFactory(connectionFactory);
    return containerFactory;
  }
}
