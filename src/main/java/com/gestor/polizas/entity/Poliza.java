package com.gestor.polizas.entity;

import com.gestor.polizas.entity.enums.EstadoPoliza;
import com.gestor.polizas.entity.enums.TipoPoliza;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poliza")
@Getter
@Setter
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_poliza", nullable = false)
    private TipoPoliza tipoPoliza;

    // Datos de los actores
    @NotBlank
    @Column(name = "tomador_nombre", nullable = false, length = 150)
    private String tomadorNombre;

    @NotBlank
    @Column(name = "tomador_documento", nullable = false, length = 50)
    private String tomadorDocumento;

    @NotBlank
    @Column(name = "asegurado_nombre", nullable = false, length = 150)
    private String aseguradoNombre;

    @NotBlank
    @Column(name = "asegurado_documento", nullable = false, length = 50)
    private String aseguradoDocumento;

    @NotBlank
    @Column(name = "beneficiario_nombre", nullable = false, length = 150)
    private String beneficiarioNombre;

    @NotBlank
    @Column(name = "beneficiario_documento", nullable = false, length = 50)
    private String beneficiarioDocumento;

    // Vigencia y valores economicos
    @NotNull
    @Column(name = "vigencia_inicio", nullable = false)
    private LocalDate vigenciaInicio;

    @NotNull
    @Column(name = "vigencia_fin", nullable = false)
    private LocalDate vigenciaFin;

    @NotNull
    @Column(name = "canon_mensual", nullable = false, precision = 12, scale = 2)
    private BigDecimal canonMensual;

    @NotNull
    @Column(name = "meses_vigencia", nullable = false)
    private Integer mesesVigencia;

    @NotNull
    @Column(name = "valor_prima", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorPrima;

    // Control y estado
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoPoliza estado = EstadoPoliza.ACTIVA;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Riesgo> riesgos = new ArrayList<>();
}
