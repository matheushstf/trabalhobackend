package matheus.stefanello.trabalhobackend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import matheus.stefanello.trabalhobackend.dto.request.PromocaoRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.PromocaoResponseDTO;
import matheus.stefanello.trabalhobackend.service.PromocaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promocoes")
@RequiredArgsConstructor
@Validated
public class PromocaoController {

    private final PromocaoService promocaoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<PromocaoResponseDTO> criar(@Valid @RequestBody PromocaoRequestDTO dto) {
        PromocaoResponseDTO promocao = promocaoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(promocao);
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<PromocaoResponseDTO>> listarAtivas(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
        return ResponseEntity.ok(promocaoService.listarAtivas(page, limit));
    }
}