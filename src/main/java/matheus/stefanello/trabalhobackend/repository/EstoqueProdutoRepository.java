package matheus.stefanello.trabalhobackend.repository;

import matheus.stefanello.trabalhobackend.model.EstoqueProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueProdutoRepository extends JpaRepository<EstoqueProduto, Long> {
    
    Optional<EstoqueProduto> findByProdutoIdAndUnidadeId(Long produtoId, Long unidadeId);
    
    List<EstoqueProduto> findByUnidadeId(Long unidadeId);
    
    List<EstoqueProduto> findByProdutoId(Long produtoId);
}
