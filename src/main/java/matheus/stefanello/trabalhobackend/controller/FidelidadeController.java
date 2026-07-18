package matheus.stefanello.trabalhobackend.controller;

import jakarta.validation.Valid;
import matheus.stefanello.trabalhobackend.dto.request.ResgateRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.FidelidadeResponseDTO;
import matheus.stefanello.trabalhobackend.service.FidelidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fidelidade")
public class FidelidadeController {

    @Autowired
    private FidelidadeService fidelidadeService;

    @PostMapping("/aderir")
    public ResponseEntity<FidelidadeResponseDTO> aderir() {
        FidelidadeResponseDTO fidelidade = fidelidadeService.aderirPrograma();
        return ResponseEntity.status(HttpStatus.CREATED).body(fidelidade);
    }

    @GetMapping("/meus-pontos")
    public ResponseEntity<FidelidadeResponseDTO> obterMeusPontos() {
        FidelidadeResponseDTO fidelidade = fidelidadeService.obterMeusPontos();
        return ResponseEntity.ok(fidelidade);
    }

    @PostMapping("/resgatar")
    public ResponseEntity<FidelidadeResponseDTO> resgatar(@Valid @RequestBody ResgateRequestDTO dto) {
        FidelidadeResponseDTO fidelidade = fidelidadeService.resgatarPontos(dto);
        return ResponseEntity.ok(fidelidade);
    }
}
