package com.example.endavapwj.util;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JudgeQueueConfig {

  public static final String EXCHANGE = "judge.direct";
  public static final String ROUTING_KEY = "judge.run";
  public static final String QUEUE = "judge.requests";
  public static final String RESULT = "judge.result";

  @Bean
  public DirectExchange judgeExchange() {
    return new DirectExchange(EXCHANGE);
  }

  @Bean
  public Queue judgeQueue() {
    return QueueBuilder.durable(QUEUE).build();
  }

  @Bean
  public Binding judgeBinding(Queue judgeQueue, DirectExchange judgeExchange) {
    return BindingBuilder.bind(judgeQueue).to(judgeExchange).with(ROUTING_KEY);
  }

  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public Queue judgeResultQueue() {
    return QueueBuilder.durable(RESULT).build();
  }
}
