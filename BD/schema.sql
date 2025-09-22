-- Base de Datos para Plataforma de Orientación Vocacional
-- Versión Final: Modelo Funcional Completo
-- Dialecto: PostgreSQL

-- Habilitar extensiones necesarias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- -----------------------------------------------------
-- Tabla: Usuario (Solo para autenticación)
-- -----------------------------------------------------
CREATE TABLE Usuario (
    id_usuario SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    contrasena_hash VARCHAR(255) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    esta_activo BOOLEAN DEFAULT TRUE
);

-- -----------------------------------------------------
-- Tabla: Perfil (Datos del usuario dentro de la app)
-- -----------------------------------------------------
CREATE TABLE Perfil (
    id_usuario INTEGER PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE,
    avatar_url VARCHAR(255),
    estadisticas_generales JSONB,
    CONSTRAINT fk_perfil_usuario FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tablas para el Test Vocacional (Estructura)
-- -----------------------------------------------------
CREATE TABLE AreaInteres (
    id_area_interes SERIAL PRIMARY KEY,
    nombre_area VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE TestVocacional (
    id_test SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT
);

CREATE TABLE Pregunta (
    id_pregunta SERIAL PRIMARY KEY,
    id_test INTEGER NOT NULL,
    texto_pregunta TEXT NOT NULL,
    CONSTRAINT fk_pregunta_test FOREIGN KEY (id_test) REFERENCES TestVocacional(id_test) ON DELETE CASCADE
);

CREATE TABLE Opcion (
    id_opcion SERIAL PRIMARY KEY,
    id_pregunta INTEGER NOT NULL,
    id_area_interes INTEGER NOT NULL,
    texto_opcion TEXT NOT NULL,
    CONSTRAINT fk_opcion_pregunta FOREIGN KEY (id_pregunta) REFERENCES Pregunta(id_pregunta) ON DELETE CASCADE,
    CONSTRAINT fk_opcion_area FOREIGN KEY (id_area_interes) REFERENCES AreaInteres(id_area_interes)
);

-- -----------------------------------------------------
-- Tablas para el Historial y Resultados de Tests
-- -----------------------------------------------------
CREATE TABLE IntentoTest (
    id_intento SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL,
    id_test INTEGER NOT NULL,
    fecha_intento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    borrado BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_intento_usuario FOREIGN KEY (id_usuario) REFERENCES Perfil(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_intento_test FOREIGN KEY (id_test) REFERENCES TestVocacional(id_test)
);

CREATE TABLE RespuestaUsuario (
    id_respuesta SERIAL PRIMARY KEY,
    id_intento INTEGER NOT NULL,
    id_pregunta INTEGER NOT NULL,
    id_opcion_seleccionada INTEGER NOT NULL,
    CONSTRAINT fk_respuesta_intento FOREIGN KEY (id_intento) REFERENCES IntentoTest(id_intento) ON DELETE CASCADE,
    CONSTRAINT fk_respuesta_pregunta FOREIGN KEY (id_pregunta) REFERENCES Pregunta(id_pregunta),
    CONSTRAINT fk_respuesta_opcion FOREIGN KEY (id_opcion_seleccionada) REFERENCES Opcion(id_opcion)
);

CREATE TABLE ResultadoTest (
    id_resultado SERIAL PRIMARY KEY,
    id_intento INTEGER NOT NULL UNIQUE,
    resultados_por_area JSONB,
    url_informe_pdf VARCHAR(255),
    CONSTRAINT fk_resultado_intento FOREIGN KEY (id_intento) REFERENCES IntentoTest(id_intento) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tablas para Exploración de Carreras
-- -----------------------------------------------------
CREATE TABLE Carrera (
    id_carrera SERIAL PRIMARY KEY,
    nombre_carrera VARCHAR(150) NOT NULL,
    descripcion_detallada TEXT,
    perfil_requerido TEXT
);

CREATE TABLE CarrerasFavoritas (
    id_usuario INTEGER NOT NULL,
    id_carrera INTEGER NOT NULL,
    PRIMARY KEY (id_usuario, id_carrera),
    CONSTRAINT fk_favoritas_usuario FOREIGN KEY (id_usuario) REFERENCES Perfil(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_favoritas_carrera FOREIGN KEY (id_carrera) REFERENCES Carrera(id_carrera) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tablas para Módulo de Aprendizaje
-- -----------------------------------------------------
-- Crear tipo ENUM para tipo_recurso
CREATE TYPE tipo_recurso_enum AS ENUM ('pdf', 'video', 'enlace', 'testimonio');

CREATE TABLE RecursoAprendizaje (
    id_recurso SERIAL PRIMARY KEY,
    id_carrera INTEGER NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    tipo_recurso tipo_recurso_enum NOT NULL,
    url_recurso VARCHAR(255) NOT NULL,
    CONSTRAINT fk_recurso_carrera FOREIGN KEY (id_carrera) REFERENCES Carrera(id_carrera) ON DELETE CASCADE
);

CREATE TABLE RecursosGuardados (
    id_usuario INTEGER NOT NULL,
    id_recurso INTEGER NOT NULL,
    fecha_guardado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario, id_recurso),
    CONSTRAINT fk_guardados_usuario FOREIGN KEY (id_usuario) REFERENCES Perfil(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_guardados_recurso FOREIGN KEY (id_recurso) REFERENCES RecursoAprendizaje(id_recurso) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tablas para el Chatbot
-- -----------------------------------------------------
-- Crear tipo ENUM para emisor
CREATE TYPE emisor_enum AS ENUM ('usuario', 'bot');

CREATE TABLE ChatConversacion (
    id_conversacion SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL,
    fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    historial_resumido TEXT,
    CONSTRAINT fk_chat_usuario FOREIGN KEY (id_usuario) REFERENCES Perfil(id_usuario) ON DELETE CASCADE
);

CREATE TABLE ChatMensaje (
    id_mensaje SERIAL PRIMARY KEY,
    id_conversacion INTEGER NOT NULL,
    emisor emisor_enum NOT NULL,
    texto_mensaje TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mensaje_conversacion FOREIGN KEY (id_conversacion) REFERENCES ChatConversacion(id_conversacion) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Índices para mejorar el rendimiento
-- -----------------------------------------------------
CREATE INDEX idx_usuario_email ON Usuario(email);
CREATE INDEX idx_intento_usuario ON IntentoTest(id_usuario);
CREATE INDEX idx_intento_fecha ON IntentoTest(fecha_intento);
CREATE INDEX idx_respuesta_intento ON RespuestaUsuario(id_intento);
CREATE INDEX idx_chat_usuario ON ChatConversacion(id_usuario);
CREATE INDEX idx_mensaje_conversacion ON ChatMensaje(id_conversacion);
CREATE INDEX idx_mensaje_timestamp ON ChatMensaje(timestamp);