-- Datos de ejemplo para el gestor de pólizas.
-- Requiere el esquema ya creado (ver schema.sql).
-- Ejecutar: mysql ... gestion_polizas < data.sql
--
-- Contenido: 10 pólizas individuales (1 riesgo cada una)
--            5 pólizas colectivas con 3, 4, 5, 6 y 7 riesgos.
-- La prima = canon_mensual * meses_vigencia (12).

USE gestion_polizas;

-- Limpia datos previos (respetando la FK: primero riesgos)
DELETE FROM riesgo;
DELETE FROM poliza;

-- ============================================================
-- 10 PÓLIZAS INDIVIDUALES (tomador = asegurado = arrendatario)
-- ============================================================
INSERT INTO poliza
(tipo_poliza, tomador_nombre, tomador_documento, asegurado_nombre, asegurado_documento,
 beneficiario_nombre, beneficiario_documento, vigencia_inicio, vigencia_fin,
 canon_mensual, meses_vigencia, valor_prima, estado)
VALUES
('INDIVIDUAL','Arrendatario Ind 01','CC1001','Arrendatario Ind 01','CC1001','Arrendador 01','CC2001','2026-01-01','2027-01-01',1000000.00,12,12000000.00,'ACTIVA'),
('INDIVIDUAL','Arrendatario Ind 02','CC1002','Arrendatario Ind 02','CC1002','Arrendador 02','CC2002','2026-01-01','2027-01-01',1100000.00,12,13200000.00,'ACTIVA'),
('INDIVIDUAL','Arrendatario Ind 03','CC1003','Arrendatario Ind 03','CC1003','Arrendador 03','CC2003','2026-01-01','2027-01-01',1200000.00,12,14400000.00,'ACTIVA'),
('INDIVIDUAL','Arrendatario Ind 04','CC1004','Arrendatario Ind 04','CC1004','Arrendador 04','CC2004','2026-01-01','2027-01-01',1300000.00,12,15600000.00,'ACTIVA'),
('INDIVIDUAL','Arrendatario Ind 05','CC1005','Arrendatario Ind 05','CC1005','Arrendador 05','CC2005','2026-01-01','2027-01-01',1400000.00,12,16800000.00,'ACTIVA'),
('INDIVIDUAL','Arrendatario Ind 06','CC1006','Arrendatario Ind 06','CC1006','Arrendador 06','CC2006','2026-01-01','2027-01-01',1500000.00,12,18000000.00,'ACTIVA'),
('INDIVIDUAL','Arrendatario Ind 07','CC1007','Arrendatario Ind 07','CC1007','Arrendador 07','CC2007','2026-01-01','2027-01-01',1600000.00,12,19200000.00,'ACTIVA'),
('INDIVIDUAL','Arrendatario Ind 08','CC1008','Arrendatario Ind 08','CC1008','Arrendador 08','CC2008','2026-01-01','2027-01-01',1700000.00,12,20400000.00,'ACTIVA'),
('INDIVIDUAL','Arrendatario Ind 09','CC1009','Arrendatario Ind 09','CC1009','Arrendador 09','CC2009','2026-01-01','2027-01-01',1800000.00,12,21600000.00,'ACTIVA'),
('INDIVIDUAL','Arrendatario Ind 10','CC1010','Arrendatario Ind 10','CC1010','Arrendador 10','CC2010','2026-01-01','2027-01-01',1900000.00,12,22800000.00,'ACTIVA');

-- Un riesgo por cada póliza individual
INSERT INTO riesgo (poliza_id, inmueble_direccion, arrendatario_nombre, arrendatario_documento, canon_individual, estado)
SELECT p.id, CONCAT('Calle ', p.tomador_documento, ' # ', p.id),
       p.asegurado_nombre, p.asegurado_documento, p.canon_mensual, 'ACTIVO'
FROM poliza p
WHERE p.tipo_poliza = 'INDIVIDUAL';

-- ============================================================
-- 5 PÓLIZAS COLECTIVAS (tomador = inmobiliaria) con 3,4,5,6,7 riesgos
-- ============================================================
INSERT INTO poliza
(tipo_poliza, tomador_nombre, tomador_documento, asegurado_nombre, asegurado_documento,
 beneficiario_nombre, beneficiario_documento, vigencia_inicio, vigencia_fin,
 canon_mensual, meses_vigencia, valor_prima, estado)
VALUES
('COLECTIVA','Inmobiliaria Alfa SAS','NIT3001','Arrendatarios Alfa','NIT4001','Arrendadores Alfa','NIT5001','2026-01-01','2027-01-01',3000000.00,12,36000000.00,'ACTIVA'),
('COLECTIVA','Inmobiliaria Beta SAS','NIT3002','Arrendatarios Beta','NIT4002','Arrendadores Beta','NIT5002','2026-01-01','2027-01-01',4000000.00,12,48000000.00,'ACTIVA'),
('COLECTIVA','Inmobiliaria Gamma SAS','NIT3003','Arrendatarios Gamma','NIT4003','Arrendadores Gamma','NIT5003','2026-01-01','2027-01-01',5000000.00,12,60000000.00,'ACTIVA'),
('COLECTIVA','Inmobiliaria Delta SAS','NIT3004','Arrendatarios Delta','NIT4004','Arrendadores Delta','NIT5004','2026-01-01','2027-01-01',6000000.00,12,72000000.00,'ACTIVA'),
('COLECTIVA','Inmobiliaria Epsilon SAS','NIT3005','Arrendatarios Epsilon','NIT4005','Arrendadores Epsilon','NIT5005','2026-01-01','2027-01-01',7000000.00,12,84000000.00,'ACTIVA');

