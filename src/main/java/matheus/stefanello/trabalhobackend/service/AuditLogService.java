package matheus.stefanello.trabalhobackend.service;

import matheus.stefanello.trabalhobackend.model.AuditLog;
import matheus.stefanello.trabalhobackend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void registrar(String evento, String entidade, Long entidadeId, String detalhes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioEmail = null;

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            usuarioEmail = authentication.getName();
        }

        registrar(evento, entidade, entidadeId, usuarioEmail, detalhes);
    }

    @Transactional
    public void registrar(String evento, String entidade, Long entidadeId, String usuarioEmail, String detalhes) {
        AuditLog auditLog = AuditLog.builder()
                .evento(evento)
                .entidade(entidade)
                .entidadeId(entidadeId)
                .usuarioEmail(usuarioEmail)
                .detalhes(detalhes)
                .build();

        auditLogRepository.save(auditLog);
    }
}