package com.example.endavapwj.util;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JudgeQueueConfig {

  public static final String EXCHANGE = "judge.direct";
  public static final String ROUTING_KEY = "judge.run";
  public static final String QUEUE = "judge.requests";
  public static final String RESULT = "judge.result";
  public static final String RESULT_ROUTING_KEY = "judge.key.result";

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
  public Queue judgeResultQueue() {
    return QueueBuilder.durable(RESULT).build();
  }

  @Bean
  public Binding judgeResultBinding(Queue judgeResultQueue, DirectExchange judgeExchange) {
    return BindingBuilder.bind(judgeResultQueue).to(judgeExchange).with(RESULT_ROUTING_KEY);
  }
}
