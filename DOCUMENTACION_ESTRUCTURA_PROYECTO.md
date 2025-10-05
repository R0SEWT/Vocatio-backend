# 📚 DOCUMENTACIÓN COMPLETA - Proyecto Vocatio Backend

## 📋 Índice
1. [Introducción General](#introducción-general)
2. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
3. [Base de Datos Dockerizada](#base-de-datos-dockerizada)
4. [Componentes del Proyecto](#componentes-del-proyecto)
5. [Flujo de Funcionamiento](#flujo-de-funcionamiento)
6. [Tecnologías Utilizadas](#tecnologías-utilizadas)

---

## 🎯 Introducción General

**Vocatio** es una aplicación web backend para orientación vocacional dirigida a estudiantes de secundaria y primeros ciclos universitarios. El proyecto está construido con **Spring Boot 3.4.5** y **Java 21**, siguiendo una arquitectura de capas (Layered Architecture).

### Propósito del Proyecto
- Ayudar a estudiantes a explorar carreras profesionales
- Realizar tests vocacionales personalizados
- Gestionar perfiles de usuario con autenticación segura
- Proporcionar rutas de aprendizaje personalizadas

---

## 🏗️ Arquitectura del Proyecto

El proyecto sigue el patrón **MVC (Model-View-Controller)** adaptado para APIs REST, con una arquitectura de **capas separadas**:

```
┌─────────────────────────────────────────┐
│         CAPA DE PRESENTACIÓN            │
│     (Controllers - API REST)            │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│         CAPA DE SEGURIDAD               │
│  (JWT Filters, Security Config)         │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│         CAPA DE NEGOCIO                 │
│          (Services)                     │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│         CAPA DE DATOS                   │
│     (Repositories - JPA)                │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│         BASE DE DATOS                   │
│       (PostgreSQL 16)                   │
└─────────────────────────────────────────┘
```

---

## 🐳 Base de Datos Dockerizada

### Docker Compose Configuration

Tu base de datos **PostgreSQL 16** está completamente dockerizada en un contenedor. Aquí está la configuración:

**Archivo:** `docker-compose.yml`

```yaml
services:
  postgres:
    image: postgres:16
    container_name: postgres_vocatio
    environment:
      POSTGRES_DB: vocatio_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5434:5432"   # Puerto HOST:CONTENEDOR
    volumes:
      - postgres_data:/var/lib/postgresql/data
```

### ¿Qué significa esto?

| Concepto | Explicación |
|----------|-------------|
| **Imagen** | `postgres:16` - Usa PostgreSQL versión 16 oficial |
| **Contenedor** | `postgres_vocatio` - Nombre del contenedor |
| **Puerto** | `5434` (host) → `5432` (contenedor) - Evita conflictos con otras instalaciones de PostgreSQL |
| **Volumen** | `postgres_data` - Los datos persisten aunque el contenedor se elimine |
| **Credenciales** | DB: `vocatio_db`, Usuario: `postgres`, Password: `password` |

### Comandos Docker Útiles

```bash
# Iniciar el contenedor
docker-compose up -d

# Ver logs de la base de datos
docker-compose logs postgres

# Detener el contenedor
docker-compose down

# Detener y eliminar datos
docker-compose down -v
```

### Conexión desde Spring Boot

En `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5434/vocatio_db
spring.datasource.username=postgres
spring.datasource.password=password
```

### Esquema de Base de Datos

Basado en `BD/schema.sql`, las tablas principales son:

**Autenticación:**
- `Usuario` - Credenciales de login (email, contraseña)
- `Perfil` - Datos personales del usuario

**Tests Vocacionales:**
- `TestVocacional` - Definición de tests
- `Pregunta` - Preguntas del test
- `Opcion` - Opciones de respuesta
- `AreaInteres` - Áreas vocacionales (tecnología, salud, etc.)
- `IntentoTest` - Historial de intentos
- `RespuestaUsuario` - Respuestas del usuario
- `ResultadoTest` - Resultados calculados

**Exploración:**
- `Carrera` - Catálogo de carreras profesionales

---

## 📦 Componentes del Proyecto

### 1. **MODEL (Modelo de Datos)** 📊

**Ubicación:** `src/main/java/com/acme/vocatio/model/`

Los **modelos** son clases Java que representan las **tablas de la base de datos**. Usan JPA (Java Persistence API) para mapear objetos a tablas.

#### **User.java** - Usuario del sistema
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    private Instant createdAt;
    private boolean active = true;
    
    @OneToOne(mappedBy = "user")
    private Profile profile;  // Relación 1:1 con Perfil
}
```

**¿Qué hace?**
- Representa la tabla `users` en la base de datos
- Almacena credenciales de autenticación
- `@Entity` marca la clase como una entidad JPA
- `@Id` indica la clave primaria
- `@GeneratedValue` auto-incrementa el ID
- `@OneToOne` crea una relación uno-a-uno con Profile

#### **Profile.java** - Perfil del usuario
```java
@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @Column(name = "id_usuario")
    private Long id;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private User user;  // Usa el mismo ID que User
    
    private String name;
    private Short age;
    
    @Enumerated(EnumType.STRING)
    private AcademicGrade grade;  // Enum: SECUNDARIA, UNIVERSIDAD, etc.
    
    @Column(columnDefinition = "jsonb")
    private String personalInterests;  // JSON en PostgreSQL
}
```

**¿Qué hace?**
- Almacena información personal del usuario
- Comparte el mismo ID con User (`@MapsId`)
- Usa `@Enumerated` para guardar enums como texto
- Usa tipo JSONB de PostgreSQL para datos flexibles

#### **RefreshToken.java** - Tokens de refresco
```java
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String token;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private Instant expiresAt;
    private boolean revoked;
}
```

**¿Qué hace?**
- Almacena tokens JWT de larga duración
- Permite "recordarme" y renovar sesiones
- `@ManyToOne` permite múltiples tokens por usuario

---

### 2. **REPOSITORY (Capa de Datos)** 🗄️

**Ubicación:** `src/main/java/com/acme/vocatio/repository/`

Los **repositorios** son interfaces que extienden `JpaRepository` y proporcionan métodos para **acceder a la base de datos** sin escribir SQL.

#### **UserRepository.java**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    boolean existsByEmailIgnoreCase(String email);
    
    Optional<User> findByEmailIgnoreCase(String email);
}
```

**¿Qué hace?**
- `JpaRepository<User, Long>` proporciona métodos CRUD automáticos:
  - `save()` - Guardar/actualizar
  - `findById()` - Buscar por ID
  - `findAll()` - Listar todos
  - `delete()` - Eliminar
- Spring genera la implementación automáticamente
- Métodos personalizados como `findByEmailIgnoreCase()` se implementan automáticamente por el nombre

#### **ProfileRepository.java**
```java
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);
}
```

#### **RefreshTokenRepository.java**
```java
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);
    
    List<RefreshToken> findByUserAndRevokedFalse(User user);
    
    void deleteByUser(User user);
}
```

**Spring Data JPA** traduce estos métodos a SQL automáticamente:
- `findByEmailIgnoreCase("test@mail.com")` → `SELECT * FROM users WHERE LOWER(email) = LOWER('test@mail.com')`
- `findByUserAndRevokedFalse(user)` → `SELECT * FROM refresh_tokens WHERE user_id = ? AND revoked = false`

---

### 3. **SERVICE (Lógica de Negocio)** ⚙️

**Ubicación:** `src/main/java/com/acme/vocatio/service/`

Los **servicios** contienen la **lógica de negocio** de la aplicación. Procesan datos, aplican reglas y coordinan entre repositorios.

#### **AuthService.java** - Autenticación y registro
```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Validar que el email no exista
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateEmailException("Email ya registrado");
        }
        
        // 2. Crear usuario
        User user = new User();
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);
        
        // 3. Generar tokens JWT
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.create(user, duration);
        
        // 4. Retornar respuesta
        return new AuthResponse(accessToken, refreshToken, "Registro exitoso");
    }
    
    public AuthResponse login(LoginRequest request) {
        // 1. Autenticar con Spring Security
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(), 
                request.password()
            )
        );
        
        // 2. Buscar usuario
        User user = userRepository.findByEmailIgnoreCase(request.email())
            .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));
        
        // 3. Generar tokens
        return buildAuthResponse(user, request.rememberMe());
    }
}
```

**¿Qué hace?**
- **Registro:** Valida email único, hashea contraseña con BCrypt, genera tokens JWT
- **Login:** Autentica credenciales, genera tokens
- `@Transactional` asegura que todas las operaciones de BD se completen o se reviertan juntas
- `@RequiredArgsConstructor` (Lombok) inyecta dependencias automáticamente

#### **ProfileService.java** - Gestión de perfiles
```java
@Service
@RequiredArgsConstructor
public class ProfileService {
    
    private final ProfileRepository profileRepository;
    
    public ProfileDto getProfile(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new UserNotFoundException("Perfil no encontrado"));
        return mapToDto(profile);
    }
    
    @Transactional
    public ProfileDto updateProfile(Long userId, ProfileUpdateRequest request) {
        Profile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new UserNotFoundException("Perfil no encontrado"));
        
        profile.setName(request.name());
        profile.setAge(request.age());
        profile.setGrade(request.grade());
        
        profile = profileRepository.save(profile);
        return mapToDto(profile);
    }
}
```

**¿Qué hace?**
- Obtiene y actualiza perfiles de usuario
- Convierte entidades a DTOs (Data Transfer Objects)
- Lanza excepciones personalizadas si no encuentra datos

#### **RefreshTokenService.java** - Gestión de tokens de refresco
```java
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final RefreshTokenRepository repository;
    private final JwtService jwtService;
    
    public RefreshTokenPayload create(User user, Duration ttl) {
        String tokenString = jwtService.generateRefreshToken(user, ttl);
        
        RefreshToken entity = new RefreshToken();
        entity.setUser(user);
        entity.setToken(tokenString);
        entity.setExpiresAt(Instant.now().plus(ttl));
        entity.setRevoked(false);
        
        repository.save(entity);
        return new RefreshTokenPayload(tokenString, entity.getExpiresAt());
    }
    
    public void revokeActiveTokens(User user) {
        List<RefreshToken> active = repository.findByUserAndRevokedFalse(user);
        active.forEach(t -> t.setRevoked(true));
        repository.saveAll(active);
    }
}
```

---

### 4. **CONTROLLER (Capa de Presentación)** 🎮

**Ubicación:** `src/main/java/com/acme/vocatio/controller/`

Los **controladores** exponen **endpoints HTTP REST** que reciben peticiones y devuelven respuestas JSON.

#### **AuthController.java** - Endpoints de autenticación
```java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
```

**¿Qué hace?**
- `@RestController` marca la clase como controlador REST
- `@RequestMapping("/auth")` prefija todas las rutas con `/auth`
- `@PostMapping("/register")` crea el endpoint `POST /auth/register`
- `@Valid` valida automáticamente el request con Bean Validation
- `@RequestBody` convierte JSON a objeto Java
- `ResponseEntity` permite controlar el código HTTP (200, 201, 400, etc.)

**Ejemplo de request:**
```bash
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "email": "estudiante@mail.com",
  "password": "MiPassword123",
  "rememberMe": false
}
```

**Ejemplo de response:**
```json
{
  "tokens": {
    "tokenType": "Bearer",
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "accessTokenExpiresAt": "2025-10-02T10:15:00Z",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshTokenExpiresAt": "2025-10-09T09:00:00Z"
  },
  "message": "Registro exitoso"
}
```

#### **UserProfileController.java** - Endpoints de perfil
```java
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    
    @GetMapping
    public ResponseEntity<ProfileDto> getProfile(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        ProfileDto profile = userProfileService.getProfile(principal.getUserId());
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping
    public ResponseEntity<ProfileUpdateResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        ProfileDto updated = userProfileService.updateProfile(principal.getUserId(), request);
        return ResponseEntity.ok(new ProfileUpdateResponse(updated, "Perfil actualizado"));
    }
}
```

**¿Qué hace?**
- `@GetMapping` → `GET /profile` - Obtiene perfil del usuario autenticado
- `@PutMapping` → `PUT /profile` - Actualiza perfil
- `Authentication` es inyectado por Spring Security con el usuario autenticado
- Endpoints protegidos, requieren JWT válido

---

### 5. **SECURITY (Seguridad y JWT)** 🔐

**Ubicación:** `src/main/java/com/acme/vocatio/security/` y `config/`

La seguridad está basada en **JWT (JSON Web Tokens)** para autenticación stateless.

#### **SecurityConfig.java** - Configuración de seguridad
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())  // Desactiva CSRF para API REST
            .cors(cors -> {})  // Habilita CORS
            .sessionManagement(sm -> 
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Sin sesiones
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/auth/**", "/swagger-ui/**").permitAll()  // Públicas
                .anyRequest().authenticated()  // Resto requiere autenticación
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Encriptación de contraseñas
    }
}
```

**¿Qué hace?**
- **CSRF desactivado:** Las APIs REST usan tokens, no cookies
- **Stateless:** No se guardan sesiones en el servidor
- **Rutas públicas:** `/auth/register`, `/auth/login` no requieren autenticación
- **Rutas protegidas:** Todo lo demás requiere JWT válido
- **BCrypt:** Hashea contraseñas de forma segura

#### **JwtAuthenticationFilter.java** - Filtro JWT
```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) {
        // 1. Extraer token del header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String jwt = authHeader.substring(7);  // Quitar "Bearer "
        
        // 2. Extraer email del token
        String userEmail = jwtService.extractClaim(jwt, Claims::getSubject);
        
        // 3. Validar y autenticar
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                    );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

**¿Qué hace?**
- Intercepta **todas las peticiones HTTP**
- Extrae el token JWT del header `Authorization: Bearer <token>`
- Valida el token y obtiene el usuario
- Si es válido, autentica al usuario en Spring Security
- Permite que la petición continúe al controlador

#### **JwtService.java** - Generación y validación de JWT
```java
@Service
@RequiredArgsConstructor
public class JwtService {
    
    private final JwtProperties jwtProperties;
    
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtProperties.getAccessTokenTtl());  // 15 minutos
        
        return Jwts.builder()
            .setSubject(user.getEmail())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiresAt))
            .claim("uid", user.getId())
            .claim("type", "access")
            .signWith(jwtProperties.getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractClaim(token, Claims::getSubject);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(jwtProperties.getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
```

**¿Qué hace?**
- **Genera tokens JWT** firmados con clave secreta
- **Valida tokens** verificando firma y expiración
- **Access Token:** 15 minutos de duración
- **Refresh Token:** 7 días (o 30 días con "recordarme")

#### **CustomUserDetailsService.java** - Carga de usuarios
```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        return new UserPrincipal(user);
    }
}
```

**¿Qué hace?**
- Spring Security llama este servicio para cargar datos del usuario
- Convierte `User` (entidad) a `UserDetails` (interfaz de Spring Security)

#### **UserPrincipal.java** - Usuario autenticado
```java
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {
    
    private final User user;
    
    @Override
    public String getUsername() {
        return user.getEmail();
    }
    
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
    
    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return true; }
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return user.isActive(); }
    
    public Long getUserId() {
        return user.getId();
    }
}
```

---

### 6. **DTO (Data Transfer Objects)** 📤

**Ubicación:** `src/main/java/com/acme/vocatio/dto/`

Los **DTOs** son objetos que se usan para **transferir datos** entre el cliente y el servidor. Separan la estructura interna (entidades) de la API pública.

#### **RegisterRequest.java**
```java
public record RegisterRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    boolean rememberMe
) {}
```

#### **LoginRequest.java**
```java
public record LoginRequest(
    @NotBlank @Email String email,
    @NotBlank String password,
    boolean rememberMe
) {}
```

#### **AuthResponse.java**
```java
public record AuthResponse(
    TokenBundle tokens,
    String message
) {
    public record TokenBundle(
        String tokenType,
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt
    ) {}
}
```

#### **ProfileDto.java**
```java
public record ProfileDto(
    Long id,
    String name,
    Short age,
    AcademicGrade grade,
    String personalInterests
) {}
```

**¿Por qué usar DTOs?**
- ✅ **Seguridad:** No expones contraseñas o campos internos
- ✅ **Flexibilidad:** Puedes cambiar entidades sin romper la API
- ✅ **Validación:** `@Valid` valida automáticamente con anotaciones
- ✅ **Documentación:** La API es más clara

---

### 7. **EXCEPTION (Manejo de Errores)** ⚠️

**Ubicación:** `src/main/java/com/acme/vocatio/exception/` y `web/`

#### **Excepciones Personalizadas**

```java
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
```

#### **GlobalExceptionHandler.java** - Manejo global
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(errors));
    }
}
```

**¿Qué hace?**
- `@RestControllerAdvice` captura excepciones de todos los controladores
- Convierte excepciones a respuestas JSON con códigos HTTP apropiados
- Maneja validaciones automáticamente

**Ejemplo de respuesta de error:**
```json
{
  "message": "El email ya está registrado",
  "timestamp": "2025-10-02T09:30:00Z"
}
```

---

### 8. **VALIDATION (Validaciones)** ✅

**Ubicación:** `src/main/java/com/acme/vocatio/validation/`

#### **ValidAcademicGrade.java** - Anotación personalizada
```java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAcademicGradeValidator.class)
public @interface ValidAcademicGrade {
    String message() default "Grado académico no válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

#### **ValidAcademicGradeValidator.java** - Validador
```java
public class ValidAcademicGradeValidator 
        implements ConstraintValidator<ValidAcademicGrade, AcademicGrade> {
    
    @Override
    public boolean isValid(AcademicGrade value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return EnumSet.allOf(AcademicGrade.class).contains(value);
    }
}
```

**Uso:**
```java
public record ProfileUpdateRequest(
    @NotBlank String name,
    @Min(13) @Max(25) Short age,
    @ValidAcademicGrade AcademicGrade grade
) {}
```

---

### 9. **CONFIG (Configuración)** ⚙️

**Ubicación:** `src/main/java/com/acme/vocatio/config/`

#### **JwtProperties.java** - Propiedades JWT
```java
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Validated
@Data
public class JwtProperties {
    
    @NotBlank
    private String secret;
    
    @DurationMin(minutes = 5)
    private Duration accessTokenTtl;
    
    @DurationMin(hours = 1)
    private Duration refreshTokenTtl;
    
    @DurationMin(days = 1)
    private Duration rememberMeTtl;
    
    public SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

**Configuración en `application.properties`:**
```properties
jwt.secret=06+uF0KwtD/j4LsHf8QAGhFPOUvoxMfx2bYHBvsW5Wo=
jwt.access-token-ttl=PT15M        # 15 minutos
jwt.refresh-token-ttl=PT168H      # 7 días
jwt.remember-me-ttl=P30D          # 30 días
```

---

## 🔄 Flujo de Funcionamiento

### Flujo de Registro de Usuario

```
1. Cliente → POST /auth/register {email, password}
   ↓
2. AuthController recibe la petición
   ↓
3. @Valid valida el formato (email válido, password mínimo 8 caracteres)
   ↓
4. AuthService.register()
   ├─ Verifica email no duplicado
   ├─ Hashea password con BCrypt
   ├─ Guarda User en BD (UserRepository)
   ├─ Genera Access Token JWT (15 min)
   ├─ Genera Refresh Token JWT (7 días)
   └─ Guarda Refresh Token en BD
   ↓
5. Retorna AuthResponse con tokens
   ↓
6. Cliente guarda tokens y los usa para futuras peticiones
```

### Flujo de Login

```
1. Cliente → POST /auth/login {email, password}
   ↓
2. AuthController.login()
   ↓
3. AuthenticationManager valida credenciales
   ├─ CustomUserDetailsService carga usuario por email
   ├─ PasswordEncoder compara hash
   └─ Si es válido, autentica
   ↓
4. AuthService.login()
   ├─ Genera nuevos tokens
   ├─ Revoca tokens anteriores (opcional)
   └─ Guarda nuevo Refresh Token
   ↓
5. Retorna AuthResponse con tokens
```

### Flujo de Petición Protegida

```
1. Cliente → GET /profile
   Header: Authorization: Bearer <accessToken>
   ↓
2. JwtAuthenticationFilter intercepta
   ├─ Extrae token del header
   ├─ Valida firma y expiración (JwtService)
   ├─ Extrae email del token
   ├─ Carga usuario (CustomUserDetailsService)
   └─ Autentica en SecurityContext
   ↓
3. Spring Security permite acceso al controlador
   ↓
4. UserProfileController.getProfile()
   ├─ Obtiene userId del Authentication
   ├─ UserProfileService.getProfile(userId)
   ├─ ProfileRepository.findByUserId()
   └─ Convierte Profile a ProfileDto
   ↓
5. Retorna ProfileDto como JSON
```

---

## 🛠️ Tecnologías Utilizadas

### Backend Framework
- **Spring Boot 3.4.5** - Framework principal
- **Java 21** - Lenguaje de programación

### Base de Datos
- **PostgreSQL 16** - Base de datos relacional
- **Spring Data JPA** - ORM (Object-Relational Mapping)
- **Hibernate** - Implementación JPA

### Seguridad
- **Spring Security** - Framework de seguridad
- **JWT (JSON Web Tokens)** - Autenticación stateless
- **jjwt 0.11.5** - Librería JWT
- **BCrypt** - Encriptación de contraseñas

### Validación
- **Jakarta Validation (Bean Validation)** - Validación de datos
- **Hibernate Validator** - Implementación

### Utilidades
- **Lombok** - Reduce código boilerplate (@Getter, @Setter, etc.)
- **Spring Boot DevTools** - Recarga automática en desarrollo

### Documentación
- **SpringDoc OpenAPI 2.6.0** - Genera Swagger UI automáticamente
  - URL: http://localhost:8080/swagger-ui.html

### Testing
- **Spring Boot Test** - Testing framework
- **Testcontainers** - Tests con contenedores Docker

### Contenedorización
- **Docker** - Contenedor para PostgreSQL
- **Docker Compose** - Orquestación de contenedores

### Build Tool
- **Maven** - Gestión de dependencias y construcción

---

## 📝 Comandos Útiles

### Iniciar la Base de Datos
```bash
docker-compose up -d
```

### Compilar el Proyecto
```bash
mvnw clean install
```

### Ejecutar la Aplicación
```bash
mvnw spring-boot:run
```

### Ejecutar Tests
```bash
mvnw test
```

### Acceder a Swagger
```
http://localhost:8080/swagger-ui.html
```

### Ver Logs de Docker
```bash
docker-compose logs -f postgres
```

---

## 🎓 Resumen por Componente

| Componente | Responsabilidad | Ejemplo |
|------------|----------------|---------|
| **Model** | Representar tablas de BD | `User.java`, `Profile.java` |
| **Repository** | Acceso a datos (CRUD) | `UserRepository.java` |
| **Service** | Lógica de negocio | `AuthService.java` |
| **Controller** | Endpoints REST | `AuthController.java` |
| **Security** | Autenticación JWT | `JwtService.java`, `SecurityConfig.java` |
| **DTO** | Transferencia de datos | `LoginRequest.java`, `AuthResponse.java` |
| **Exception** | Manejo de errores | `GlobalExceptionHandler.java` |
| **Validation** | Validaciones personalizadas | `ValidAcademicGrade.java` |
| **Config** | Configuración de Spring | `JwtProperties.java` |

---

## 🔐 Seguridad Implementada

✅ **Autenticación JWT** - Tokens firmados y con expiración  
✅ **Contraseñas hasheadas** - BCrypt con salt automático  
✅ **Refresh Tokens** - Renovación segura de sesiones  
✅ **Revocación de tokens** - Al hacer login nuevo  
✅ **Stateless** - No hay sesiones en servidor  
✅ **CORS habilitado** - Para frontend separado  
✅ **Validación de entrada** - Bean Validation automática  
✅ **Manejo de errores** - Respuestas consistentes  

---

## 📊 Estructura de la Base de Datos

```
┌──────────────┐         ┌──────────────┐
│    Usuario   │1──────1│   Perfil     │
│              │         │              │
│ id           │◄────────│ id_usuario   │
│ email        │         │ nombre       │
│ password     │         │ edad         │
│ created_at   │         │ grado        │
│ is_active    │         │ intereses    │
└──────┬───────┘         └──────────────┘
       │
       │1
       │
       │*
┌──────▼────────┐
│ RefreshToken  │
│               │
│ id            │
│ token         │
│ user_id       │
│ expires_at    │
│ revoked       │
└───────────────┘
```

---

## 🚀 Próximos Pasos (Funcionalidades Futuras)

Basado en el `manual.md`, el proyecto debe implementar:

- **Módulo 2:** Tests vocacionales
- **Módulo 3:** Exploración de carreras
- **Módulo 4:** Rutas de aprendizaje
- **Módulo 5:** Reportes y recomendaciones

---

**Documentación generada para el proyecto Vocatio Backend**  
**Fecha:** Octubre 2025  
**Versión:** 0.0.1-SNAPSHOT

