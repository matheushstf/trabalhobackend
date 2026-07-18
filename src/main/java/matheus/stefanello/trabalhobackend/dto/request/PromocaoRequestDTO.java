package matheus.stefanello.trabalhobackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromocaoRequestDTO {

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    private String descricao;

    private String regraAplicacao;

    @NotNull(message = "Percentual de desconto é obrigatório")
    @PositiveOrZero(message = "Percentual de desconto deve ser positivo")
    private BigDecimal percentualDesconto;

    @NotNull(message = "Status da promoção é obrigatório")
    private Boolean ativa;

    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;
}