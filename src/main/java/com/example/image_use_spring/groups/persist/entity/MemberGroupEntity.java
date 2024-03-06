package com.example.image_use_spring.groups.persist.entity;

import com.example.image_use_spring.common.entity.BaseTimeEntity;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "member_group")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberGroupEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private MemberEntity member;

  @ManyToOne(fetch = FetchType.LAZY)
  private ChatGroupEntity chatGroup;
}
