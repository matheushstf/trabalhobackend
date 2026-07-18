package matheus.stefanello.trabalhobackend.service;

import matheus.stefanello.trabalhobackend.dto.request.PromocaoRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.PromocaoResponseDTO;
import matheus.stefanello.trabalhobackend.model.Promocao;
import matheus.stefanello.trabalhobackend.repository.PromocaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;

    private final AuditLogService auditLogService;

    @Transactional
    public PromocaoResponseDTO criar(PromocaoRequestDTO dto) {
        Promocao promocao = Promocao.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .regraAplicacao(dto.getRegraAplicacao())
                .percentualDesconto(dto.getPercentualDesconto())
                .ativa(dto.getAtiva())
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .build();

        promocao = promocaoRepository.save(promocao);
        auditLogService.registrar("PROMO_CREATED", "PROMOCAO", promocao.getId(), "Promoção criada: " + promocao.getTitulo());

        return toResponseDTO(promocao);
    }

    public List<PromocaoResponseDTO> listarAtivas() {
        return listarAtivas(1, 10);
    }

    public List<PromocaoResponseDTO> listarAtivas(int page, int limit) {
        LocalDateTime agora = LocalDateTime.now();
        return promocaoRepository.findByAtivaTrueOrderByDataCriacaoDesc().stream()
                .filter(promocao -> promocao.getDataInicio() == null || !agora.isBefore(promocao.getDataInicio()))
                .filter(promocao -> promocao.getDataFim() == null || !agora.isAfter(promocao.getDataFim()))
                .skip((long) (page - 1) * limit)
                .limit(limit)
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private PromocaoResponseDTO toResponseDTO(Promocao promocao) {
        return PromocaoResponseDTO.builder()
                .id(promocao.getId())
                .titulo(promocao.getTitulo())
                .descricao(promocao.getDescricao())
                .regraAplicacao(promocao.getRegraAplicacao())
                .percentualDesconto(promocao.getPercentualDesconto())
                .ativa(promocao.getAtiva())
                .dataInicio(promocao.getDataInicio())
                .dataFim(promocao.getDataFim())
                .dataCriacao(promocao.getDataCriacao())
                .build();
    }
}