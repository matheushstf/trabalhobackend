package matheus.stefanello.trabalhobackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FidelidadeResponseDTO {

    private Long id;
    private Long usuarioId;
    private String usuarioNome;
    private Integer pontosAcumulados;
    private Boolean consentimentoPrograma;
}
