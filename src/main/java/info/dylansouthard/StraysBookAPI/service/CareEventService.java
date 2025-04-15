package info.dylansouthard.StraysBookAPI.service;

import info.dylansouthard.StraysBookAPI.model.CareEvent;
import info.dylansouthard.StraysBookAPI.repository.CareEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CareEventService {
  @Autowired
    private CareEventRepository careEventRepository;

  public List<CareEvent> getAllRecentCareEventsByAnimalId(Long animalId) {
      LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
      return careEventRepository.findRecentCareEventsByAnimalId(animalId, thirtyDaysAgo);
  }
}