SET @alfa    = (SELECT id FROM poliza WHERE tomador_documento='NIT3001');
SET @beta    = (SELECT id FROM poliza WHERE tomador_documento='NIT3002');
SET @gamma   = (SELECT id FROM poliza WHERE tomador_documento='NIT3003');
SET @delta   = (SELECT id FROM poliza WHERE tomador_documento='NIT3004');
SET @epsilon = (SELECT id FROM poliza WHERE tomador_documento='NIT3005');

-- Alfa: 3 riesgos
INSERT INTO riesgo (poliza_id, inmueble_direccion, arrendatario_nombre, arrendatario_documento, canon_individual, estado) VALUES
(@alfa,'Torre Alfa Apto 101','Arrendatario Alfa 1','CCA101',1000000.00,'ACTIVO'),
(@alfa,'Torre Alfa Apto 102','Arrendatario Alfa 2','CCA102',1000000.00,'ACTIVO'),
(@alfa,'Torre Alfa Apto 103','Arrendatario Alfa 3','CCA103',1000000.00,'ACTIVO');

-- Beta: 4 riesgos
INSERT INTO riesgo (poliza_id, inmueble_direccion, arrendatario_nombre, arrendatario_documento, canon_individual, estado) VALUES
(@beta,'Torre Beta Apto 201','Arrendatario Beta 1','CCB201',1000000.00,'ACTIVO'),
(@beta,'Torre Beta Apto 202','Arrendatario Beta 2','CCB202',1000000.00,'ACTIVO'),
(@beta,'Torre Beta Apto 203','Arrendatario Beta 3','CCB203',1000000.00,'ACTIVO'),
(@beta,'Torre Beta Apto 204','Arrendatario Beta 4','CCB204',1000000.00,'ACTIVO');

-- Gamma: 5 riesgos
INSERT INTO riesgo (poliza_id, inmueble_direccion, arrendatario_nombre, arrendatario_documento, canon_individual, estado) VALUES
(@gamma,'Torre Gamma Apto 301','Arrendatario Gamma 1','CCG301',1000000.00,'ACTIVO'),
(@gamma,'Torre Gamma Apto 302','Arrendatario Gamma 2','CCG302',1000000.00,'ACTIVO'),
(@gamma,'Torre Gamma Apto 303','Arrendatario Gamma 3','CCG303',1000000.00,'ACTIVO'),
(@gamma,'Torre Gamma Apto 304','Arrendatario Gamma 4','CCG304',1000000.00,'ACTIVO'),
(@gamma,'Torre Gamma Apto 305','Arrendatario Gamma 5','CCG305',1000000.00,'ACTIVO');

-- Delta: 6 riesgos
INSERT INTO riesgo (poliza_id, inmueble_direccion, arrendatario_nombre, arrendatario_documento, canon_individual, estado) VALUES
(@delta,'Torre Delta Apto 401','Arrendatario Delta 1','CCD401',1000000.00,'ACTIVO'),
(@delta,'Torre Delta Apto 402','Arrendatario Delta 2','CCD402',1000000.00,'ACTIVO'),
(@delta,'Torre Delta Apto 403','Arrendatario Delta 3','CCD403',1000000.00,'ACTIVO'),
(@delta,'Torre Delta Apto 404','Arrendatario Delta 4','CCD404',1000000.00,'ACTIVO'),
(@delta,'Torre Delta Apto 405','Arrendatario Delta 5','CCD405',1000000.00,'ACTIVO'),
(@delta,'Torre Delta Apto 406','Arrendatario Delta 6','CCD406',1000000.00,'ACTIVO');

-- Epsilon: 7 riesgos
INSERT INTO riesgo (poliza_id, inmueble_direccion, arrendatario_nombre, arrendatario_documento, canon_individual, estado) VALUES
(@epsilon,'Torre Epsilon Apto 501','Arrendatario Epsilon 1','CCE501',1000000.00,'ACTIVO'),
(@epsilon,'Torre Epsilon Apto 502','Arrendatario Epsilon 2','CCE502',1000000.00,'ACTIVO'),
(@epsilon,'Torre Epsilon Apto 503','Arrendatario Epsilon 3','CCE503',1000000.00,'ACTIVO'),
(@epsilon,'Torre Epsilon Apto 504','Arrendatario Epsilon 4','CCE504',1000000.00,'ACTIVO'),
(@epsilon,'Torre Epsilon Apto 505','Arrendatario Epsilon 5','CCE505',1000000.00,'ACTIVO'),
(@epsilon,'Torre Epsilon Apto 506','Arrendatario Epsilon 6','CCE506',1000000.00,'ACTIVO'),
(@epsilon,'Torre Epsilon Apto 507','Arrendatario Epsilon 7','CCE507',1000000.00,'ACTIVO');
