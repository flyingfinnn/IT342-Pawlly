package com.g1appdev.Hubbits.repository;

import com.g1appdev.Hubbits.entity.VolunteerSignUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunteerSignUpRepository extends JpaRepository<VolunteerSignUp, Integer> {
    // Custom query methods can be added here if needed
    boolean existsByEmailAndVolunteerOpportunity_OpportunityID(String email, int opportunityId);
}
