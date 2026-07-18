package matheus.stefanello.trabalhobackend.repository;

import matheus.stefanello.trabalhobackend.model.Promocao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromocaoRepository extends JpaRepository<Promocao, Long> {

    List<Promocao> findByAtivaTrueOrderByDataCriacaoDesc();
}