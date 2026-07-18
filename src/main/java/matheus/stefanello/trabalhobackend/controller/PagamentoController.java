package matheus.stefanello.trabalhobackend.controller;

import jakarta.validation.Valid;
import matheus.stefanello.trabalhobackend.dto.request.PagamentoRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.PagamentoResponseDTO;
import matheus.stefanello.trabalhobackend.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping("/solicitar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagamentoResponseDTO> solicitarPagamento(@Valid @RequestBody PagamentoRequestDTO dto) {
        PagamentoResponseDTO pagamento = pagamentoService.solicitarPagamento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamento);
    }

    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagamentoResponseDTO> buscarPorPedido(@PathVariable Long pedidoId) {
        PagamentoResponseDTO pagamento = pagamentoService.buscarPorPedido(pedidoId);
        return ResponseEntity.ok(pagamento);
    }
}
