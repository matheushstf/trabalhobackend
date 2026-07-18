package matheus.stefanello.trabalhobackend.controller;

import jakarta.validation.Valid;
import matheus.stefanello.trabalhobackend.dto.request.ResgateRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.FidelidadeResponseDTO;
import matheus.stefanello.trabalhobackend.service.FidelidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fidelidade")
@RequiredArgsConstructor
public class FidelidadeController {

    private final FidelidadeService fidelidadeService;

    @PostMapping("/aderir")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FidelidadeResponseDTO> aderir() {
        FidelidadeResponseDTO fidelidade = fidelidadeService.aderirPrograma();
        return ResponseEntity.status(HttpStatus.CREATED).body(fidelidade);
    }

    @GetMapping("/meus-pontos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FidelidadeResponseDTO> obterMeusPontos() {
        FidelidadeResponseDTO fidelidade = fidelidadeService.obterMeusPontos();
        return ResponseEntity.ok(fidelidade);
    }

    @PostMapping("/resgatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FidelidadeResponseDTO> resgatar(@Valid @RequestBody ResgateRequestDTO dto) {
        FidelidadeResponseDTO fidelidade = fidelidadeService.resgatarPontos(dto);
        return ResponseEntity.ok(fidelidade);
    }
}
