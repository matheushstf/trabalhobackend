package matheus.stefanello.trabalhobackend.controller;

import jakarta.validation.Valid;
import matheus.stefanello.trabalhobackend.dto.request.UsuarioUpdateRequestDTO;
import matheus.stefanello.trabalhobackend.dto.response.UsuarioResponseDTO;
import matheus.stefanello.trabalhobackend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequestDTO dto) {
        UsuarioResponseDTO usuario = usuarioService.atualizar(id, dto);
        return ResponseEntity.ok(usuario);
    }
}
