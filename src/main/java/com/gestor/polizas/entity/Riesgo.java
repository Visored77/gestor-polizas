package com.gestor.polizas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "riesgo")
@Getter
@Setter
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id", nullable = false)
    @JsonIgnore
    private Poliza poliza;

    @NotBlank
    @Column(name = "inmueble_direccion", nullable = false, length = 255)
    private String inmuebleDireccion;

    @NotBlank
    @Column(name = "arrendatario_nombre", nullable = false, length = 150)
    private String arrendatarioNombre;

    @NotBlank
    @Column(name = "arrendatario_documento", nullable = false, length = 50)
    private String arrendatarioDocumento;

    @NotNull
    @Column(name = "canon_individual", nullable = false, precision = 12, scale = 2)
    private BigDecimal canonIndividual;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoRiesgo estado = EstadoRiesgo.ACTIVO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
