package com.example.image_use_spring.groups.domain;

import com.example.image_use_spring.groups.dto.ChatGroupDto;
import com.example.image_use_spring.groups.persist.entity.ChatGroupEntity;
import com.example.image_use_spring.member.domain.Member;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ChatGroup {

  private Long id;

  private String name;

  private String description;

  private Long maxMembers;

  private LocalDate startAt;

  private LocalDate endAt;

  private Long adminId;

  private String inviteLink;

  private LocalDateTime linkExpiredAt;

  public static ChatGroup fromRequest(ChatGroupDto.Request groupRequest, Member member) {
    return ChatGroup.builder()
        .name(groupRequest.getName())
        .description(groupRequest.getDescription())
        .maxMembers(groupRequest.getMaxMembers())
        .startAt(groupRequest.getStartAt())
        .endAt(groupRequest.getEndAt())
        .adminId(member.getId())
        .build();
  }

  public static ChatGroup fromEntity(ChatGroupEntity chatGroupEntity) {
    return ChatGroup.builder()
        .id(chatGroupEntity.getId())
        .name(chatGroupEntity.getName())
        .description(chatGroupEntity.getDescription())
        .maxMembers(chatGroupEntity.getMaxMembers())
        .startAt(chatGroupEntity.getStartAt())
        .endAt(chatGroupEntity.getEndAt())
        .adminId(chatGroupEntity.getAdmin().getId())
        .inviteLink(chatGroupEntity.getInviteLink())
        .linkExpiredAt(chatGroupEntity.getLinkExpiredAt())
        .build();
  }

  public boolean isLinkValid() {
    return inviteLink != null && linkExpiredAt.isAfter(LocalDateTime.now());
  }

  public ChatGroupEntity toEntity() {
    return ChatGroupEntity.builder()
        .id(id)
        .name(name)
        .description(description)
        .maxMembers(maxMembers)
        .startAt(startAt)
        .endAt(endAt)
        .admin(MemberEntity.builder().id(adminId).build())
        .inviteLink(inviteLink)
        .linkExpiredAt(linkExpiredAt)
        .build();
  }

  public boolean isAdmin(Member member) {
    return adminId.equals(member.getId());
  }

  public ChatGroup update(ChatGroupDto.Request request) {
    if (request.getName() != null) {
      this.name = request.getName();
    }
    if (request.getDescription() != null) {
      this.description = request.getDescription();
    }
    if (request.getMaxMembers() != null) {
      this.maxMembers = request.getMaxMembers();
    }
    if (request.getStartAt() != null) {
      this.startAt = request.getStartAt();
    }
    if (request.getEndAt() != null) {
      this.endAt = request.getEndAt();
    }
    return this;
  }

  public ChatGroup updateInviteLink(String inviteLink) {
    this.inviteLink = inviteLink;
    this.linkExpiredAt = LocalDateTime.now().plusMinutes(30);
    return this;
  }
}
