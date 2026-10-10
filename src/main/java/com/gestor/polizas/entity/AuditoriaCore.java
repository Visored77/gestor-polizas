package com.gestor.polizas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_core")
@Getter
@Setter
public class AuditoriaCore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id")
    private Poliza poliza;

    @NotBlank
    @Column(name = "accion", nullable = false, length = 100)
    private String accion;

    @NotBlank
    @Lob
    @Column(name = "payload_enviado", nullable = false, columnDefinition = "TEXT")
    private String payloadEnviado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_sincronizacion")
    private EstadoSincronizacion estadoSincronizacion = EstadoSincronizacion.PENDIENTE;

    @Column(name = "intentos")
    private Integer intentos = 0;

    @Lob
    @Column(name = "respuesta_core", columnDefinition = "TEXT")
    private String respuestaCore;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;
}
