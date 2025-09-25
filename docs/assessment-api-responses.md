# API REST - Respuestas según Tabla de Diseño

Este documento muestra las respuestas exactas de la API REST según la tabla de diseño proporcionada.

## 1. POST /api/v1/assessments

**Request:**
```json
{
  "testType": "Test Vocacional Básico"
}
```

**Response (201):**
```json
{
  "assessmentId": "asmt-a1b2c3d4",
  "status": "en_curso",
  "createdAt": "2025-09-23T21:56:00Z",
  "expiresAt": "2025-09-23T23:41:00Z",
  "questions": {
    "testType": "Test Vocacional Básico",
    "totalQuestions": 5,
    "currentQuestion": 1,
    "currentQuestionData": {
      "questionId": 1,
      "questionText": "¿Qué actividad te resulta más atractiva?",
      "options": [
        {
          "optionId": 1,
          "optionText": "Resolver ecuaciones matemáticas complejas",
          "areaInteres": "Ciencias Exactas"
        },
        {
          "optionId": 2,
          "optionText": "Programar una aplicación móvil",
          "areaInteres": "Tecnología"
        },
        {
          "optionId": 3,
          "optionText": "Ayudar a personas con problemas de salud",
          "areaInteres": "Ciencias de la Salud"
        },
        {
          "optionId": 4,
          "optionText": "Crear una obra de arte o diseño",
          "areaInteres": "Arte y Creatividad"
        }
      ]
    }
  }
}
```

## 2. GET /api/v1/assessments/{assessmentId}

**Response (200):**
```json
{
  "assessmentId": "asmt-a1b2c3d4",
  "status": "en_curso",
  "createdAt": "2025-09-23T21:41:00Z",
  "expiresAt": "2025-09-23T23:41:00Z",
  "questions": {
    "testType": "Test Vocacional Básico",
    "totalQuestions": 5,
    "currentQuestion": 2,
    "currentQuestionData": {
      "questionId": 2,
      "questionText": "¿En qué tipo de ambiente prefieres trabajar?",
      "options": [
        {
          "optionId": 5,
          "optionText": "En una oficina con tecnología de punta",
          "areaInteres": "Tecnología"
        },
        {
          "optionId": 6,
          "optionText": "En un hospital o clínica",
          "areaInteres": "Ciencias de la Salud"
        },
        {
          "optionId": 7,
          "optionText": "En una empresa o corporación",
          "areaInteres": "Negocios"
        },
        {
          "optionId": 8,
          "optionText": "Al aire libre en contacto con la naturaleza",
          "areaInteres": "Ciencias Ambientales"
        }
      ]
    }
  }
}
```

## 3. PUT /api/v1/assessments/{assessmentId}/answer

**Request:**
```json
{
  "questionId": 1,
  "selectedOptionId": 2
}
```

**Response (200):**
```json
{
  "assessmentId": "asmt-a1b2c3d4",
  "status": "en_curso",
  "createdAt": "2025-09-23T21:36:00Z",
  "expiresAt": "2025-09-23T23:41:00Z",
  "questions": {
    "testType": "Test Vocacional Básico",
    "totalQuestions": 5,
    "currentQuestion": 3,
    "currentQuestionData": {
      "questionId": 3,
      "questionText": "¿Qué tipo de problemas te gusta resolver?",
      "options": [
        {
          "optionId": 9,
          "optionText": "Problemas lógicos y matemáticos",
          "areaInteres": "Ciencias Exactas"
        },
        {
          "optionId": 10,
          "optionText": "Problemas sociales y comunitarios",
          "areaInteres": "Ciencias Sociales"
        },
        {
          "optionId": 11,
          "optionText": "Problemas técnicos y de ingeniería",
          "areaInteres": "Ingeniería"
        },
        {
          "optionId": 12,
          "optionText": "Problemas de comunicación y medios",
          "areaInteres": "Comunicación"
        }
      ]
    }
  }
}
```

## 4. POST /api/v1/assessments/{assessmentId}/complete

