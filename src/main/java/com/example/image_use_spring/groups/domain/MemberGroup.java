package com.example.image_use_spring.groups.domain;

import com.example.image_use_spring.groups.persist.entity.ChatGroupEntity;
import com.example.image_use_spring.groups.persist.entity.MemberGroupEntity;
import com.example.image_use_spring.member.domain.Member;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
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
public class MemberGroup {

  private Long id;
  private Long memberId;
  private String memberName;
  private Long chatGroupId;
  private String groupName;

  public static MemberGroup fromEntity(MemberGroupEntity memberGroupEntity) {
    return MemberGroup.builder()
        .id(memberGroupEntity.getId())
        .memberId(memberGroupEntity.getMember().getId())
        .memberName(memberGroupEntity.getMember().getName())
        .chatGroupId(memberGroupEntity.getChatGroup().getId())
        .groupName(memberGroupEntity.getChatGroup().getName())
        .build();
  }

  public static MemberGroup create(ChatGroup group, Member member) {
    return MemberGroup.builder()
        .memberId(member.getId())
        .memberName(member.getName())
        .chatGroupId(group.getId())
        .groupName(group.getName())
        .build();
  }

  public MemberGroupEntity toEntity() {
    return MemberGroupEntity.builder()
        .id(id)
        .member(MemberEntity.builder().id(memberId).name(memberName).build())
        .chatGroup(ChatGroupEntity.builder().id(chatGroupId).name(groupName).build())
        .build();
  }

}
