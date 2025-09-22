# Base de Datos - Plataforma de Orientación Vocacional

Este directorio contiene los scripts SQL para la base de datos de la plataforma de orientación vocacional.

## Archivos

- **`schema.sql`**: Script principal que crea todas las tablas, índices y relaciones de la base de datos
- **`datos_iniciales.sql`**: Script con datos de ejemplo y configuración inicial

## Estructura de la Base de Datos

### Diagrama de Relaciones

```
Usuario (1) ←→ (1) Perfil
    ↓
IntentoTest (N) ←→ (1) TestVocacional
    ↓                     ↓
RespuestaUsuario (N) → Pregunta (N)
    ↓                     ↓
ResultadoTest (1)    Opcion (N) → AreaInteres (1)

Perfil (N) ←→ (N) Carrera (via CarrerasFavoritas)
Carrera (1) ←→ (N) RecursoAprendizaje
Perfil (N) ←→ (N) RecursoAprendizaje (via RecursosGuardados)

Perfil (1) ←→ (N) ChatConversacion
ChatConversacion (1) ←→ (N) ChatMensaje
```

### Descripción de Tablas

#### Módulo de Usuarios
- **Usuario**: Datos de autenticación (email, contraseña)
- **Perfil**: Información personal del usuario dentro de la aplicación

#### Módulo de Tests Vocacionales
- **TestVocacional**: Tests disponibles en la plataforma
- **AreaInteres**: Áreas de interés vocacional (ej: Tecnología, Salud, Arte)
- **Pregunta**: Preguntas de cada test
- **Opcion**: Opciones de respuesta para cada pregunta
- **IntentoTest**: Registro de cada vez que un usuario realiza un test
- **RespuestaUsuario**: Respuestas específicas del usuario
- **ResultadoTest**: Resultados procesados del test

#### Módulo de Carreras
- **Carrera**: Información detallada de carreras profesionales
- **CarrerasFavoritas**: Carreras marcadas como favoritas por usuarios

#### Módulo de Aprendizaje
- **RecursoAprendizaje**: Recursos educativos (videos, PDFs, enlaces)
- **RecursosGuardados**: Recursos guardados por usuarios

#### Módulo de Chatbot
- **ChatConversacion**: Conversaciones del usuario con el chatbot
- **ChatMensaje**: Mensajes individuales de cada conversación

## Configuración

### Requisitos
- PostgreSQL 12 o superior
- Extensión `uuid-ossp` (opcional, para UUIDs)

### Instalación

1. **Crear la base de datos**:
   ```sql
   CREATE DATABASE vocatio_db;
   ```

2. **Ejecutar el esquema**:
   ```bash
   psql -d vocatio_db -f schema.sql
   ```

3. **Cargar datos iniciales** (opcional):
   ```bash
   psql -d vocatio_db -f datos_iniciales.sql
   ```

### Variables de Entorno Recomendadas

```properties
# application.properties o .env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=vocatio_db
DB_USER=vocatio_user
DB_PASSWORD=tu_password_seguro
```

## Características Técnicas

### Optimizaciones
- Índices en campos de búsqueda frecuente
- Uso de JSONB para datos flexibles
- Restricciones de integridad referencial
- Eliminación en cascada para datos relacionados

### Tipos de Datos PostgreSQL
- **SERIAL**: Para claves primarias auto-incrementales
- **JSONB**: Para datos JSON con indexación optimizada
- **TIMESTAMP**: Para fechas con zona horaria
- **ENUM**: Para valores predefinidos (tipo_recurso, emisor)

### Consideraciones de Seguridad
- Contraseñas almacenadas como hash
- Validaciones de integridad referencial
- Campos de eliminación lógica (borrado=true)

## Mantenimiento

### Copias de Seguridad
```bash
# Backup completo
pg_dump vocatio_db > backup_$(date +%Y%m%d).sql

# Backup solo datos
pg_dump --data-only vocatio_db > datos_backup_$(date +%Y%m%d).sql
```

### Restauración
```bash
# Restaurar desde backup
psql vocatio_db < backup_20241122.sql
```

---

**Versión**: 1.0  
**Última actualización**: Septiembre 2025  
**Compatibilidad**: PostgreSQL 12+