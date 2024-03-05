package com.example.image_use_spring.groups.persist.repository;

import com.example.image_use_spring.groups.persist.entity.ChatGroupEntity;
import com.example.image_use_spring.groups.persist.entity.MemberGroupEntity;
import com.example.image_use_spring.member.persist.entity.MemberEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberGroupRepository extends JpaRepository<MemberGroupEntity, Long> {

  List<MemberGroupEntity> findByChatGroup(ChatGroupEntity chatGroupEntity);

  boolean existsByMemberAndChatGroup(MemberEntity memberEntity, ChatGroupEntity chatGroupEntity);

  List<MemberGroupEntity> findByMember(MemberEntity memberEntity);

  Long countByChatGroup(ChatGroupEntity entity);
}
