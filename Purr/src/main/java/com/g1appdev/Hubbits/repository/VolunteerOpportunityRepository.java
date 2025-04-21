package com.g1appdev.Hubbits.repository;

import com.g1appdev.Hubbits.entity.VolunteerOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunteerOpportunityRepository extends JpaRepository<VolunteerOpportunity, Integer> {
    // You can define custom query methods here if needed
}
