//package com.example.image_use_spring.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.ses.SesClient;
//
//@Configuration
//public class SesConfig {
//
//  @Value("${aws.ses.access-key}")
//  private String accessKey;
//
//  @Value("${aws.ses.secret-key}")
//  private String secretKey;
//
//  @Value("${cloud.aws.region.static}")
//  private String region;
//
//  @Bean
//  public SesClient sesClient() {
//    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
//
//    return SesClient.builder()
//        .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
//        .region(Region.of(region))
//        .build();
//  }
//}
