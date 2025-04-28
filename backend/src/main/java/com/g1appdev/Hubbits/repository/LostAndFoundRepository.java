package com.g1appdev.Hubbits.repository;

import com.g1appdev.Hubbits.entity.LostAndFoundEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LostAndFoundRepository extends JpaRepository<LostAndFoundEntity, Integer> {
    List<LostAndFoundEntity> findByLastseen(String lastseen);

    List<LostAndFoundEntity> findByCreatorid(int creatorid);
}