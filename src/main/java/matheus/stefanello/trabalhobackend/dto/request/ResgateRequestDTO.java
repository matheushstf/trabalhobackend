package matheus.stefanello.trabalhobackend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResgateRequestDTO {

    @NotNull(message = "Quantidade de pontos é obrigatória")
    @Min(value = 1, message = "Quantidade de pontos deve ser maior que zero")
    private Integer pontos;

    private String descricao;
}
