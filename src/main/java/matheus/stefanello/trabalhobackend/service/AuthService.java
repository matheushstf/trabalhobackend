package matheus.stefanello.trabalhobackend.service;

import matheus.stefanello.trabalhobackend.dto.request.LoginRequestDTO;
import matheus.stefanello.trabalhobackend.dto.request.RegisterRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.AuthResponseDTO;
import matheus.stefanello.trabalhobackend.enums.Role;
import matheus.stefanello.trabalhobackend.exception.BusinessRuleException;
import matheus.stefanello.trabalhobackend.model.Usuario;
import matheus.stefanello.trabalhobackend.repository.UsuarioRepository;
import matheus.stefanello.trabalhobackend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO dto) {
        // Validar email único
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessRuleException("Email já cadastrado");
        }

        // Validar consentimento LGPD
        if (!dto.getConsentimentoLGPD()) {
            throw new BusinessRuleException("Consentimento LGPD é obrigatório para cadastro");
        }

        // Criar usuário com role CLIENTE por padrão
        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .role(Role.ROLE_CLIENTE)
                .consentimentoLGPD(dto.getConsentimentoLGPD())
                .build();

        usuario = usuarioRepository.save(usuario);

        // Gerar token
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return buildAuthResponse(usuario, token);
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        // Autenticar
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
        );

        // Buscar usuário
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessRuleException("Usuário não encontrado"));

        // Gerar token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        return buildAuthResponse(usuario, token);
    }

    private AuthResponseDTO buildAuthResponse(Usuario usuario, String token) {
        return AuthResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .usuarioId(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .perfis(Collections.singleton(usuario.getRole().name()))
                .build();
    }
}
