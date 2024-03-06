package com.example.image_use_spring.member.persist.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "PasswordReset", timeToLive = 1200)
@Builder
@Getter
@Setter
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
