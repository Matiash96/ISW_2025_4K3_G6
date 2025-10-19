-- =================================================================================
-- SCRIPT DE CREACIÓN Y POBLACIÓN DE LA BASE DE DATOS ECOHARMONY PARK
-- Usando SQLite
-- =================================================================================

-- Sentencias para permitir la re-ejecución del script: ELIMINAR LAS TABLAS SI EXISTEN.
-- El orden es importante para evitar problemas con las claves foráneas (FK).
DROP TABLE IF EXISTS InscripcionesXVisitantes;
DROP TABLE IF EXISTS Visitantes;
DROP TABLE IF EXISTS Inscripciones;
DROP TABLE IF EXISTS ActividadesProgramadas;
DROP TABLE IF EXISTS Actividades;
DROP TABLE IF EXISTS Tallas;


-- 1. CREACIÓN DE LA TABLA TALLA
-- Contiene las tallas de vestimenta (XS, S, M, L, XL, XXL)
CREATE TABLE Tallas (
    idTalla INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE
);

-- 2. CREACIÓN DE LA TABLA ACTIVIDADES
-- Contiene el catálogo de actividades que ofrece el parque (Tirolesa, Safari, etc.)
CREATE TABLE Actividades (
    idActividad INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    requiereVestimenta INTEGER NOT NULL, -- 1=TRUE, 0=FALSE (en la entidad esto deberia pasarse a un booleano)
    terminosYCondiciones TEXT NOT NULL
);

-- 3. CREACIÓN DE LA TABLA ACTIVIDADESPROGRAMADAS
-- Contiene las instancias específicas de las actividades que se pueden reservar
CREATE TABLE ActividadesProgramadas (
    idActividadProgramada INTEGER PRIMARY KEY AUTOINCREMENT,
    fechaHoraInicio TEXT NOT NULL, -- Almacenado como texto ISO 8601 (en la entidad esto deberia pasarse a LocalDateTime)
    fechaHoraFin TEXT NOT NULL,     -- Almacenado como texto ISO 8601 (en la entidad esto deberia pasarse a LocalDateTime)
    cupoDisponible INTEGER NOT NULL,
    idActividad INTEGER NOT NULL,
    FOREIGN KEY (idActividad) REFERENCES Actividades(idActividad)
);

-- 4. CREACIÓN DE LA TABLA INSCRIPCIONES
-- Contiene el encabezado de la inscripción de uno o varios visitantes a una ActividadProgramada
CREATE TABLE Inscripciones (
    idInscripcion INTEGER PRIMARY KEY AUTOINCREMENT,
    fechaHoraInscripcion TEXT NOT NULL DEFAULT (datetime('now', 'localtime')), -- (en la entidad esto deberia pasarse a LocalDateTime)
    aceptanTyC INTEGER NOT NULL, -- 1=TRUE, 0=FALSE (en la entidad esto deberia pasarse a un booleano)
    idActividadProgramada INTEGER NOT NULL,
    FOREIGN KEY (idActividadProgramada) REFERENCES ActividadesProgramadas(idActividadProgramada)
);

-- 5. CREACIÓN DE LA TABLA VISITANTES
-- Contiene los datos de las personas que visitan el parque y pueden inscribirse a actividades
CREATE TABLE Visitantes (
    dni INTEGER PRIMARY KEY, -- DNI como clave primaria
    nombre TEXT NOT NULL,
    edad INTEGER NOT NULL,
    idTalla INTEGER NULL, -- Puede ser nulo si la actividad no requiere vestimenta
    FOREIGN KEY (idTalla) REFERENCES Tallas(idTalla)
);

-- 6. CREACIÓN DE LA TABLA INSCRIPCIONESXVISITANTES
-- Tabla de relación N:M entre Inscripciones y Visitantes
CREATE TABLE InscripcionesXVisitantes (
    idInscripcion INTEGER NOT NULL,
    dni INTEGER NOT NULL,
    PRIMARY KEY (idInscripcion, dni),
    FOREIGN KEY (idInscripcion) REFERENCES Inscripciones(idInscripcion),
    FOREIGN KEY (dni) REFERENCES Visitantes(dni)
);

