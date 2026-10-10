package com.gestor.polizas.repository;

import com.gestor.polizas.entity.AuditoriaCore;
import com.gestor.polizas.entity.EstadoSincronizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaCoreRepository extends JpaRepository<AuditoriaCore, Integer> {

    List<AuditoriaCore> findByPolizaId(Integer polizaId);

    List<AuditoriaCore> findByEstadoSincronizacion(EstadoSincronizacion estado);
}
