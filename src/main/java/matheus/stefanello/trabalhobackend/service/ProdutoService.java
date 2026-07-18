package matheus.stefanello.trabalhobackend.service;

import lombok.RequiredArgsConstructor;
import matheus.stefanello.trabalhobackend.exception.ResourceNotFoundException;
import matheus.stefanello.trabalhobackend.model.Produto;
import matheus.stefanello.trabalhobackend.repository.ProdutoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public List<Produto> listarTodos(int page, int limit) {
        return produtoRepository.findAll(PageRequest.of(page - 1, limit)).getContent();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", "id", id));
    }
}
