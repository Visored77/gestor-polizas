package com.gestor.polizas.repository;

import com.gestor.polizas.entity.EstadoPoliza;
import com.gestor.polizas.entity.Poliza;
import com.gestor.polizas.entity.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Integer> {

    /**
     * Lista polizas filtrando opcionalmente por tipo y/o estado.
     * Si un parametro es null, no se aplica ese filtro.
     */
    @Query("SELECT p FROM Poliza p "
            + "WHERE (:tipo IS NULL OR p.tipoPoliza = :tipo) "
            + "AND (:estado IS NULL OR p.estado = :estado)")
    List<Poliza> buscarPorFiltros(@Param("tipo") TipoPoliza tipo,
                                  @Param("estado") EstadoPoliza estado);
}
