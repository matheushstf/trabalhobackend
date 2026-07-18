package matheus.stefanello.trabalhobackend.controller;

import jakarta.validation.Valid;
import matheus.stefanello.trabalhobackend.dto.request.PedidoRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.PedidoResponseDTO;
import matheus.stefanello.trabalhobackend.enums.CanalPedido;
import matheus.stefanello.trabalhobackend.enums.StatusPedido;
import matheus.stefanello.trabalhobackend.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@Valid @RequestBody PedidoRequestDTO dto) {
        PedidoResponseDTO pedido = pedidoService.criarPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidos(
            @RequestParam(required = false) CanalPedido canalPedido,
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(required = false) Long unidadeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        
        List<PedidoResponseDTO> pedidos = pedidoService.listarPedidos(
                canalPedido, status, unidadeId, dataInicio, dataFim);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(pedido);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE', 'COZINHA')")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusPedido status) {
        PedidoResponseDTO pedido = pedidoService.atualizarStatus(id, status);
        return ResponseEntity.ok(pedido);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }
}
