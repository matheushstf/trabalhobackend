package matheus.stefanello.trabalhobackend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import matheus.stefanello.trabalhobackend.enums.MetodoPagamento;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoRequestDTO {

    @NotNull(message = "ID do pedido é obrigatório")
    private Long pedidoId;

    @NotNull(message = "Método de pagamento é obrigatório")
    private MetodoPagamento metodoPagamento;
}
