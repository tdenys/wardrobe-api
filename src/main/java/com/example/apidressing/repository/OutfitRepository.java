package com.example.apidressing.repository;

import com.example.apidressing.model.OutfitEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutfitRepository extends JpaRepository<OutfitEntity, Long> {

    Page<OutfitEntity> findByUserId(Long userId, Pageable pageable);

    java.util.Optional<OutfitEntity> findByIdAndUserId(Long id, Long userId);
}
