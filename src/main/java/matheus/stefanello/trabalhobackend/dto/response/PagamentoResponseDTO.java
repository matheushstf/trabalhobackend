package matheus.stefanello.trabalhobackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import matheus.stefanello.trabalhobackend.enums.MetodoPagamento;
import matheus.stefanello.trabalhobackend.enums.StatusPagamento;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoResponseDTO {

    private Long id;
    private Long pedidoId;
    private BigDecimal valor;
    private MetodoPagamento metodoPagamento;
    private StatusPagamento status;
    private String mensagem;
}
