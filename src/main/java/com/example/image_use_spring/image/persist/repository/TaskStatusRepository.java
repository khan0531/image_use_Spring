package com.example.image_use_spring.image.persist.repository;

import com.example.image_use_spring.image.persist.entity.TaskStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, String> {
  List<TaskStatus> findByUuid(String id);
}
