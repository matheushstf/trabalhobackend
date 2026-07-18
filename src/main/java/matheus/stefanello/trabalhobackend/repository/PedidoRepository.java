package matheus.stefanello.trabalhobackend.repository;

import matheus.stefanello.trabalhobackend.enums.CanalPedido;
import matheus.stefanello.trabalhobackend.enums.StatusPedido;
import matheus.stefanello.trabalhobackend.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByUsuarioId(Long usuarioId);
    
    List<Pedido> findByCanalPedido(CanalPedido canalPedido);
    
    List<Pedido> findByStatus(StatusPedido status);
    
    List<Pedido> findByUnidadeId(Long unidadeId);
    
    @Query("SELECT p FROM Pedido p WHERE " +
           "(:canalPedido IS NULL OR p.canalPedido = :canalPedido) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:unidadeId IS NULL OR p.unidade.id = :unidadeId) AND " +
           "(:dataInicio IS NULL OR p.dataHora >= :dataInicio) AND " +
           "(:dataFim IS NULL OR p.dataHora <= :dataFim)")
        Page<Pedido> findByFiltros(
            @Param("canalPedido") CanalPedido canalPedido,
            @Param("status") StatusPedido status,
            @Param("unidadeId") Long unidadeId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );
}
