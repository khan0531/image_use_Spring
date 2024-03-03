package com.example.image_use_spring.groups.persist.entity;

import com.example.image_use_spring.common.entity.BaseTimeEntity;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "chat_group")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatGroupEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String description;

  private Long maxMembers;

  private LocalDate startAt;

  private LocalDate endAt;

  @ManyToOne(fetch = FetchType.LAZY)
  private MemberEntity admin;

  private String inviteLink;

  private LocalDateTime linkExpiredAt;
}
