package com.example.image_use_spring.groups.persist.repository;

import com.example.image_use_spring.groups.persist.entity.ChatGroupEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatGroupRepository extends JpaRepository<ChatGroupEntity, Long> {

  Optional<ChatGroupEntity> findByInviteLink(String inviteLink);
}
