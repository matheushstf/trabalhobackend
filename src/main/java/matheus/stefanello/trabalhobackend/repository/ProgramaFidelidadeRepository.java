package matheus.stefanello.trabalhobackend.repository;

import matheus.stefanello.trabalhobackend.model.ProgramaFidelidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgramaFidelidadeRepository extends JpaRepository<ProgramaFidelidade, Long> {
    
    Optional<ProgramaFidelidade> findByUsuarioId(Long usuarioId);
}
