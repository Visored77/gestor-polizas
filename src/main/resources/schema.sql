-- Esquema de la base de datos del gestor de pólizas.
-- Ejecutar sobre una instancia MySQL 8.

CREATE DATABASE IF NOT EXISTS gestion_polizas
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE gestion_polizas;

-- Tabla principal de pólizas
CREATE TABLE IF NOT EXISTS poliza (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_poliza ENUM('INDIVIDUAL','COLECTIVA') NOT NULL,
    tomador_nombre VARCHAR(150) NOT NULL,
    tomador_documento VARCHAR(50) NOT NULL,
    asegurado_nombre VARCHAR(150) NOT NULL,
    asegurado_documento VARCHAR(50) NOT NULL,
    beneficiario_nombre VARCHAR(150) NOT NULL,
    beneficiario_documento VARCHAR(50) NOT NULL,
    vigencia_inicio DATE NOT NULL,
    vigencia_fin DATE NOT NULL,
    canon_mensual DECIMAL(12,2) NOT NULL,
    meses_vigencia INT NOT NULL,
    valor_prima DECIMAL(12,2) NOT NULL,
    estado ENUM('ACTIVA','RENOVADA','CANCELADA','VENCIDA') DEFAULT 'ACTIVA',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de riesgos (inmuebles amparados por una póliza)
CREATE TABLE IF NOT EXISTS riesgo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    poliza_id INT NOT NULL,
    inmueble_direccion VARCHAR(255) NOT NULL,
    arrendatario_nombre VARCHAR(150) NOT NULL,
    arrendatario_documento VARCHAR(50) NOT NULL,
    canon_individual DECIMAL(12,2) NOT NULL,
    estado ENUM('ACTIVO','CANCELADO') DEFAULT 'ACTIVO',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_poliza_riesgo FOREIGN KEY (poliza_id)
        REFERENCES poliza (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
