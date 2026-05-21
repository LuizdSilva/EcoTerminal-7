package com.ecoterminal.repository;

import com.ecoterminal.model.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Long> {

    Optional<Terminal> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
