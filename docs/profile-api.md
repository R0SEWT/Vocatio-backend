# API REST - Módulo de Perfil

Esta documentación describe los endpoints REST disponibles para el módulo de perfil de usuario en la plataforma Vocatio.

## Endpoints Disponibles

### 1. Obtener Perfil del Usuario Autenticado
```http
GET /api/profile
Authorization: Bearer {token}
```

**Respuesta exitosa (200):**
```json
{
  "id": 1,
  "name": "Juan Pérez",
  "age": 17,
  "grade": "11°",
  "email": "juan.perez@email.com",
  "personalInterests": {
    "areas": {
      "Tecnología": 8.5,
      "Ciencias": 7.2,
      "Arte": 5.8,
      "Deportes": 6.4
    },
    "dominantArea": "Tecnología",
    "averageScore": 7.0
  }
}
```

### 2. Obtener Perfil por ID de Usuario
```http
GET /api/profile/{userId}
Authorization: Bearer {token}
```

### 3. Actualizar Perfil del Usuario Autenticado
```http
PUT /api/profile
Authorization: Bearer {token}
Content-Type: application/json
```

**Cuerpo de la petición:**
```json
{
  "name": "Juan Carlos Pérez",
  "age": 18,
  "grade": "12°",
  "personalInterests": {
    "Tecnología": 9.0,
    "Ciencias": 7.5,
    "Arte": 6.0,
    "Deportes": 6.8,
    "Negocios": 5.2
  }
}
```

**Validaciones:**
- `name`: Obligatorio, entre 2 y 100 caracteres
- `age`: Obligatorio, entre 12 y 120 años
- `grade`: Obligatorio, máximo 50 caracteres
- `personalInterests`: Opcional, mapa de áreas con puntuaciones

### 4. Obtener Estadísticas de Intereses
```http
GET /api/profile/interests/statistics
Authorization: Bearer {token}
```

**Respuesta exitosa (200):**
```json
{
  "areaScores": {
    "Tecnología": 9.0,
    "Ciencias": 7.5,
    "Arte": 6.0,
    "Deportes": 6.8,
    "Negocios": 5.2
  },
  "dominantArea": "Tecnología",
  "averageScore": 6.9,
  "totalTests": 1,
  "strengthLevel": "Advanced"
}
```

### 5. Obtener Estadísticas por ID de Usuario
```http
GET /api/profile/{userId}/interests/statistics
Authorization: Bearer {token}
```

### 6. Verificar Estado del Perfil
```http
GET /api/profile/status
Authorization: Bearer {token}
```

**Respuesta exitosa (200):**
```json
{
  "exists": true,
  "isComplete": true,
  "message": "Perfil completo",
  "interestsCount": 5
}
```

## Códigos de Estado

| Código | Descripción |
|--------|-------------|
| 200 | Éxito |
| 400 | Error de validación |
| 401 | No autorizado |
| 404 | Perfil no encontrado |
| 500 | Error interno del servidor |

## Niveles de Fortaleza (strengthLevel)

- **Starter**: 0.0 - 2.0
- **Beginner**: 2.0 - 4.0  
- **Intermediate**: 4.0 - 6.0
- **Advanced**: 6.0 - 8.0
- **Expert**: 8.0 - 10.0

## Ejemplos de Errores

### Perfil no encontrado (404)
```json
{
  "message": "Perfil para el usuario con ID 123 no encontrado",
  "suggestions": [
    "Verifique que el usuario exista",
    "Complete el perfil si es necesario"
  ]
}
```

### Error de validación (400)
```json
{
  "message": "Revise los datos enviados",
  "errors": {
    "name": ["El nombre es obligatorio"],
    "age": ["La edad mínima es 12 años"]
  }
}
```

## Autenticación

Todos los endpoints requieren autenticación mediante Bearer Token JWT. El token debe incluirse en el header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Notas Importantes

1. **Intereses Personales**: Se almacenan como JSON en la base de datos y se mapean automáticamente
2. **Área Dominante**: Se calcula automáticamente como el área con mayor puntuación
3. **Puntuación Promedio**: Promedio de todas las áreas de interés
4. **Perfil Completo**: Requiere nombre, edad, grado y al menos un interés personal