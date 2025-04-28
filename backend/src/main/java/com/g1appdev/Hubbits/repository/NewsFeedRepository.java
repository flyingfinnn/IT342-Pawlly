package com.g1appdev.Hubbits.repository;

import com.g1appdev.Hubbits.entity.NewsFeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsFeedRepository extends JpaRepository<NewsFeedEntity, Long> {
}
