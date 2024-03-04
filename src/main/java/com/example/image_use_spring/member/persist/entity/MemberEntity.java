package com.example.image_use_spring.member.persist.entity;


import com.example.image_use_spring.common.entity.BaseTimeEntity;
import com.example.image_use_spring.member.dto.constants.AuthType;
import com.example.image_use_spring.member.dto.constants.Authority;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member", indexes = {
    @Index(columnList = "oauthId"),
    @Index(columnList = "email"),
    @Index(columnList = "refreshToken")
})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private AuthType authType;

  @Column(unique = true)
  private String oauthId; // 일반 회원 가입은 null

  private String refreshToken;

  private String name;

  @Column(unique = true)
  private String email;

  private String password;

  @Enumerated(EnumType.STRING)
  private Authority role;

  private LocalDateTime withdrawalAt;
}