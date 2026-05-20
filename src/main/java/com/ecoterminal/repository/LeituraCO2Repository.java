package com.ecoterminal.repository;

import com.ecoterminal.model.LeituraCO2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeituraCO2Repository extends JpaRepository<LeituraCO2, Long> {

    List<LeituraCO2> findByOnibusId(Long onibusId);

    List<LeituraCO2> findByOnibusIdAndDataHoraBetween(
            Long onibusId, LocalDateTime inicio, LocalDateTime fim);

    @Query("""
            SELECT l FROM LeituraCO2 l
            WHERE l.onibus.terminal.id = :terminalId
              AND l.dataHora BETWEEN :inicio AND :fim
            ORDER BY l.dataHora DESC
            """)
    List<LeituraCO2> findByTerminalIdAndPeriodo(
            @Param("terminalId") Long terminalId,
            @Param("inicio")    LocalDateTime inicio,
            @Param("fim")       LocalDateTime fim);

    @Query("""
            SELECT SUM(l.kmPercorridos) FROM LeituraCO2 l
            WHERE l.onibus.id = :onibusId
              AND YEAR(l.dataHora) = :ano
            """)
    Double sumKmPercorridosByOnibusIdAndAno(
            @Param("onibusId") Long onibusId,
            @Param("ano")      int ano);
}
