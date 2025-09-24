#%%

 -- ==========================================
-- Vocatio DB - Versión Básica Completa
-- PostgreSQL 14+
-- ==========================================

-- Extensiones útiles
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS citext;

-- ======================
-- 1. Usuarios y Perfiles
-- ======================
CREATE TABLE Usuario (
                         id_usuario SERIAL PRIMARY KEY,
                         email CITEXT NOT NULL UNIQUE,
                         contrasena_hash VARCHAR(255) NOT NULL,
                         fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         esta_activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE Perfil (
                        id_usuario INTEGER PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        edad SMALLINT CHECK (edad BETWEEN 13 AND 30),
                        grado VARCHAR(50),
                        intereses JSONB,  -- lista básica de intereses
                        CONSTRAINT fk_perfil_usuario FOREIGN KEY (id_usuario)
                            REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- ======================
-- 2. Test Vocacional
-- ======================
CREATE TABLE AreaInteres (
                             id_area SERIAL PRIMARY KEY,
                             nombre VARCHAR(100) NOT NULL UNIQUE,
                             descripcion TEXT
);

CREATE TABLE TestVocacional (
                                id_test SERIAL PRIMARY KEY,
                                nombre VARCHAR(150) NOT NULL,
                                descripcion TEXT
);

CREATE TYPE tipo_pregunta_enum AS ENUM ('single','multiple');
CREATE TABLE Pregunta (
                          id_pregunta SERIAL PRIMARY KEY,
                          id_test INTEGER NOT NULL REFERENCES TestVocacional(id_test) ON DELETE CASCADE,
                          texto TEXT NOT NULL,
                          tipo tipo_pregunta_enum NOT NULL DEFAULT 'single',
                          item_order INTEGER
);

CREATE TABLE Opcion (
                        id_opcion SERIAL PRIMARY KEY,
                        id_pregunta INTEGER NOT NULL REFERENCES Pregunta(id_pregunta) ON DELETE CASCADE,
                        id_area INTEGER NOT NULL REFERENCES AreaInteres(id_area),
                        texto TEXT NOT NULL
);

CREATE TYPE estado_intento_enum AS ENUM ('draft','completed');
CREATE TABLE IntentoTest (
                             id_intento SERIAL PRIMARY KEY,
                             id_usuario INTEGER NOT NULL REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
                             id_test INTEGER NOT NULL REFERENCES TestVocacional(id_test),
                             fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             fecha_fin TIMESTAMP,
                             estado estado_intento_enum NOT NULL DEFAULT 'draft'
);

CREATE TABLE RespuestaUsuario (
                                  id_respuesta SERIAL PRIMARY KEY,
                                  id_intento INTEGER NOT NULL REFERENCES IntentoTest(id_intento) ON DELETE CASCADE,
                                  id_pregunta INTEGER NOT NULL REFERENCES Pregunta(id_pregunta),
                                  id_opcion INTEGER NOT NULL REFERENCES Opcion(id_opcion),
                                  UNIQUE (id_intento, id_pregunta)
);

CREATE TABLE ResultadoTest (
                               id_resultado SERIAL PRIMARY KEY,
                               id_intento INTEGER NOT NULL UNIQUE REFERENCES IntentoTest(id_intento) ON DELETE CASCADE,
                               resultados_por_area JSONB,
                               top_areas JSONB,
                               resumen TEXT
);

-- ======================
-- 3. Carreras y Favoritos
-- ======================
CREATE TABLE Carrera (
                         id_carrera SERIAL PRIMARY KEY,
                         nombre VARCHAR(150) NOT NULL,
                         descripcion TEXT,
                         id_area INTEGER REFERENCES AreaInteres(id_area) ON DELETE SET NULL
);

CREATE TABLE CarrerasFavoritas (
                                   id_usuario INTEGER NOT NULL REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
                                   id_carrera INTEGER NOT NULL REFERENCES Carrera(id_carrera) ON DELETE CASCADE,
                                   PRIMARY KEY (id_usuario, id_carrera)
);

-- ======================
-- 4. Ruta de Aprendizaje
-- ======================
CREATE TYPE tipo_recurso_enum AS ENUM ('pdf','video','enlace');
CREATE TABLE RecursoAprendizaje (
                                    id_recurso SERIAL PRIMARY KEY,
                                    id_carrera INTEGER NOT NULL REFERENCES Carrera(id_carrera) ON DELETE CASCADE,
                                    titulo VARCHAR(255) NOT NULL,
                                    tipo tipo_recurso_enum NOT NULL,
                                    url VARCHAR(255) NOT NULL
);

CREATE TABLE RecursosGuardados (
                                   id_usuario INTEGER NOT NULL REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
                                   id_recurso INTEGER NOT NULL REFERENCES RecursoAprendizaje(id_recurso) ON DELETE CASCADE,
                                   fecha_guardado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (id_usuario, id_recurso)
);

-- ======================
-- 5. Reportes (vista simple)
-- ======================
CREATE OR REPLACE VIEW v_reporte_usuario AS
SELECT
    u.id_usuario,
    u.email,
    p.nombre,
    r.id_resultado,
    r.resultados_por_area,
    r.top_areas,
    (
        SELECT jsonb_agg(c.nombre)
        FROM CarrerasFavoritas cf
                 JOIN Carrera c ON c.id_carrera = cf.id_carrera
        WHERE cf.id_usuario = u.id_usuario
    ) AS carreras_favoritas,
    (
        SELECT jsonb_agg(rac.titulo)
        FROM RecursosGuardados rg
                 JOIN RecursoAprendizaje rac ON rac.id_recurso = rg.id_recurso
        WHERE rg.id_usuario = u.id_usuario
    ) AS recursos_guardados
FROM Usuario u
         JOIN Perfil p ON p.id_usuario = u.id_usuario
         LEFT JOIN IntentoTest it ON it.id_usuario = u.id_usuario AND it.estado='completed'
         LEFT JOIN ResultadoTest r ON r.id_intento = it.id_intento;