-- 1. Inserción en Talla (6 registros)
INSERT INTO Tallas (nombre) VALUES
('XS'),
('S'),
('M'),
('L'),
('XL'),
('XXL');

-- 2. Inserción en Actividades (6 registros)
INSERT INTO Actividades (nombre, requiereVestimenta, terminosYCondiciones) VALUES
('Tirolesa', 1, 'Se requiere equipo de seguridad y la talla es obligatoria.'),
('Safari', 0, 'Tour guiado por senderos. No se permite bajar del vehículo.'),
('Palestra', 1, 'Se requiere arnés. El peso máximo es de 100kg.'),
('Jardinería', 0, 'Actividad educativa. Se recomienda usar gorra y protector solar.');

-- 3. Inserción en Visitantes (6 registros)
-- Se usan DNI ficticios. Se asume que los idTalla 1-6 corresponden a XS-XXL, ya que es auto-increment
INSERT INTO Visitantes (dni, nombre, edad, idTalla) VALUES
(10111222, 'Ana Torres', 28, 3),    -- M
(20333444, 'Juan Perez', 45, 5),    -- XL
(30555666, 'Laura Gomez', 19, 2),   -- S
(40777888, 'Carlos Ruiz', 35, 4),   -- L
(50999000, 'Marta Lopez', 50, NULL),-- No asigno talla
(60101213, 'Elias Vega', 12, 1);    -- XS

-- 4. Inserción en ActividadesProgramadas (6 registros)
-- Asumiendo idActividad: 1=Tirolesa, 2=Safari, 3=Palestra, etc.
INSERT INTO ActividadesProgramadas (fechaHoraInicio, fechaHoraFin, cupoDisponible, idActividad) VALUES
('2025-10-25 10:00:00', '2025-10-25 11:00:00', 15, 1), -- Tirolesa (Req Talla)
('2025-10-25 14:00:00', '2025-10-25 15:00:00', 20, 2), -- Safari (No Req Talla)
('2025-10-26 09:30:00', '2025-10-26 10:30:00', 10, 3), -- Palestra (Req Talla)
('2025-10-26 16:00:00', '2025-10-26 17:00:00', 30, 4), -- Jardinería (No Req Talla)
('2025-10-27 11:00:00', '2025-10-27 12:00:00', 25, 2), -- Safari (No Req Talla)
('2025-10-27 15:00:00', '2025-10-27 16:00:00', 12, 3); -- Palestra (Req Talla)

-- 5. Inserción en Inscripciones (6 registros)
-- Los idActividadProgramada coinciden con los de la tabla anterior (1 a 6)
INSERT INTO Inscripciones (aceptanTyC, idActividadProgramada) VALUES
(1, 1), -- Ana y Juan se inscriben a Tirolesa
(1, 2), -- Laura se inscribe a Safari
(1, 3), -- Carlos se inscribe a Palestra
(1, 4); -- Marta y Elias se inscriben a Jardinería

-- 6. Inserción en InscripcionesXVisitantes (6 registros)
-- Relación de visitantes a inscripciones (muchos a muchos)
INSERT INTO InscripcionesXVisitantes (idInscripcion, dni) VALUES
(1, 10111222), -- Ana (M) -> Inscripción 1 (Tirolesa)
(1, 20333444), -- Juan (XL) -> Inscripción 1 (Tirolesa)
(2, 30555666), -- Laura (S) -> Inscripción 2 (Safari)
(3, 40777888), -- Carlos (L) -> Inscripción 3 (Palestra)
(4, 50999000), -- Marta (NULL Talla) -> Inscripción 4 (Jardinería)
(4, 60101213); -- Elias (XS) -> Inscripción 4 (Jardinería)
