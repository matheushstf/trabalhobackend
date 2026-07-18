package matheus.stefanello.trabalhobackend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import matheus.stefanello.trabalhobackend.enums.CanalPedido;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {

    @NotNull(message = "ID da unidade é obrigatório")
    private Long unidadeId;

    @NotNull(message = "Canal do pedido é obrigatório")
    private CanalPedido canalPedido;

    @NotEmpty(message = "Pedido deve conter ao menos um item")
    @Valid
    private List<ItemPedidoRequestDTO> itens;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPedidoRequestDTO {
        
        @NotNull(message = "ID do produto é obrigatório")
        private Long produtoId;

        @NotNull(message = "Quantidade é obrigatória")
        private Integer quantidade;
    }
}
