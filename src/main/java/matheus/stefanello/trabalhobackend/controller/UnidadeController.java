package matheus.stefanello.trabalhobackend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import matheus.stefanello.trabalhobackend.dto.request.UnidadeRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.ProdutoCardapioDTO;
import matheus.stefanello.trabalhobackend.model.Unidade;
import matheus.stefanello.trabalhobackend.service.UnidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
@RequiredArgsConstructor
@Validated
public class UnidadeController {

    private final UnidadeService unidadeService;

    @GetMapping
    public ResponseEntity<List<Unidade>> listarTodas(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
        List<Unidade> unidades = unidadeService.listarAtivas(page, limit);
        return ResponseEntity.ok(unidades);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Unidade> criar(@Valid @RequestBody UnidadeRequestDTO dto) {
        Unidade unidade = unidadeService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(unidade);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Unidade> buscarPorId(@PathVariable Long id) {
        Unidade unidade = unidadeService.buscarPorId(id);
        return ResponseEntity.ok(unidade);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Unidade> atualizar(@PathVariable Long id, @Valid @RequestBody UnidadeRequestDTO dto) {
        Unidade unidade = unidadeService.atualizar(id, dto);
        return ResponseEntity.ok(unidade);
    }

    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Unidade> desativar(@PathVariable Long id) {
        Unidade unidade = unidadeService.desativar(id);
        return ResponseEntity.ok(unidade);
    }

    @GetMapping("/{id}/cardapio")
    public ResponseEntity<List<ProdutoCardapioDTO>> obterCardapio(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
        List<ProdutoCardapioDTO> cardapio = unidadeService.obterCardapioPorUnidade(id, page, limit);
        return ResponseEntity.ok(cardapio);
    }
}
