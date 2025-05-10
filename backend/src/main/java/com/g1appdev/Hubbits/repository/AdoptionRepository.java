package com.g1appdev.Hubbits.repository;

import com.g1appdev.Hubbits.entity.AdoptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AdoptionRepository extends JpaRepository<AdoptionEntity, Long> {
    java.util.List<AdoptionEntity> findByUserId(Long userId);
    java.util.List<AdoptionEntity> findByUserIdAndPetId(Long userId, Integer petId);
    
    @Modifying
    @Query("DELETE FROM AdoptionEntity a WHERE a.petId = ?1")
    void deleteByPetId(Integer petId);
}
