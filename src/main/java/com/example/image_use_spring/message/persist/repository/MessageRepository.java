package com.example.image_use_spring.message.persist.repository;

import com.example.image_use_spring.groups.persist.entity.ChatGroupEntity;
import com.example.image_use_spring.message.persist.entity.MessageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
  List<MessageEntity> findByGroup(ChatGroupEntity challengeGroupEntity);
}
