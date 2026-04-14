package com.example.apidressing.repository;

import com.example.apidressing.gen.model.ClothingLayer;
import com.example.apidressing.model.ClothingItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClothingItemRepository extends JpaRepository<ClothingItemEntity, Long> {

    List<ClothingItemEntity> findByUserId(Long userId);

    List<ClothingItemEntity> findByUserIdAndLayer(Long userId, ClothingLayer layer);

    boolean existsByIdAndUserId(Long id, Long userId);

    java.util.Optional<ClothingItemEntity> findByIdAndUserId(Long id, Long userId);
}
