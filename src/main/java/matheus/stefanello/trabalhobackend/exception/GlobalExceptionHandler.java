package matheus.stefanello.trabalhobackend.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import matheus.stefanello.trabalhobackend.dto.ErrorResponseDTO;
import matheus.stefanello.trabalhobackend.dto.ErrorResponseDTO.FieldErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = ErrorResponseDTO.create(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessRuleException(
            BusinessRuleException ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = ErrorResponseDTO.create(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<ErrorResponseDTO> handleEstoqueInsuficienteException(
            EstoqueInsuficienteException ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = ErrorResponseDTO.create(
                HttpStatus.CONFLICT.value(),
                "Insufficient Stock",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(PagamentoException.class)
    public ResponseEntity<ErrorResponseDTO> handlePagamentoException(
            PagamentoException ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = ErrorResponseDTO.create(
                HttpStatus.BAD_REQUEST.value(),
                "Payment Error",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = ErrorResponseDTO.create(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = ErrorResponseDTO.create(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Credenciais inválidas",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = ErrorResponseDTO.create(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "Você não tem permissão para acessar este recurso",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        List<FieldErrorDTO> fieldErrors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(FieldErrorDTO.builder()
                    .field(error.getField())
                    .message(error.getDefaultMessage())
                    .build());
        }
        
        ErrorResponseDTO error = ErrorResponseDTO.createWithFieldErrors(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Erro de validação nos campos",
                request.getRequestURI(),
                fieldErrors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        List<FieldErrorDTO> fieldErrors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            fieldErrors.add(FieldErrorDTO.builder()
                    .field(fieldName)
                    .message(message)
                    .build());
        }
        
        ErrorResponseDTO error = ErrorResponseDTO.createWithFieldErrors(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Erro de validação",
                request.getRequestURI(),
                fieldErrors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        List<FieldErrorDTO> fieldErrors = new ArrayList<>();
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException
                && invalidFormatException.getTargetType().isEnum()) {
            String fieldName = invalidFormatException.getPath().isEmpty()
                    ? "request"
                    : invalidFormatException.getPath().get(invalidFormatException.getPath().size() - 1).getFieldName();

            fieldErrors.add(FieldErrorDTO.builder()
                    .field(fieldName)
                    .message(buildEnumErrorMessage(fieldName, invalidFormatException.getTargetType()))
                    .build());
        }

        ErrorResponseDTO error = fieldErrors.isEmpty()
                ? ErrorResponseDTO.create(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        "JSON inválido ou mal formatado",
                        request.getRequestURI())
                : ErrorResponseDTO.createWithFieldErrors(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Error",
                        "Erro de validação nos campos",
                        request.getRequestURI(),
                        fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        List<FieldErrorDTO> fieldErrors = new ArrayList<>();
        Class<?> requiredType = ex.getRequiredType();
        String fieldName = ex.getName();

        fieldErrors.add(FieldErrorDTO.builder()
                .field(fieldName)
                .message(requiredType != null && requiredType.isEnum()
                        ? buildEnumErrorMessage(fieldName, requiredType)
                        : "Valor inválido para o campo " + fieldName)
                .build());

        ErrorResponseDTO error = ErrorResponseDTO.createWithFieldErrors(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Erro de validação nos campos",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = ErrorResponseDTO.create(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = ErrorResponseDTO.create(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde.",
                request.getRequestURI()
        );
        
        // Log the full exception for debugging
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

        private String buildEnumErrorMessage(String fieldName, Class<?> enumType) {
                String acceptedValues = Arrays.stream(enumType.getEnumConstants())
                                .map(Object::toString)
                                .reduce((first, second) -> first + ", " + second)
                                .orElse("");

                return "Valor inválido para o campo " + fieldName + ". Valores aceitos: " + acceptedValues;
        }
}
