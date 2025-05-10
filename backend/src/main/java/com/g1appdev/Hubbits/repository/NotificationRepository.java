package com.g1appdev.Hubbits.repository;

import com.g1appdev.Hubbits.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.pet_id = ?1")
    void deleteByPetId(Long petId);
} 