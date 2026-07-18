package matheus.stefanello.trabalhobackend.service;

import matheus.stefanello.trabalhobackend.dto.request.PagamentoRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.PagamentoResponseDTO;
import matheus.stefanello.trabalhobackend.enums.StatusPagamento;
import matheus.stefanello.trabalhobackend.enums.StatusPedido;
import matheus.stefanello.trabalhobackend.exception.BusinessRuleException;
import matheus.stefanello.trabalhobackend.exception.PagamentoException;
import matheus.stefanello.trabalhobackend.exception.ResourceNotFoundException;
import matheus.stefanello.trabalhobackend.model.Pagamento;
import matheus.stefanello.trabalhobackend.model.Pedido;
import matheus.stefanello.trabalhobackend.repository.PagamentoRepository;
import matheus.stefanello.trabalhobackend.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PagamentoMockService pagamentoMockService;

    @Transactional
    public PagamentoResponseDTO solicitarPagamento(PagamentoRequestDTO dto) {
        // Buscar pedido
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", dto.getPedidoId()));

        // Validar status do pedido
        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new BusinessRuleException("Pedido não está aguardando pagamento");
        }

        // Verificar se já existe pagamento
        if (pagamentoRepository.findByPedidoId(pedido.getId()).isPresent()) {
            throw new BusinessRuleException("Já existe um pagamento para este pedido");
        }

        Pagamento pagamento = null;
        
        try {
            // Criar registro de pagamento
            pagamento = Pagamento.builder()
                    .pedido(pedido)
                    .valor(pedido.getValorTotal())
                    .metodoPagamento(dto.getMetodoPagamento())
                    .status(StatusPagamento.PENDENTE)
                    .build();
            pagamento = pagamentoRepository.save(pagamento);

            // Chamar mock de pagamento externo
            Map<String, Object> resultadoMock = pagamentoMockService.processarPagamento(
                    pedido.getValorTotal(), 
                    dto.getMetodoPagamento()
            );

            // Processar resposta do mock
            StatusPagamento statusPagamento = StatusPagamento.valueOf((String) resultadoMock.get("status"));
            String mensagem = (String) resultadoMock.get("mensagem");

            // Atualizar pagamento (simplificado - sem transactionId e dadosRetorno)
            pagamento.setStatus(statusPagamento);
            pagamento = pagamentoRepository.save(pagamento);

            // Atualizar status do pedido se aprovado
            if (statusPagamento == StatusPagamento.APROVADO) {
                pedido.setStatus(StatusPedido.PAGO);
                pedidoRepository.save(pedido);
            }

            return toResponseDTO(pagamento, mensagem);

        } catch (Exception e) {
            // Em caso de erro, marcar pagamento como recusado se já foi criado
            if (pagamento != null && pagamento.getId() != null) {
                pagamento.setStatus(StatusPagamento.RECUSADO);
                pagamentoRepository.save(pagamento);
            }
            throw new PagamentoException("Erro ao processar pagamento: " + e.getMessage(), e);
        }
    }

    public PagamentoResponseDTO buscarPorPedido(Long pedidoId) {
        Pagamento pagamento = pagamentoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado para o pedido", "pedidoId", pedidoId));
        return toResponseDTO(pagamento, null);
    }

    private PagamentoResponseDTO toResponseDTO(Pagamento pagamento, String mensagem) {
        return PagamentoResponseDTO.builder()
                .id(pagamento.getId())
                .pedidoId(pagamento.getPedido().getId())
                .valor(pagamento.getValor())
                .metodoPagamento(pagamento.getMetodoPagamento())
                .status(pagamento.getStatus())
                .mensagem(mensagem)
                .build();
    }
}
