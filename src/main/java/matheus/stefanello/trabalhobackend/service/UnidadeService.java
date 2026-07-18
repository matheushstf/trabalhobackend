package matheus.stefanello.trabalhobackend.service;

import matheus.stefanello.trabalhobackend.dto.request.UnidadeRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.ProdutoCardapioDTO;
import matheus.stefanello.trabalhobackend.exception.ResourceNotFoundException;
import matheus.stefanello.trabalhobackend.model.EstoqueProduto;
import matheus.stefanello.trabalhobackend.model.Produto;
import matheus.stefanello.trabalhobackend.model.Unidade;
import matheus.stefanello.trabalhobackend.repository.EstoqueProdutoRepository;
import matheus.stefanello.trabalhobackend.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;

    private final EstoqueProdutoRepository estoqueProdutoRepository;

    private final AuditLogService auditLogService;

    public List<Unidade> listarTodas() {
        return unidadeRepository.findAll();
    }

    public List<Unidade> listarAtivas() {
        return unidadeRepository.findByAtivaTrue();
    }

    public List<Unidade> listarAtivas(int page, int limit) {
        return unidadeRepository.findByAtivaTrue(PageRequest.of(page - 1, limit)).getContent();
    }

    public Unidade buscarPorId(Long id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade", "id", id));
    }

    @Transactional
    public Unidade criar(UnidadeRequestDTO dto) {
        Unidade unidade = Unidade.builder()
                .nome(dto.getNome())
                .endereco(dto.getEndereco())
                .telefone(dto.getTelefone())
                .ativa(true)
                .build();

        unidade = unidadeRepository.save(unidade);
        auditLogService.registrar("UNIT_CREATED", "UNIDADE", unidade.getId(), "Unidade criada: " + unidade.getNome());

        return unidade;
    }

    @Transactional
    public Unidade atualizar(Long id, UnidadeRequestDTO dto) {
        Unidade unidade = buscarPorId(id);

        unidade.setNome(dto.getNome());
        unidade.setEndereco(dto.getEndereco());
        unidade.setTelefone(dto.getTelefone());

        unidade = unidadeRepository.save(unidade);
        auditLogService.registrar("UNIT_UPDATED", "UNIDADE", unidade.getId(), "Unidade atualizada: " + unidade.getNome());

        return unidade;
    }

    @Transactional
    public Unidade desativar(Long id) {
        Unidade unidade = buscarPorId(id);
        unidade.setAtiva(false);
        unidade = unidadeRepository.save(unidade);
        auditLogService.registrar("UNIT_DEACTIVATED", "UNIDADE", unidade.getId(), "Unidade desativada: " + unidade.getNome());

        return unidade;
    }

    public List<ProdutoCardapioDTO> obterCardapioPorUnidade(Long unidadeId, int page, int limit) {
        List<EstoqueProduto> estoqueProdutos = estoqueProdutoRepository.findByUnidadeId(unidadeId);
        
        return estoqueProdutos.stream()
                .filter(ep -> ep.getQuantidadeDisponivel() > 0)
                .skip((long) (page - 1) * limit)
                .limit(limit)
                .map(ep -> {
                    Produto produto = ep.getProduto();
                    return ProdutoCardapioDTO.builder()
                            .id(produto.getId())
                            .nome(produto.getNome())
                            .preco(produto.getPreco())
                            .categoria(produto.getCategoria())
                            .disponivelEstoque(ep.getQuantidadeDisponivel() > 0)
                            .quantidadeDisponivel(ep.getQuantidadeDisponivel())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
