package info.dylansouthard.StraysBookAPI.repository;

import info.dylansouthard.StraysBookAPI.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
