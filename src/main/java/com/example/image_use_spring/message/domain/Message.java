package com.example.image_use_spring.message.domain;

import com.example.image_use_spring.groups.persist.entity.ChatGroupEntity;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import com.example.image_use_spring.message.dto.MessageType;
import com.example.image_use_spring.message.persist.entity.MessageEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
  private Long id;
  private Long groupId; // 그룹(채팅방) 번호
  private Long senderId; // 채팅을 보낸 사람
  private String message; // 메시지
  private MessageType messageType;
  private LocalDateTime createdAt;

  public static Message fromEntity(MessageEntity messageEntity) {
    return Message.builder()
        .id(messageEntity.getId())
        .groupId(messageEntity.getGroup().getId())
        .senderId(messageEntity.getSender().getId())
        .message(messageEntity.getMessage())
        .messageType(messageEntity.getMessageType())
        .createdAt(messageEntity.getCreatedAt())
        .build();
  }

  public static List<Message> fromEntities(List<MessageEntity> messageEntities) {
    return messageEntities.stream()
        .map(Message::fromEntity)
        .collect(Collectors.toList());
  }

  public MessageEntity toEntity() {
    return MessageEntity.builder()
        .group(ChatGroupEntity.builder().id(groupId).build())
        .sender(MemberEntity.builder().id(senderId).build())
        .message(message)
        .messageType(messageType)
        .build();
  }
}
