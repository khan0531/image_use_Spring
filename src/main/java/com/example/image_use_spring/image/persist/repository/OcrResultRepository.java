package com.example.image_use_spring.image.persist.repository;

import com.example.image_use_spring.image.persist.entity.OcrResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcrResultRepository extends JpaRepository<OcrResultEntity, Long> {

}
