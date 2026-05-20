package com.ecoterminal.config;

import com.ecoterminal.model.Usuario;
import com.ecoterminal.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Inicialização de dados essenciais na primeira execução.
 *
 * Cria o usuário administrador padrão caso não exista nenhum cadastro.
 * Em produção, troque a senha padrão imediatamente após o primeiro acesso.
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder   passwordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            if (usuarioRepository.findByEmail("admin@ecoterminal.com").isEmpty()) {
                Usuario admin = Usuario.builder()
                        .nome("Administrador")
                        .email("admin@ecoterminal.com")
                        .senha(passwordEncoder.encode("admin123"))
                        .role(Usuario.Role.ADMIN)
                        .ativo(true)
                        .build();
                usuarioRepository.save(admin);
                log.warn("Admin padrão criado — troque a senha em produção! " +
                         "[admin@ecoterminal.com / admin123]");
            } else {
                log.info("Usuário admin já existe — inicialização ignorada.");
            }
        };
    }
}