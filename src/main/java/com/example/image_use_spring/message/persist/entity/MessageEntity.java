package com.example.image_use_spring.message.persist.entity;

import com.example.image_use_spring.groups.persist.entity.ChatGroupEntity;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import com.example.image_use_spring.message.dto.MessageType;
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

@Entity(name = "chat_message")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private ChatGroupEntity group;

  @ManyToOne(fetch = FetchType.LAZY)
  private MemberEntity sender;

  private String message;

  private MessageType messageType;
}
