package com.example.image_use_spring.member.persist.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "PasswordReset", timeToLive = 1200)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordReset implements Serializable {

  @Id
  private Long memberId;
  private String email;
  private String token;

  @TimeToLive
  private Long ttl;

}
