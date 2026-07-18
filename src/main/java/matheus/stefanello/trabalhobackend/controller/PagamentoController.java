package matheus.stefanello.trabalhobackend.controller;

import jakarta.validation.Valid;
import matheus.stefanello.trabalhobackend.dto.request.PagamentoRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.PagamentoResponseDTO;
import matheus.stefanello.trabalhobackend.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping("/solicitar")
    public ResponseEntity<PagamentoResponseDTO> solicitarPagamento(@Valid @RequestBody PagamentoRequestDTO dto) {
        PagamentoResponseDTO pagamento = pagamentoService.solicitarPagamento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamento);
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagamentoResponseDTO> buscarPorPedido(@PathVariable Long pedidoId) {
        PagamentoResponseDTO pagamento = pagamentoService.buscarPorPedido(pedidoId);
        return ResponseEntity.ok(pagamento);
    }
}
