package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.model.CareEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CareEventRepository extends JpaRepository<CareEvent, Long> {
    @Query("SELECT ce FROM CareEvent ce JOIN ce.animals a " +
            "WHERE a.id = :animalId AND ce.date >= :sinceDate " +
            "ORDER BY ce.date DESC")
    List<CareEvent> findRecentCareEventsByAnimalId(@Param("animalId") Long animalId, @Param("sinceDate") LocalDateTime sinceDate);
}
