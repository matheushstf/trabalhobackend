package matheus.stefanello.trabalhobackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import matheus.stefanello.trabalhobackend.enums.MetodoPagamento;
import matheus.stefanello.trabalhobackend.enums.StatusPagamento;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class PagamentoMockService {

    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> processarPagamento(BigDecimal valor, MetodoPagamento metodoPagamento) {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            // Simular tempo de processamento
            Thread.sleep(1000 + random.nextInt(2000));
            
            // Simular aprovação (90% de chance de aprovação)
            boolean aprovado = random.nextInt(100) < 90;
            
            String transacaoId = UUID.randomUUID().toString();
            StatusPagamento status = aprovado ? StatusPagamento.APROVADO : StatusPagamento.RECUSADO;
            String mensagem = aprovado 
                    ? "Pagamento aprovado com sucesso" 
                    : "Pagamento recusado - Saldo insuficiente ou limite excedido";
            
            resultado.put("transacaoId", transacaoId);
            resultado.put("status", status.name());
            resultado.put("mensagem", mensagem);
            resultado.put("valor", valor);
            resultado.put("metodoPagamento", metodoPagamento.name());
            resultado.put("codigoAutorizacao", aprovado ? String.format("%06d", random.nextInt(999999)) : null);
            
        } catch (InterruptedException e) {
            resultado.put("status", StatusPagamento.RECUSADO.name());
            resultado.put("mensagem", "Erro ao processar pagamento");
            Thread.currentThread().interrupt();
        }
        
        return resultado;
    }

    public String toJson(Map<String, Object> dados) {
        try {
            return objectMapper.writeValueAsString(dados);
        } catch (Exception e) {
            return "{}";
        }
    }
}
