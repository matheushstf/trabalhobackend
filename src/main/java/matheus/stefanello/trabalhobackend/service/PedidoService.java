package matheus.stefanello.trabalhobackend.service;

import matheus.stefanello.trabalhobackend.dto.request.PedidoRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.PedidoResponseDTO;
import matheus.stefanello.trabalhobackend.enums.CanalPedido;
import matheus.stefanello.trabalhobackend.enums.StatusPedido;
import matheus.stefanello.trabalhobackend.exception.BusinessRuleException;
import matheus.stefanello.trabalhobackend.exception.EstoqueInsuficienteException;
import matheus.stefanello.trabalhobackend.exception.ResourceNotFoundException;
import matheus.stefanello.trabalhobackend.model.*;
import matheus.stefanello.trabalhobackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueProdutoRepository estoqueProdutoRepository;

    @Transactional
    public PedidoResponseDTO criarPedido(PedidoRequestDTO dto) {
        Unidade unidade = unidadeRepository.findById(dto.getUnidadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Unidade", "id", dto.getUnidadeId()));

        Usuario usuario = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            usuario = usuarioRepository.findByEmail(authentication.getName()).orElse(null);
        }

        Pedido pedido = Pedido.builder()
                .unidade(unidade)
                .usuario(usuario)
                .canalPedido(dto.getCanalPedido())
                .status(StatusPedido.AGUARDANDO_PAGAMENTO)
                .valorTotal(BigDecimal.ZERO)
                .itens(new ArrayList<>())
                .build();

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (PedidoRequestDTO.ItemPedidoRequestDTO itemDTO : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto", "id", itemDTO.getProdutoId()));

            EstoqueProduto estoque = estoqueProdutoRepository
                    .findByProdutoIdAndUnidadeId(produto.getId(), unidade.getId())
                    .orElseThrow(() -> new BusinessRuleException(
                            "Produto " + produto.getNome() + " não disponível nesta unidade"));

            if (estoque.getQuantidadeDisponivel() < itemDTO.getQuantidade()) {
                throw new EstoqueInsuficienteException(
                        produto.getNome(), 
                        itemDTO.getQuantidade(), 
                        estoque.getQuantidadeDisponivel()
                );
            }

            BigDecimal subtotal = produto.getPreco().multiply(new BigDecimal(itemDTO.getQuantidade()));

            ItemPedido item = ItemPedido.builder()
                    .pedido(pedido)
                    .produto(produto)
                    .quantidade(itemDTO.getQuantidade())
                    .precoUnitario(produto.getPreco())
                    .subtotal(subtotal)
                    .build();

            pedido.getItens().add(item);
            valorTotal = valorTotal.add(subtotal);

            estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() - itemDTO.getQuantidade());
            estoqueProdutoRepository.save(estoque);
        }

        pedido.setValorTotal(valorTotal);
        pedido = pedidoRepository.save(pedido);

        return toResponseDTO(pedido);
    }

    public List<PedidoResponseDTO> listarPedidos(CanalPedido canalPedido, StatusPedido status, 
                                                  Long unidadeId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<Pedido> pedidos = pedidoRepository.findByFiltros(canalPedido, status, unidadeId, dataInicio, dataFim);
        return pedidos.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        return toResponseDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(Long id, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));

        validarTransicaoStatus(pedido.getStatus(), novoStatus);

        pedido.setStatus(novoStatus);
        pedido = pedidoRepository.save(pedido);

        return toResponseDTO(pedido);
    }

    @Transactional
    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));

        if (pedido.getStatus() == StatusPedido.AGUARDANDO_PAGAMENTO || 
            pedido.getStatus() == StatusPedido.PAGO) {
            
            for (ItemPedido item : pedido.getItens()) {
                EstoqueProduto estoque = estoqueProdutoRepository
                        .findByProdutoIdAndUnidadeId(item.getProduto().getId(), pedido.getUnidade().getId())
                        .orElseThrow(() -> new BusinessRuleException("Estoque não encontrado"));

                estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() + item.getQuantidade());
                estoqueProdutoRepository.save(estoque);
            }
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }

    private void validarTransicaoStatus(StatusPedido statusAtual, StatusPedido novoStatus) {
        if (statusAtual == StatusPedido.CANCELADO) {
            throw new BusinessRuleException("Pedido já está cancelado");
        }

        if (statusAtual == StatusPedido.ENTREGUE) {
            throw new BusinessRuleException("Pedido já está entregue");
        }

        if (statusAtual == StatusPedido.AGUARDANDO_PAGAMENTO && novoStatus != StatusPedido.PAGO && novoStatus != StatusPedido.CANCELADO) {
            throw new BusinessRuleException("Status inválido. Aguardando pagamento deve ir para PAGO");
        }
    }

    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        List<PedidoResponseDTO.ItemPedidoDTO> itensDTO = pedido.getItens().stream()
                .map(item -> PedidoResponseDTO.ItemPedidoDTO.builder()
                        .id(item.getId())
                        .produtoId(item.getProduto().getId())
                        .produtoNome(item.getProduto().getNome())
                        .quantidade(item.getQuantidade())
                        .precoUnitario(item.getPrecoUnitario())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .unidadeId(pedido.getUnidade().getId())
                .unidadeNome(pedido.getUnidade().getNome())
                .usuarioId(pedido.getUsuario() != null ? pedido.getUsuario().getId() : null)
                .usuarioNome(pedido.getUsuario() != null ? pedido.getUsuario().getNome() : "Anônimo")
                .canalPedido(pedido.getCanalPedido())
                .status(pedido.getStatus())
                .valorTotal(pedido.getValorTotal())
                .itens(itensDTO)
                .dataHora(pedido.getDataHora())
                .build();
    }
}
