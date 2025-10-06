# Postman

Colección de pruebas manuales para la API de Vocatio.

## Archivos

- `Vocatio-M1.postman_collection.json`: Casos de prueba del Módulo 1 (registro, login y perfil).
- `Vocatio-local.postman_environment.json`: Entorno con la URL base local (`http://localhost:8080`).

## Ejecución sugerida

1. Importa la colección y el entorno en Postman (o Newman).
2. Selecciona el entorno **Vocatio - Local** y ajusta la variable `baseUrl` si tu servidor usa otro host/puerto.
3. Ejecuta la colección completa para cubrir los escenarios felices y de error. Los datos de prueba se generan dinámicamente para evitar colisiones de email.

Los casos negativos requieren estar autenticado previamente, por lo que es recomendable correr la colección completa en el orden definido.
