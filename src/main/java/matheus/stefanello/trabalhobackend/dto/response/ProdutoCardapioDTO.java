package matheus.stefanello.trabalhobackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoCardapioDTO {

    private Long id;
    private String nome;
    private BigDecimal preco;
    private String categoria;
    private Boolean disponivelEstoque;
    private Integer quantidadeDisponivel;
}
