package com.g1appdev.Hubbits.controller;

import com.g1appdev.Hubbits.entity.AdoptionEntity;
import com.g1appdev.Hubbits.service.AdoptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/adoptions")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    // Get all adoptions
    @GetMapping
    public List<AdoptionEntity> getAllAdoptions() {
        return adoptionService.getAllAdoptions();
    }

    // Get adoption by ID
    @GetMapping("/{id}")
    public ResponseEntity<AdoptionEntity> getAdoptionById(@PathVariable Long id) {
        Optional<AdoptionEntity> adoption = adoptionService.getAdoptionById(id);
        return adoption.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create new adoption
    @PostMapping
    public AdoptionEntity createAdoption(@RequestBody AdoptionEntity adoption) {
        return adoptionService.createAdoption(adoption);
    }

    // Update adoption by ID
    @PutMapping("/{id}")
    public ResponseEntity<AdoptionEntity> updateAdoption(@PathVariable Long id, @RequestBody AdoptionEntity adoption) {
        AdoptionEntity updatedAdoption = adoptionService.updateAdoption(id, adoption);
        if (updatedAdoption != null) {
            return ResponseEntity.ok(updatedAdoption);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete adoption by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdoption(@PathVariable Long id) {
        boolean deleted = adoptionService.deleteAdoption(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
