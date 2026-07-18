package matheus.stefanello.trabalhobackend.service;

import matheus.stefanello.trabalhobackend.dto.request.UsuarioUpdateRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.UsuarioResponseDTO;
import matheus.stefanello.trabalhobackend.exception.ResourceNotFoundException;
import matheus.stefanello.trabalhobackend.exception.UnauthorizedException;
import matheus.stefanello.trabalhobackend.model.Usuario;
import matheus.stefanello.trabalhobackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioResponseDTO> listarTodos(int page, int limit) {
        return usuarioRepository.findAll(PageRequest.of(page - 1, limit)).getContent().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        // Verificar se o usuário pode acessar este recurso
        verificarPermissaoAcesso(usuario);
        
        return toResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioUpdateRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        // Verificar se o usuário pode editar este recurso
        verificarPermissaoEdicao(usuario);
        
        // Atualizar dados básicos apenas
        usuario.setNome(dto.getNome());
        
        usuario = usuarioRepository.save(usuario);
        return toResponseDTO(usuario);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .consentimentoLGPD(usuario.getConsentimentoLGPD())
                .perfis(Collections.singleton(usuario.getRole().name()))
                .dataCriacao(usuario.getDataCriacao())
                .build();
    }

    private void verificarPermissaoAcesso(Usuario usuario) {
        if (!isAdmin() && !isProprioUsuario(usuario.getEmail())) {
            throw new UnauthorizedException("Você não tem permissão para acessar este recurso");
        }
    }

    private void verificarPermissaoEdicao(Usuario usuario) {
        if (!isAdmin() && !isProprioUsuario(usuario.getEmail())) {
            throw new UnauthorizedException("Você não tem permissão para editar este recurso");
        }
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isProprioUsuario(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName().equals(email);
    }
}
