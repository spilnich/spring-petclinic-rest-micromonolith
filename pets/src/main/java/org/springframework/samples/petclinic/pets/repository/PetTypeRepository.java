package org.springframework.samples.petclinic.pets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.pets.model.PetType;

public interface PetTypeRepository extends JpaRepository<PetType, Integer> {
}
