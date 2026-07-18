package matheus.stefanello.trabalhobackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "programa_fidelidade")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramaFidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "saldo", nullable = false)
    @Builder.Default
    private Integer saldo = 0;

    @Column(name = "consentimento", nullable = false)
    @Builder.Default
    private Boolean consentimento = false;
}
