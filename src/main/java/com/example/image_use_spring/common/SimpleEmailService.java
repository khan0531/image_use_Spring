package com.example.image_use_spring.common;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

@Slf4j
@Service
public class SimpleEmailService {

  @Value("${aws.ses.access-key}")
  private String accessKey;

  @Value("${aws.ses.secret-key}")
  private String secretKey;

  private SesClient sesClient;

  @PostConstruct
  private void SimpleEmailService() {
    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
    sesClient = SesClient.builder()
        .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
        .region(Region.AP_NORTHEAST_2)
        .build();
  }


  public void sendEmail(String to, String subject, String body) {
    SendEmailRequest request = SendEmailRequest.builder()
        .destination(Destination.builder()
            .toAddresses(to)
            .build())
        .message(Message.builder()
            .subject(Content.builder()
                .data(subject)
                .charset("UTF-8")
                .build())
            .body(Body.builder()
                .text(Content.builder()
                    .data(body)
                    .charset("UTF-8")
                    .build())
                .build())
            .build())
        .source("rudgksdl94@gmail.com")
        .build();
    try {
      sesClient.sendEmail(request);
    }catch (Exception e) {
      log.info("error: {}", e.getMessage());
    }
  }
}