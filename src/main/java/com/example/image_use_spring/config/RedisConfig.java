//package com.example.image_use_spring.config;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import redis.embedded.RedisServer;
//
//@Configuration
//public class RedisConfig {
//
//  private RedisServer redisServer;
//
//  @PostConstruct
//  public void startRedis() {
//    redisServer = new RedisServer(6379);
//    redisServer.start();
//  }
//
//  @PreDestroy
//  public void stopRedis() {
//    if (redisServer != null) {
//      redisServer.stop();
//    }
//  }
//
//  @Bean
//  public LettuceConnectionFactory redisConnectionFactory() {
//    return new LettuceConnectionFactory();
//  }
//
//  @Bean
//  public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
//    RedisTemplate<String, Object> template = new RedisTemplate<>();
//    template.setConnectionFactory(connectionFactory);
//    return template;
//  }
//}
