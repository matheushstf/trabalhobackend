package matheus.stefanello.trabalhobackend.controller;

import matheus.stefanello.trabalhobackend.dto.response.ProdutoCardapioDTO;
import matheus.stefanello.trabalhobackend.model.Unidade;
import matheus.stefanello.trabalhobackend.service.UnidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
public class UnidadeController {

    @Autowired
    private UnidadeService unidadeService;

    @GetMapping
    public ResponseEntity<List<Unidade>> listarTodas() {
        List<Unidade> unidades = unidadeService.listarAtivas();
        return ResponseEntity.ok(unidades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Unidade> buscarPorId(@PathVariable Long id) {
        Unidade unidade = unidadeService.buscarPorId(id);
        return ResponseEntity.ok(unidade);
    }

    @GetMapping("/{id}/cardapio")
    public ResponseEntity<List<ProdutoCardapioDTO>> obterCardapio(@PathVariable Long id) {
        List<ProdutoCardapioDTO> cardapio = unidadeService.obterCardapioPorUnidade(id);
        return ResponseEntity.ok(cardapio);
    }
}