**Response (200):**
```json
{
  "assessmentId": "asmt-a1b2c3d4",
  "status": "completado",
  "completedAt": "2025-09-23T22:00:00Z",
  "results": {
    "areaScores": {
      "Tecnología": 8.5,
      "Ciencias Exactas": 7.2,
      "Ingeniería": 6.8,
      "Ciencias de la Salud": 5.4,
      "Arte y Creatividad": 4.1
    },
    "dominantArea": "Tecnología",
    "averageScore": 6.8,
    "strengthLevel": "Advanced"
  },
  "recommendations": {
    "careers": [
      {
        "careerId": 1,
        "careerName": "Ingeniería de Sistemas",
        "description": "Carrera enfocada en el diseño y desarrollo de sistemas de información",
        "matchPercentage": 92.5,
        "reasoning": "Tu alta puntuación en Tecnología e Ingeniería indica una fuerte afinidad"
      },
      {
        "careerId": 2,
        "careerName": "Ciencias de la Computación",
        "description": "Estudio teórico y práctico de algoritmos y estructuras de datos",
        "matchPercentage": 88.3,
        "reasoning": "Combina perfectamente tus intereses en Tecnología y Ciencias Exactas"
      }
    ],
    "resources": [
      {
        "resourceId": 1,
        "title": "Curso de Programación Básica",
        "type": "video",
        "url": "https://ejemplo.com/programacion-basica",
        "description": "Introducción a los conceptos fundamentales de programación"
      },
      {
        "resourceId": 2,
        "title": "Matemáticas para Ingeniería",
        "type": "pdf",
        "url": "https://ejemplo.com/matematicas.pdf",
        "description": "Fundamentos matemáticos aplicados a la ingeniería"
      }
    ],
    "nextSteps": "Te recomendamos explorar carreras en el área de Tecnología y considerar realizar cursos preparatorios en programación y matemáticas."
  }
}
```

## 5. GET /api/v1/assessments/{assessmentId}/results

**Response (200):**
```json
{
  "assessmentId": "asmt-a1b2c3d4",
  "status": "completado",
  "completedAt": "2025-09-23T22:00:00Z",
  "results": {
    "areaScores": {
      "Tecnología": 8.5,
      "Ciencias Exactas": 7.2,
      "Ingeniería": 6.8,
      "Ciencias de la Salud": 5.4,
      "Arte y Creatividad": 4.1
    },
    "dominantArea": "Tecnología",
    "averageScore": 6.8,
    "strengthLevel": "Advanced"
  },
  "recommendations": {
    "careers": [
      {
        "careerId": 1,
        "careerName": "Ingeniería de Sistemas",
        "description": "Carrera enfocada en el diseño y desarrollo de sistemas de información",
        "matchPercentage": 92.5,
        "reasoning": "Tu alta puntuación en Tecnología e Ingeniería indica una fuerte afinidad"
      }
    ],
    "resources": [
      {
        "resourceId": 1,
        "title": "Curso de Programación Básica",
        "type": "video",
        "url": "https://ejemplo.com/programacion-basica",
        "description": "Introducción a los conceptos fundamentales de programación"
      }
    ],
    "nextSteps": "Te recomendamos explorar carreras en el área de Tecnología y considerar realizar cursos preparatorios en programación y matemáticas."
  }
}
```

## 6. GET /api/v1/assessments/history

**Response (200):**
```json
[
  {
    "assessmentId": "asmt-a1b2c3d4",
    "status": "completado",
    "createdAt": "2025-09-16T22:00:00Z",
    "expiresAt": "2025-09-17T00:00:00Z",
    "questions": null
  },
  {
    "assessmentId": "asmt-e5f6g7h8",
    "status": "en_curso",
    "createdAt": "2025-09-23T21:00:00Z",
    "expiresAt": "2025-09-23T23:00:00Z",
    "questions": null
  }
]
```

## Códigos de Error

### 401 - No autorizado
```json
{
  "message": "Token de acceso inválido o expirado"
}
```

### 409 - Conflicto (ya existe evaluación en curso)
```json
{
  "message": "Ya tienes una evaluación en curso",
  "suggestions": [
    "Completa la evaluación actual",
    "O cancela la evaluación para iniciar una nueva"
  ]
}
```

### 404 - Evaluación no encontrada
```json
{
  "message": "Evaluación con ID asmt-a1b2c3d4 no encontrada",
  "suggestions": [
    "Verifique que el ID de evaluación sea correcto",
    "La evaluación puede haber expirado"
  ]
}
```

## Reglas de Negocio Aplicadas

- **RN-08**: Un usuario solo puede tener una evaluación activa a la vez
- **RN-09**: Las evaluaciones expiran después de 2 horas de inactividad