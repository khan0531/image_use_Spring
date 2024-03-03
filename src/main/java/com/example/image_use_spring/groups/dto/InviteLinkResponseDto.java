package com.example.image_use_spring.groups.dto;

import com.example.image_use_spring.groups.persist.entity.ChatGroupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InviteLinkResponseDto {

  private Long groupId;
  private String inviteLink;

  public static InviteLinkResponseDto fromEntity(ChatGroupEntity ChatGroup) {
    return InviteLinkResponseDto.builder()
        .groupId(ChatGroup.getId())
        .inviteLink(ChatGroup.getInviteLink())
        .build();
  }
}
