package com.ecoterminal.repository;

import com.ecoterminal.enums.PadraoMotor;
import com.ecoterminal.enums.TipoOnibus;
import com.ecoterminal.model.Onibus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnibusRepository extends JpaRepository<Onibus, Long> {

    Optional<Onibus> findByPrefixo(String prefixo);

    List<Onibus> findByTerminalId(Long terminalId);

    List<Onibus> findByTipo(TipoOnibus tipo);

    List<Onibus> findByPadraoMotor(PadraoMotor padraoMotor);

    List<Onibus> findByTerminalIdAndPadraoMotor(Long terminalId, PadraoMotor padraoMotor);

    long countByTerminalId(Long terminalId);

    @Query("SELECT SUM(o.kmAnuais) FROM Onibus o WHERE o.terminal.id = :terminalId")
    Double sumKmAnuaisByTerminalId(@Param("terminalId") Long terminalId);
}
