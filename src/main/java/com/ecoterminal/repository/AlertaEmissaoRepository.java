package com.ecoterminal.repository;

import com.ecoterminal.model.AlertaEmissao;
import com.ecoterminal.model.AlertaEmissao.Severidade;
import com.ecoterminal.model.AlertaEmissao.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AlertaEmissaoRepository extends JpaRepository<AlertaEmissao, Long> {

    List<AlertaEmissao> findByOnibusId(Long onibusId);

    List<AlertaEmissao> findByOnibusTerminalIdAndReconhecidoFalse(Long terminalId);

    List<AlertaEmissao> findBySeveridade(Severidade severidade);

    List<AlertaEmissao> findByTipo(TipoAlerta tipo);

    long countByOnibusTerminalIdAndReconhecidoFalse(Long terminalId);
    
    @Query("""
            SELECT COUNT(a) > 0 FROM AlertaEmissao a
            WHERE a.onibus.id = :onibusId
              AND a.tipo      = :tipo
              AND a.reconhecido = false
            """)
    boolean existsAtivoByOnibusIdAndTipo(
            @Param("onibusId") Long onibusId,
            @Param("tipo")     TipoAlerta tipo);
}
