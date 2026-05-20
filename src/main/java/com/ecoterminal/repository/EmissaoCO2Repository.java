package com.ecoterminal.repository;

import com.ecoterminal.model.EmissaoCO2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmissaoCO2Repository extends JpaRepository<EmissaoCO2, Long> {

    List<EmissaoCO2> findByOnibusId(Long onibusId);

    Optional<EmissaoCO2> findByOnibusIdAndAnoReferencia(Long onibusId, int anoReferencia);

    List<EmissaoCO2> findByAnoReferencia(int anoReferencia);

    @Query("""
            SELECT e FROM EmissaoCO2 e
            WHERE e.onibus.terminal.id = :terminalId
              AND e.anoReferencia = :ano
            ORDER BY e.co2Toneladas DESC
            """)
    List<EmissaoCO2> findByTerminalIdAndAno(
            @Param("terminalId") Long terminalId,
            @Param("ano")        int ano);

    @Query("""
            SELECT SUM(e.co2Toneladas) FROM EmissaoCO2 e
            WHERE e.onibus.terminal.id = :terminalId
              AND e.anoReferencia = :ano
            """)
    Double sumCo2ToneladasByTerminalIdAndAno(
            @Param("terminalId") Long terminalId,
            @Param("ano")        int ano);

    @Query("""
            SELECT e FROM EmissaoCO2 e
            WHERE e.onibus.terminal.id = :terminalId
              AND e.anoReferencia = :ano
              AND (e.conformeCo2 = false
                OR e.conformeMp  = false
                OR e.conformeNox = false)
            """)
    List<EmissaoCO2> findNaoConformesByTerminalIdAndAno(
            @Param("terminalId") Long terminalId,
            @Param("ano")        int ano);
}
