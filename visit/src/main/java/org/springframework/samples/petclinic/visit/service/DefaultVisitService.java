package org.springframework.samples.petclinic.visit.service;

import com.google.common.eventbus.EventBus;

import org.springframework.samples.petclinic.common.data.AnimalType;
import org.springframework.samples.petclinic.common.error.ResourceNotFoundException;
import org.springframework.samples.petclinic.common.error.VisitsAmountIsExceededException;
import org.springframework.samples.petclinic.common.event.VisitCreatedEvent;
import org.springframework.samples.petclinic.customer.api.message.PetResponse;
import org.springframework.samples.petclinic.customer.api.service.ApiPetService;
import org.springframework.samples.petclinic.visit.model.Visit;
import org.springframework.samples.petclinic.visit.repository.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultVisitService implements VisitService {

  private static final String VISIT_NOT_FOUND_PATTERN = "Visit with id %d not found";
  private static final int MAX_VISITS_THRESHOLD = 3;

  private final VisitRepository visitRepository;
  private final ApiPetService apiPetService;
  private final EventBus eventBus;

  @Override
  public Visit getVisitById(int id) {
    return visitRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(String.format(VISIT_NOT_FOUND_PATTERN, id)));
  }

  @Override
  public Collection<Visit> getAllVisits() {
    return visitRepository.findAll();
  }

  @Override
  @Transactional
  public void saveVisit(Visit visit) {
    Integer petId = visit.getPetId();
    PetResponse pet = apiPetService.getPet(petId);
    boolean isLizardOrSnake = pet.getType() == AnimalType.LIZARD || pet.getType() == AnimalType.SNAKE;
    if (isLizardOrSnake && visitRepository.countByPetId(petId) >= MAX_VISITS_THRESHOLD) {
      throw new VisitsAmountIsExceededException();
    }
    visitRepository.saveAndFlush(visit);
    eventBus.post(
        new VisitCreatedEvent(visit.getId(), visit.getDescription(), visit.getPetId())
    );
  }

  @Override
  @Transactional
  public void deleteVisit(Integer id) {
    visitRepository.delete(
        visitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format(VISIT_NOT_FOUND_PATTERN, id)))
    );
  }

  @Override
  @Transactional
  public Visit updateVisit(Visit visit) {
    return visitRepository.save(visit);
  }
}