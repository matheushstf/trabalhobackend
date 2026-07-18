package matheus.stefanello.trabalhobackend.service;

import matheus.stefanello.trabalhobackend.dto.response.ProdutoCardapioDTO;
import matheus.stefanello.trabalhobackend.exception.ResourceNotFoundException;
import matheus.stefanello.trabalhobackend.model.EstoqueProduto;
import matheus.stefanello.trabalhobackend.model.Produto;
import matheus.stefanello.trabalhobackend.model.Unidade;
import matheus.stefanello.trabalhobackend.repository.EstoqueProdutoRepository;
import matheus.stefanello.trabalhobackend.repository.ProdutoRepository;
import matheus.stefanello.trabalhobackend.repository.UnidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnidadeService {

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private EstoqueProdutoRepository estoqueProdutoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Unidade> listarTodas() {
        return unidadeRepository.findAll();
    }

    public List<Unidade> listarAtivas() {
        return unidadeRepository.findByAtivaTrue();
    }

    public Unidade buscarPorId(Long id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade", "id", id));
    }

    public List<ProdutoCardapioDTO> obterCardapioPorUnidade(Long unidadeId) {
        Unidade unidade = buscarPorId(unidadeId);
        
        List<EstoqueProduto> estoqueProdutos = estoqueProdutoRepository.findByUnidadeId(unidadeId);
        
        return estoqueProdutos.stream()
                .filter(ep -> ep.getQuantidadeDisponivel() > 0)
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
