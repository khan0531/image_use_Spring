package com.example.image_use_awss3.image.persist.repository;

import com.example.image_use_awss3.image.persist.entity.ImageEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

  Optional<ImageEntity> findByImageFileName(String imageFileName);
}
