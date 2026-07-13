package matheus.stefanello.trabalhobackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private String tipo;
    private Long usuarioId;
    private String nome;
    private String email;
    private Set<String> perfis;
}
