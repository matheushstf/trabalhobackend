package matheus.stefanello.trabalhobackend.service;

import matheus.stefanello.trabalhobackend.dto.request.ResgateRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.FidelidadeResponseDTO;
import matheus.stefanello.trabalhobackend.exception.BusinessRuleException;
import matheus.stefanello.trabalhobackend.exception.ResourceNotFoundException;
import matheus.stefanello.trabalhobackend.model.ProgramaFidelidade;
import matheus.stefanello.trabalhobackend.model.Usuario;
import matheus.stefanello.trabalhobackend.repository.ProgramaFidelidadeRepository;
import matheus.stefanello.trabalhobackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FidelidadeService {

    private final ProgramaFidelidadeRepository programaFidelidadeRepository;

    private final UsuarioRepository usuarioRepository;

    private final AuditLogService auditLogService;

    @Transactional
    public FidelidadeResponseDTO aderirPrograma() {
        Usuario usuario = getUsuarioAutenticado();

        // Verificar consentimento LGPD
        if (!usuario.getConsentimentoLGPD()) {
            throw new BusinessRuleException("É necessário consentimento LGPD para aderir ao programa de fidelidade");
        }

        // Verificar se já está no programa
        if (programaFidelidadeRepository.findByUsuarioId(usuario.getId()).isPresent()) {
            throw new BusinessRuleException("Usuário já está cadastrado no programa de fidelidade");
        }

        // Criar programa
        ProgramaFidelidade programa = ProgramaFidelidade.builder()
                .usuario(usuario)
                .saldo(0)
                .consentimento(true)
                .build();
        programa = programaFidelidadeRepository.save(programa);
        auditLogService.registrar("LOYALTY_JOINED", "PROGRAMA_FIDELIDADE", programa.getId(), usuario.getEmail(), "Usuário aderiu ao programa de fidelidade");

        return toResponseDTO(programa);
    }

    public FidelidadeResponseDTO obterMeusPontos() {
        Usuario usuario = getUsuarioAutenticado();
        
        ProgramaFidelidade programa = programaFidelidadeRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new BusinessRuleException("Usuário não está cadastrado no programa de fidelidade"));

        return toResponseDTO(programa);
    }

    @Transactional
    public FidelidadeResponseDTO resgatarPontos(ResgateRequestDTO dto) {
        Usuario usuario = getUsuarioAutenticado();
        
        ProgramaFidelidade programa = programaFidelidadeRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new BusinessRuleException("Usuário não está cadastrado no programa de fidelidade"));

        if (!programa.getConsentimento()) {
            throw new BusinessRuleException("Programa de fidelidade não está ativo");
        }

        if (programa.getSaldo() < dto.getPontos()) {
            throw new BusinessRuleException("Pontos insuficientes. Você possui " + programa.getSaldo() + " pontos");
        }

        // Resgatar pontos (atualiza saldo diretamente)
        programa.setSaldo(programa.getSaldo() - dto.getPontos());
        programa = programaFidelidadeRepository.save(programa);
        auditLogService.registrar("LOYALTY_REDEEMED", "PROGRAMA_FIDELIDADE", programa.getId(), usuario.getEmail(), "Resgate de " + dto.getPontos() + " pontos");

        return toResponseDTO(programa);
    }

    @Transactional
    public void acumularPontosPorPedido(Long usuarioId, java.math.BigDecimal valorPedido) {
        ProgramaFidelidade programa = programaFidelidadeRepository.findByUsuarioId(usuarioId)
                .orElse(null);

        if (programa == null || !programa.getConsentimento()) {
            return; // Usuário não participa do programa
        }

        // Regra: 1 ponto a cada R$ 10 gastos
        int pontosGanhos = valorPedido.divide(new java.math.BigDecimal("10")).intValue();
        
        if (pontosGanhos > 0) {
            programa.setSaldo(programa.getSaldo() + pontosGanhos);
            programaFidelidadeRepository.save(programa);
        }
    }

    private Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private FidelidadeResponseDTO toResponseDTO(ProgramaFidelidade programa) {
        return FidelidadeResponseDTO.builder()
                .id(programa.getId())
                .usuarioId(programa.getUsuario().getId())
                .usuarioNome(programa.getUsuario().getNome())
                .pontosAcumulados(programa.getSaldo())
                .consentimentoPrograma(programa.getConsentimento())
                .build();
    }
}
