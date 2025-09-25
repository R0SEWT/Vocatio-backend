# 🎓 Vocatio - Plataforma de Orientación Vocacional

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Docker](https://img.shields.io/badge/Docker-enabled-blue)

**Vocatio** es una aplicación web diseñada para apoyar la exploración vocacional de estudiantes de educación secundaria y primeros ciclos universitarios. La plataforma proporciona herramientas de evaluación vocacional, gestión de perfiles de usuario y recomendaciones personalizadas.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Prerrequisitos](#prerrequisitos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Uso de la API](#uso-de-la-api)
- [Endpoints Principales](#endpoints-principales)
- [Guía de Pruebas con Postman](#guía-de-pruebas-con-postman)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Solución de Problemas](#solución-de-problemas)

## ✨ Características

- **Gestión de Usuarios**: Registro, autenticación y perfiles personalizados
- **Evaluaciones Vocacionales**: Sistema de tests para identificar intereses y aptitudes
- **Seguridad JWT**: Autenticación segura con tokens de acceso y renovación
- **API RESTful**: Endpoints bien documentados y estructurados
- **Base de Datos Robusta**: Esquema completo para gestión de datos vocacionales
- **Containerización**: Configuración con Docker para fácil despliegue

## 🛠️ Tecnologías Utilizadas

- **Backend**: Java 21, Spring Boot 3.4.5
- **Seguridad**: Spring Security con JWT
- **Base de Datos**: PostgreSQL 16
- **ORM**: Spring Data JPA con Hibernate
- **Migración DB**: Flyway
- **Contenedores**: Docker & Docker Compose
- **Build Tool**: Maven
- **Validación**: Jakarta Validation

## 📋 Prerrequisitos

Antes de comenzar, asegúrate de tener instalado:

- **Java 21** o superior
- **Docker** y **Docker Compose**
- **Maven** (o usa el wrapper incluido)
- **Postman** (para probar la API)
- **Git** (para clonar el repositorio)

## 🚀 Instalación y Configuración

### Paso 1: Clonar el Repositorio

```bash
git clone [URL_DEL_REPOSITORIO]
cd Vocatio-backend
```

### Paso 2: Iniciar Base de Datos con Docker

La aplicación utiliza PostgreSQL corriendo en Docker. El archivo `docker-compose.yml` está configurado para usar el puerto `5434` para evitar conflictos.

```bash
docker-compose up -d
```

Esto iniciará:
- **PostgreSQL 16** en el puerto `5434`
- Base de datos: `vocatio_db`
- Usuario: `postgres`
- Contraseña: `password`

### Paso 3: Verificar que PostgreSQL esté corriendo

```bash
docker ps
```

Deberías ver algo como:
```
CONTAINER ID   IMAGE         COMMAND                  CREATED          STATUS                    PORTS                                         NAMES
d14bc8e495f6   postgres:16   "docker-entrypoint.s…"   X minutes ago   Up X minutes (healthy)   0.0.0.0:5434->5432/tcp, [::]:5434->5432/tcp   postgres_vocatio
```

### Paso 4: Configuración de la Aplicación

La aplicación está configurada en `src/main/resources/application.properties`:

```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5434/vocatio_db
spring.datasource.username=postgres
spring.datasource.password=password

# JWT Configuration
jwt.access-token-ttl=PT15M      # 15 minutos
jwt.refresh-token-ttl=PT168H    # 7 días
jwt.remember-me-ttl=P30D        # 30 días
```

### Paso 5: Ejecutar la Aplicación

#### Opción A: Con Maven Wrapper (Recomendado)

**En Windows:**
```bash
.\mvnw.cmd spring-boot:run
```

**En Unix/Linux/Mac:**
```bash
./mvnw spring-boot:run
```

#### Opción B: Con Maven instalado

```bash
mvn spring-boot:run
```

#### Opción C: Desde IDE

Ejecuta la clase `VocatioApplication.java` desde tu IDE favorito (IntelliJ IDEA, Eclipse, VS Code).

### Paso 6: Verificar que la Aplicación esté Corriendo

La aplicación estará disponible en: `http://localhost:8080`

Verificar con curl (o en el navegador):
```bash
curl http://localhost:8080/
```

**Respuesta esperada:**
```
¡Bienvenido a Vocatio API! Visita /swagger-ui/swagger-ui.html para explorar los endpoints.
```

## 📡 Endpoints Principales

### Autenticación
- `POST /auth/register` - Registrar nuevo usuario
- `POST /auth/login` - Iniciar sesión

### Perfiles
- `GET /api/profile` - Obtener perfil del usuario autenticado
- `PUT /api/profile` - Actualizar perfil
- `GET /api/profile/{userId}` - Obtener perfil por ID

### Evaluaciones
- `POST /api/v1/assessments` - Crear nueva evaluación
- `GET /api/v1/assessments/{id}` - Obtener evaluación específica

### Información General
- `GET /` - Mensaje de bienvenida
- `GET /swagger-ui/swagger-ui.html` - Documentación interactiva (si está habilitada)

## 🧪 Guía de Pruebas con Postman

### Configuración Inicial

1. **Crear nueva colección**: "Vocatio API"
2. **Variables de entorno**:
   - `baseUrl`: `http://localhost:8080`
   - `accessToken`: (se actualizará después del login)

### Secuencia de Pruebas

#### 1. Verificar Conexión
```
GET {{baseUrl}}/
```

#### 2. Registrar Usuario
```
POST {{baseUrl}}/auth/register
Content-Type: application/json

{
    "email": "usuario@ejemplo.com",
    "password": "MiPassword123",
    "rememberMe": false
}
```

**Validaciones del password:**
- Mínimo 8 caracteres
- Al menos una letra mayúscula
- Al menos un número

#### 3. Iniciar Sesión
```
POST {{baseUrl}}/auth/login
Content-Type: application/json

{
    "email": "usuario@ejemplo.com",
    "password": "MiPassword123",
    "rememberMe": false
}
```

**Guardar el `accessToken` de la respuesta para los siguientes endpoints.**

#### 4. Crear/Actualizar Perfil
```
PUT {{baseUrl}}/api/profile
Content-Type: application/json
Authorization: Bearer {{accessToken}}

{
    "name": "Juan Pérez",
    "age": 17,
    "grade": "5to de secundaria",
    "personalInterests": {
        "tecnologia": 0.8,
        "ciencias": 0.6,
        "arte": 0.4,
        "deportes": 0.7
    }
}
```

#### 5. Obtener Perfil
```
GET {{baseUrl}}/api/profile
Authorization: Bearer {{accessToken}}
```

#### 6. Crear Evaluación
```
POST {{baseUrl}}/api/v1/assessments
Content-Type: application/json
Authorization: Bearer {{accessToken}}

{
    "testType": "vocational_interest",
    "metadata": {
        "version": "1.0",
        "source": "web"
    }
}
```

## 📁 Estructura del Proyecto

```
Vocatio-backend/
├── src/
│   ├── main/
│   │   ├── java/com/acme/vocatio/
│   │   │   ├── config/          # Configuraciones de Spring
│   │   │   ├── controller/      # Controllers REST
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   ├── auth/        # DTOs de autenticación
│   │   │   │   ├── profile/     # DTOs de perfil
│   │   │   │   └── assessment/  # DTOs de evaluación
│   │   │   ├── exception/       # Manejo de excepciones
│   │   │   ├── model/           # Entidades JPA
│   │   │   ├── repository/      # Repositorios de datos
│   │   │   ├── security/        # Configuración de seguridad
│   │   │   ├── service/         # Lógica de negocio
│   │   │   │   └── impl/        # Implementaciones
│   │   │   ├── web/             # Configuraciones web
│   │   │   └── VocatioApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Tests unitarios e integración
├── BD/                          # Scripts de base de datos
│   ├── schema.sql              # Esquema de la base de datos
│   └── datos_iniciales.sql     # Datos iniciales
├── docs/                        # Documentación adicional
├── docker-compose.yml          # Configuración de Docker
├── manual.md                   # Manual del proyecto
├── pom.xml                     # Configuración Maven
└── README.md                   # Este archivo
```

## 🔧 Solución de Problemas

### Error: "Could not send request - ECONNREFUSED"

**Causa**: La aplicación Spring Boot no está corriendo.

**Solución**:
1. Verificar que PostgreSQL esté corriendo: `docker ps`
2. Iniciar la aplicación: `.\mvnw.cmd spring-boot:run`
3. Verificar que esté en el puerto 8080: `netstat -an | findstr :8080`

### Error: "Connection to PostgreSQL failed"

**Causa**: Docker no está corriendo o la base de datos no está disponible.

**Solución**:
1. Verificar Docker: `docker ps`
2. Reiniciar contenedores: `docker-compose down && docker-compose up -d`
3. Verificar salud del contenedor: `docker logs postgres_vocatio`

### Error: "JWT Token expired"

**Causa**: El token de acceso expiró (duración: 15 minutos).

**Solución**:
1. Hacer login nuevamente para obtener un nuevo token
2. Usar el refresh token (si está implementado)
3. Configurar `rememberMe: true` para tokens de mayor duración

### Error: "Validation failed"

**Causa**: Los datos enviados no cumplen con las validaciones.

**Solución**:
1. **Password**: Mínimo 8 caracteres, una mayúscula, un número
2. **Email**: Formato válido de email
3. **Edad**: Entre 12 y 120 años
4. **Nombre**: Entre 2 y 100 caracteres

### Puertos en Uso

- **PostgreSQL**: Puerto `5434` (evita conflictos con instalaciones locales)
- **Spring Boot**: Puerto `8080`

## 📝 Notas Adicionales

### Configuración de Desarrollo

- **Perfil activo**: Por defecto (desarrollo)
- **Logging SQL**: Habilitado (`spring.jpa.show-sql=true`)
- **Actualización automática del esquema**: `spring.jpa.hibernate.ddl-auto=update`

### Seguridad

- **JWT Secret**: Configurado en `application.properties`
- **CORS**: Configurado para desarrollo
- **Endpoints públicos**: `/auth/register`, `/auth/login`, `/`

### Base de Datos

- **Dialect**: PostgreSQL
- **Connection Pool**: HikariCP (por defecto en Spring Boot)
- **Flyway**: Para migraciones de esquema

---

## 👥 Contribución

Este proyecto es parte del curso **1ACC0236 - INGENIERÍA DE SOFTWARE** y está desarrollado por el equipo de **DecideClaro**.

Para contribuir:
1. Fork el repositorio
2. Crea una rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crea un Pull Request

## 📄 Licencia

Este proyecto es desarrollado con fines académicos.

---

**¡Vocatio está listo para ayudar a los estudiantes en su exploración vocacional! 🚀**
