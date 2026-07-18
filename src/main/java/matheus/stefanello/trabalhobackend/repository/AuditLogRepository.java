package matheus.stefanello.trabalhobackend.repository;

import matheus.stefanello.trabalhobackend.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}