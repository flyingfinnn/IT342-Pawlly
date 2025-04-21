package com.g1appdev.Hubbits.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.g1appdev.Hubbits.entity.PetEntity;


@Repository 
public interface PetRepository extends JpaRepository<PetEntity, Integer>{

    public List<PetEntity> findPetsByName(String name);

}

