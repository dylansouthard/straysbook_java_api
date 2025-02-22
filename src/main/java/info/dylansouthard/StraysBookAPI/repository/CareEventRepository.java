package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.model.CareEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareEventRepository extends JpaRepository<CareEvent, Long> {
}
