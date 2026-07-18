package matheus.stefanello.trabalhobackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "estoque_produtos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"produto_id", "unidade_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstoqueProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @Column(name = "quantidade_disponivel", nullable = false)
    @Builder.Default
    private Integer quantidadeDisponivel = 0;

    @Column(name = "quantidade_minima", nullable = false)
    @Builder.Default
    private Integer quantidadeMinima = 5;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;
}
