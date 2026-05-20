package com.ecoterminal.service;

import com.ecoterminal.dto.CadastroDTO;
import com.ecoterminal.model.Usuario;
import com.ecoterminal.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder   passwordEncoder;

    // ── Spring Security — carrega usuário pelo e-mail ─────────────────────────
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com e-mail: " + email));
    }

    // ── Cadastro ──────────────────────────────────────────────────────────────
    @Transactional
    public void cadastrar(CadastroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }
        if (!dto.senhasConferem()) {
            throw new IllegalArgumentException("As senhas não conferem.");
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .senha(passwordEncoder.encode(dto.senha()))
                .role(Usuario.Role.OPERADOR)   // novo cadastro sempre como OPERADOR
                .ativo(true)
                .build();

        usuarioRepository.save(usuario);
    }

    // ── Verifica se já existe algum usuário (primeiro acesso) ─────────────────
    @Transactional(readOnly = true)
    public boolean existeAlgumUsuario() {
        return usuarioRepository.count() > 0;
    }
}
