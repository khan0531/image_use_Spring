package com.example.image_use_spring.member.persist.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "VerificationCode", timeToLive = 300)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationCode implements Serializable {

  @Id
  private String email;
  private String code;
  private boolean isVerified;

  @TimeToLive
  private Long ttl;
}
