package matheus.stefanello.trabalhobackend.exception;

public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(String message) {
        super(message);
    }
    
    public EstoqueInsuficienteException(String produto, Integer quantidadeSolicitada, Integer quantidadeDisponivel) {
        super(String.format("Estoque insuficiente para o produto '%s'. Solicitado: %d, Disponível: %d", 
                produto, quantidadeSolicitada, quantidadeDisponivel));
    }
}
