package matheus.stefanello.trabalhobackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import matheus.stefanello.trabalhobackend.enums.CanalPedido;
import matheus.stefanello.trabalhobackend.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {

    private Long id;
    private Long unidadeId;
    private String unidadeNome;
    private Long usuarioId;
    private String usuarioNome;
    private CanalPedido canalPedido;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private List<ItemPedidoDTO> itens;
    private LocalDateTime dataHora;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPedidoDTO {
        private Long id;
        private Long produtoId;
        private String produtoNome;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private BigDecimal subtotal;
    }
}
