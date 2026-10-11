package com.gestor.polizas.repository;

import com.gestor.polizas.entity.EstadoRiesgo;
import com.gestor.polizas.entity.Riesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiesgoRepository extends JpaRepository<Riesgo, Integer> {

    List<Riesgo> findByPolizaId(Integer polizaId);

    long countByPolizaIdAndEstado(Integer polizaId, EstadoRiesgo estado);
}
