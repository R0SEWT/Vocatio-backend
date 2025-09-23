    1ACC0236– INGENIERÍA DE SOFTWARE
Proyecto 2: Orientación vocacional y exploración profesional
Título del proyecto
Diseño e Implementación de una Aplicación Web para el Apoyo en la Exploración Vocacional
de Estudiantes de Educación Secundaria y Primeros Ciclos Universitarios
Objetivo
Diseñar e implementar una aplicación web que permita a estudiantes acceder a recursos de
orientación vocacional como pruebas, perfiles ocupacionales y materiales formativos,
facilitando un proceso de exploración más informado, personalizado y accesible
Segmentos objetivo
Estudiantes de secundaria (últimos años) que se encuentran en proceso de decidir su futura
carrera o campo profesional.
Estudiantes de primeros ciclos universitarios que aún tienen dudas sobre su vocación o desean
reafirmar su elección
Visión del Proyecto
Este proyecto nace a partir de la inquietud del equipo por las dificultades que enfrentan
muchos jóvenes al momento de elegir una carrera, ya sea por falta de orientación, exceso de
opciones o desinformación. Basándose en experiencias personales y cercanas, el equipo decide
desarrollar una propuesta digital que facilite este proceso de exploración vocacional.
La startup académica DecideClaro tiene como visión ofrecer una plataforma accesible,
amigable y confiable que acompañe a estudiantes en la identificación de sus intereses, la
exploración de opciones profesionales y la reflexión personal, contribuyendo así a decisiones
más informadas y satisfactorias.
A partir de esta necesidad, surge Vocatio, una aplicación web orientada a apoyar la
exploración vocacional de estudiantes en etapa escolar o universitaria temprana. El alcance
incluye el diseño, implementación y validación del producto con usuarios reales en contextos
educativos accesibles.
1ACC0236– INGENIERÍA DE SOFTWARE
Propuesta preliminar de funcionalidades
Esta es una propuesta inicial de funcionalidades. El equipo de trabajo es libre de agregar,
actualizar o refinar funcionalidades en función de los hallazgos obtenidos durante las
entrevistas.
Módulo 1: Gestión de cuenta y perfil de usuario
Permite al usuario registrarse, acceder y personalizar su perfil básico.
Funcionalidades:
● Crear cuenta y acceder mediante login.
● Completar perfil con edad, grado educativo e intereses generales.
● Editar datos personales o cambiar contraseña.
● Eliminar cuenta y todos los datos asociados con confirmación.
Módulo 2: Evaluación de intereses vocacionales
El usuario realiza una prueba vocacional y recibe sugerencias personalizadas.
Funcionalidades:
● Responder test vocacional de opción múltiple.
● Calcular resultados con áreas de interés predominantes y sugerir carreras afines.
● Guardar resultado de la evaluación y visualizar intentos anteriores.
● Eliminar evaluaciones anteriores si ya no son relevantes.
Módulo 3: Exploración de carreras y profesiones
Permite conocer diversas opciones académicas según el perfil del usuario.
Funcionalidades:
● Listar fichas de carrera disponibles (nombre, duración, modalidad, descripción).
● Filtrar carreras por área de interés, duración o tipo de formación (técnica,
universitaria).
● Ver detalle de una carrera seleccionada.
● Marcar carreras como favoritas o eliminarlas de favoritos.
1ACC0236– INGENIERÍA DE SOFTWARE
Módulo 4: Ruta de Aprendizaje Personalizada
Brinda materiales alineados al perfil vocacional para continuar explorando y prepararse.
Funcionalidades:
● Ver materiales sugeridos por carrera o área vocacional (videos, lecturas, enlaces).
● Guardar materiales como favoritos.
● Descargar materiales en PDF o guardar enlaces externos.
● Eliminar materiales guardados si ya no son de interés.
Módulo 5: Reportes y recomendaciones
Ofrece una vista global del avance del usuario y sugerencias complementarias.
Funcionalidades:
● Visualizar gráfico con intereses predominantes (barras o radar).
● Ver lista de carreras recomendadas en base al test vocacional.
● Descargar informe vocacional en PDF (resultados, carreras favoritas, recursos
revisados).
● Comparar resultados entre intentos anteriores.
R




<El modulo y las 4 issues que componen el modulo para las cuales debemos eleaborar lo siguiente>

El título de la historia de usuario.
La descripción de la historia (COMO, QUIERO, PARA).
Los criterios de aceptación (formato Given–When–Then) por escenarios.
La asignación individual por integrante.
El estado de avance (Por hacer, En progreso, Hecho).
El tablero debe estar organizado y actualizado, demostrando el seguimiento del trabajo por parte del equipo.



Empecemos con el modulo 1 #1, y sus sub-issues #2, #3, #4 y #5




**Resumen:** Backlog listo del Módulo 1 (Gestión de cuenta y perfil) con 1 epic (#1) y 4 historias (#2–#5), cada una con historia , criterios Given–When–Then, responsable y estado.

---

# Módulo 1 — Gestión de cuenta y perfil

**Issue #1 (Epic):** *Gestión de cuenta y perfil de usuario*
#### Descripción :

* **COMO** estudiante que explora opciones vocacionales
* **QUIERO** crear una cuenta, iniciar sesión y administrar mi perfil
* **PARA** acceder a evaluaciones, guardar resultados y personalizar recomendaciones
  **Alcance:** Registro/login, perfil básico, edición/cambio de contraseña, eliminación de cuenta.
  #### Criterios de aceptación  (alto nivel):**
* Todas las historias #2–#5 están completadas y probadas, con pruebas de regresión básicas de auth.
* Flujo feliz y errores comunes cubiertos (emails inválidos, duplicados, credenciales incorrectas, validaciones).
  **Asignado a:** PM/Tech Lead
  **Estado:** Por hacer

---

## Historia #2: Registro e inicio de sesión

**Título:** *Registro y autenticación de usuarios (email + contraseña)*
#### Descripción :

* **COMO** nuevo usuario
* **QUIERO** registrarme con email y contraseña e iniciar sesión
* **PARA** acceder a mi cuenta y persistir mis avances

#### Criterios de aceptación:

1. **Registro exitoso**

* *Given* formulario con email válido y contraseña que cumple políticas
* *When* envío el formulario
* *Then* se crea la cuenta, recibo confirmación y quedo autenticado (sesión iniciada)

2. **Email ya registrado**

* *Given* email existente
* *When* intento registrarme
* *Then* veo mensaje “El email ya está registrado” y se sugiere iniciar sesión o recuperar contraseña

3. **Password policy**

* *Given* contraseña < mínimos (p.ej., 8+ chars, 1 mayúscula, 1 número)
* *When* envío el formulario
* *Then* se muestran errores específicos por regla incumplida

4. **Login exitoso**

* *Given* cuenta verificada y credenciales correctas
* *When* inicio sesión
* *Then* accedo al dashboard autenticado

5. **Login fallido**

* *Given* email o contraseña incorrectos
* *When* intento iniciar sesión
* *Then* veo mensaje “Credenciales inválidas” (sin revelar cuál campo falla)

6. **Persistencia de sesión**

* *Given* “Recordarme” marcado
* *When* cierro y reabro el navegador
* *Then* sigo autenticado hasta expirar el token/refresh o cerrar sesión

**Asignación individual:**

* **FE:** Implementar formularios/validaciones y UI de errores
* **BE:** Endpoints `/auth/register`, `/auth/login`, emisión de tokens/refresh
* **QA:** Casos de prueba positivos/negativos, políticas de contraseña, persistencia de sesión
* **UX:** Microcopys de error y estados vacíos

**Estado:** Por hacer

---

## Historia #3: Completar perfil básico

**Título:** *Completar perfil con edad, grado educativo e intereses*
#### Descripción :

* **COMO** usuario autenticado
* **QUIERO** completar/actualizar mi edad, grado y áreas de interés
* **PARA** recibir recomendaciones y rutas de aprendizaje más relevantes

#### Criterios de aceptación:

1. **Guardar perfil**

* *Given* formulario con edad (numérica), grado (catálogo) e intereses (multi-selección)
* *When* guardo cambios
* *Then* se persisten y se confirma visualmente (toast “Perfil actualizado”)

2. **Validaciones**

* *Given* edad fuera de rango permitido (p.ej., 13–30) o campos obligatorios vacíos
* *When* guardo
* *Then* veo errores por campo y no se guarda

3. **Precarga**

* *Given* ya tengo datos guardados
* *When* abro “Mi perfil”
* *Then* los campos aparecen pre-llenados

4. **Impacto en recomendaciones**

* *Given* intereses actualizados
* *When* voy a “Exploración de carreras”
* *Then* las sugerencias reflejan los nuevos intereses

**Asignación individual:**

* **FE:** UI/estado del formulario, chips/multiselect de intereses
* **BE:** Endpoints `/users/me` GET/PUT, validaciones servidor
* **QA:** Casuística de validaciones, pre-llenado y consistencia con recomendaciones
* **UX:** Taxonomía de intereses y texto de ayuda

**Estado:** Por hacer

---

## Historia #4: Edición de datos y cambio de contraseña

**Título:** *Editar datos personales y cambiar contraseña*
#### Descripción :

* **COMO** usuario autenticado
* **QUIERO** actualizar mis datos personales y cambiar mi contraseña con seguridad
* **PARA** mantener mi información al día y protegida

#### Criterios de aceptación:

1. **Editar datos personales**

* *Given* formulario con nombre y preferencias no sensibles
* *When* guardo
* *Then* los cambios se persisten y se confirma la actualización

2. **Cambiar contraseña — éxito**

* *Given* contraseña actual correcta y nueva contraseña que cumple la política
* *When* envío el cambio
* *Then* la contraseña se actualiza y se solicita re-login si el token es inválido

3. **Cambiar contraseña — fallos**

* *Given* contraseña actual incorrecta o nueva que no cumple política
* *When* envío
* *Then* se muestran errores claros sin revelar información sensible

4. **Seguridad**

* *Given* múltiples intentos fallidos de cambio
* *When* se supera el umbral (p.ej., 5 intentos)
* *Then* se aplica rate limiting y mensaje de espera

**Asignación individual:**

* **FE:** Vistas y estados de carga/éxito/error
* **BE:** Endpoints `/users/me` (PATCH) y `/auth/change-password`, hashing y rate limit
* **QA:** Pruebas de política de contraseñas y reautenticación
* **UX:** Mensajes de error comprensibles

**Estado:** Por hacer

---

## Historia #5: Eliminación de cuenta y datos

**Título:** *Eliminar cuenta y borrado completo de datos (GDPR-like)*
#### Descripción :

* **COMO** usuario
* **QUIERO** eliminar mi cuenta y todos mis datos con confirmación explícita
* **PARA** ejercer control sobre mi información

#### Criterios de aceptación:

1. **Confirmación explícita**

* *Given* usuario autenticado en “Eliminar cuenta”
* *When* confirma escribiendo “ELIMINAR” y reingresando contraseña
* *Then* la cuenta y datos asociados (perfil, evaluaciones, favoritos) se borran de forma irreversible

2. **Revocación de sesión**

* *Given* cuenta eliminada
* *When* intento volver a usar la sesión o credenciales anteriores
* *Then* el acceso es inválido y se informa que la cuenta no existe

3. **Exportación opcional previa**

* *Given* quiero una copia antes de borrar
* *When* solicito “Descargar mis datos”
* *Then* recibo un ZIP/PDF con reporte y JSON/CSV de mis registros (si está habilitado para esta release, sino mostrar “próximamente”)

4. **Tiempos y colas**

* *Given* borrado puede ser asíncrono
* *When* se inicia el proceso
* *Then* se marca la cuenta como “pendiente de borrado” y se notifica al completar

**Asignación individual:**

* **FE:** UI de confirmación, doble verificación y feedback de progreso
* **BE:** Endpoint `/users/me` DELETE, borrado en cascada, revocación de tokens
* **QA:** Verificación de borrado total (DB), sesiones inválidas, exportación previa
* **UX:** Mensajes de advertencia claros

**Estado:** Por hacer

---

## Sugerencia de organización del tablero (GitHub Projects o similar)

* **Columnas:** Por hacer → En progreso → En revisión QA → Hecho
* **Etiquetas:** `epic`, `auth`, `perfil`, `seguridad`, `backend`, `frontend`, `qa`, `ux`
* **Dependencias:** #1 (epic) ➜ #2, #3, #4, #5 (bloqueadas por #2 para acceso autenticado)
* **Definición de Hecho (DoD) común:** criterios cumplidos, tests unitarios/integ., manejo de errores, mensajes UX listos, y casos QA documentados.



**Resumen:** Backlog del Módulo 2 (Evaluación vocacional) listo: 1 epic (#6) + 4 historias (#7–#10) con COMO/QUIERO/PARA, criterios Given–When–Then, asignación y estado.

---

# Módulo 2 — Evaluación de intereses vocacionales

**Issue #6 (Epic):** *Evaluación vocacional end-to-end*
**COMO** estudiante • **QUIERO** rendir un test y recibir resultados accionables • **PARA** orientar mi exploración de carreras.
**Alcance:** test de opción múltiple, cálculo de resultados, almacenamiento/consulta de intentos, y borrado de intentos.
**Cierre del Epic:** #7–#10 completadas, con flujo feliz + errores cubiertos, y reporte mínimo disponible en #9.
**Asignado a:** PM/Tech Lead • **Estado:** Por hacer

---

## Historia #7: Rendir test vocacional (UI + flujo)

**Título:** *Presentar test de opción múltiple con progreso guardado*
**COMO** usuario autenticado • **QUIERO** responder un test con guardado de progreso • **PARA** completarlo sin perder respuestas.

**Criterios (Given–When–Then):**

1. **Carga de test**

* *Given* estoy logueado
* *When* abro “Nueva evaluación”
* *Then* veo preguntas paginadas (p.ej., 30–60 ítems), barra de progreso y opción “Guardar y continuar luego”.

2. **Validaciones de respuesta**

* *Given* una página con preguntas obligatorias
* *When* intento avanzar sin responder
* *Then* se marcan las faltantes y no avanzo.

3. **Auto-guardado**

* *Given* llevo N respuestas
* *When* cierro la pestaña
* *Then* al volver, el intento aparece como “En curso” con respuestas previas.

4. **Accesibilidad básica**

* *Given* uso teclado/lector de pantalla
* *When* navego el test
* *Then* puedo responder y avanzar (roles/labels ARIA en inputs y botones).

**Asignación:**

* **FE:** UI paginada, validaciones, autosave local + sync
* **BE:** `/assessments` (POST draft), `/assessments/{id}` (PATCH for answers)
* **QA:** casos de pérdida de conexión y recuperación
* **UX:** microcopys y estructura de páginas

**Estado:** Por hacer

---

## Historia #8: Calcular resultados y sugerir áreas/carreras

**Título:** *Scoring + mapeo a áreas de interés y carreras afines*
**COMO** usuario que finaliza el test • **QUIERO** ver mis áreas predominantes y sugerencias • **PARA** saber qué explorar.

**Criterios (Given–When–Then):**

1. **Cálculo exitoso**

* *Given* intento completado
* *When* envío “Finalizar”
* *Then* el backend calcula puntajes por área (p.ej., RIASEC u otra taxonomía), devuelve top-3 y lista de carreras sugeridas (≥5).

2. **Visualización clara**

* *Given* tengo resultados
* *When* abro la pantalla de resultados
* *Then* veo gráfico (barras/radar), descripción corta por área y botón “Explorar carreras”.

3. **Persistencia de resultados**

* *Given* finalicé el test
* *When* recargo o entro luego
* *Then* el intento aparece en el historial con fecha, puntajes y enlace al detalle.

4. **Errores de cálculo**

* *Given* se produce un error de servidor
* *When* finalizo el test
* *Then* veo mensaje de reintento y no se duplica el intento.

**Asignación:**

* **FE:** Vista de resultados + gráfico
* **BE:** `/assessments/{id}/submit` (POST), job de scoring, `/assessments/{id}/result` (GET)
* **Data/ML (si aplica):** regla de scoring y tabla de mapeo área→carreras
* **QA:** precisión del mapeo, idempotencia de submit

**Estado:** Por hacer

---

## Historia #9: Historial de intentos y descarga de informe

**Título:** *Listar intentos anteriores y descargar PDF resumido*
**COMO** usuario • **QUIERO** ver mis evaluaciones previas y bajar un informe • **PARA** comparar mi evolución y compartirla.

**Criterios (Given–When–Then):**

1. **Listado**

* *Given* tengo intentos
* *When* abro “Mis evaluaciones”
* *Then* veo tabla con fecha, estado (completo/en curso), top-áreas y acciones (ver/descargar).

2. **Detalle y comparación**

* *Given* dos o más intentos completos
* *When* elijo “Comparar”
* *Then* veo diferencias de puntajes por área (tabla o mini-gráfico).

3. **Descarga PDF**

* *Given* intento completo
* *When* hago clic “Descargar informe”
* *Then* recibo un PDF con: datos básicos del perfil, puntajes, top-carreras, y enlaces a materiales.

4. **Autorización**

* *Given* intento de otro usuario
* *When* pruebo abrir/descargar por URL directa
* *Then* obtengo 403 (acceso denegado).

**Asignación:**

* **FE:** vistas lista/detalle, botón comparar, trigger de descarga
* **BE:** `/assessments` (GET list), `/assessments/{id}` (GET), `/assessments/{id}/report.pdf`
* **QA:** verificación de ownership, formato PDF y contenidos mínimos
* **UX:** layout compacto del historial

**Estado:** Por hacer

---

## Historia #10: Eliminar evaluaciones

**Título:** *Borrar intentos anteriores no relevantes*
**COMO** usuario • **QUIERO** eliminar evaluaciones pasadas • **PARA** mantener mi historial ordenado.

**Criterios (Given–When–Then):**

1. **Confirmación**

* *Given* intento seleccionado
* *When* confirmo “Eliminar”
* *Then* el intento se elimina de forma permanente y desaparece del historial.

2. **Protección del último resultado** (opcional)

* *Given* solo tengo un intento completo
* *When* intento borrarlo
* *Then* se sugiere conservarlo (diálogo con advertencia).

3. **Auditoría mínima**

* *Given* borrado exitoso
* *When* consulto registros de sistema
* *Then* existe log interno con userId, assessmentId y timestamp (no visible al usuario).

4. **Errores y permisos**

* *Given* intento de otro usuario
* *When* envío DELETE
* *Then* obtengo 403.

**Asignación:**

* **FE:** diálogos de confirmación/toast
* **BE:** `DELETE /assessments/{id}` con checks de ownership y cascadas
* **QA:** intentos inexistentes/ajenos, refresco del listado
* **UX:** copy claro de advertencia

**Estado:** Por hacer

---

## Sugerencias rápidas de implementación

* **Modelo (mínimo):** `Assessment(id, userId, status{draft,completed}, answers[], scores{area:score}, createdAt, completedAt)`
* **Política:** 1 intento “en curso” por usuario; múltiples “completados”.
* **Idempotencia:** `submit` bloquea re-envíos duplicados (token o flag de estado).
* **Seguridad:** todos los endpoints bajo auth JWT; ownership check estricto.

## Comandos para crear issues (GitHub CLI)

```bash
gh issue create -t "M2-#6 Epic: Evaluación vocacional end-to-end" -b "Epic y alcance..." -l "epic,assessment"
gh issue create -t "M2-#7 Rendir test (UI + flujo)" -b "COMO usuario... criterios..." -l "frontend,backend,qa,ux,assessment"
gh issue create -t "M2-#8 Scoring y sugerencias" -b "COMO usuario... criterios..." -l "backend,data,frontend,qa,assessment"
gh issue create -t "M2-#9 Historial y PDF" -b "COMO usuario... criterios..." -l "frontend,backend,qa,assessment"
gh issue create -t "M2-#10 Eliminar evaluaciones" -b "COMO usuario... criterios..." -l "frontend,backend,qa,assessment"
```

¿Le damos al **Módulo 3 (#11–#14: Exploración de carreras)** con el mismo formato?






**Resumen:** Backlog del Módulo 3 (Exploración de carreras y profesiones): 1 epic (#11) y 4 historias (#12–#15) con COMO/QUIERO/PARA, criterios Given–When–Then, responsables y estado.

---

# Módulo 3 — Exploración de carreras y profesiones

**Issue #11 (Epic):** *Exploración de carreras académicas y profesionales*
**COMO** estudiante • **QUIERO** explorar fichas de carreras y marcar favoritas • **PARA** evaluar opciones académicas alineadas a mis intereses.
**Alcance:** listado, filtros, detalle de carrera y favoritos.
**Cierre del Epic:** todas las historias #12–#15 completadas con navegación integrada y consistencia con el perfil vocacional.
**Asignado a:** PM/Tech Lead • **Estado:** Por hacer

---

## Historia #12: Listado de fichas de carrera

**Título:** *Visualizar listado general de carreras con información básica*
**COMO** usuario • **QUIERO** ver fichas de carrera (nombre, duración, modalidad, descripción) • **PARA** conocer la oferta académica disponible.

**Criterios:**

1. **Listado inicial**

* *Given* usuario autenticado
* *When* abre “Explorar carreras”
* *Then* ve grid/lista con fichas (nombre, duración, modalidad, breve descripción).

2. **Carga de datos**

* *Given* dataset de carreras cargado en backend
* *When* se solicitan fichas
* *Then* se muestran ≤20 por página con paginación/infinite scroll.

3. **Errores**

* *Given* backend sin datos
* *When* abro listado
* *Then* veo mensaje “No hay carreras disponibles en este momento”.

**Asignación:**

* **FE:** UI lista/grid + paginación
* **BE:** `/careers` GET con paginación
* **QA:** validación de datos mostrados vs DB
* **UX:** diseño de card responsiva

**Estado:** Por hacer

---

## Historia #13: Filtrar carreras

**Título:** *Aplicar filtros por área, duración y tipo de formación*
**COMO** usuario • **QUIERO** filtrar carreras por criterios específicos • **PARA** reducir la lista a opciones que se ajusten a mis necesidades.

**Criterios:**

1. **Filtrado por área**

* *Given* select de “Área de interés”
* *When* selecciono un área
* *Then* se actualiza lista con carreras de esa área.

2. **Filtrado combinado**

* *Given* filtros de duración (años) y tipo (técnica/universitaria)
* *When* aplico ambos
* *Then* veo solo las que cumplen ambos criterios.

3. **Reset filtros**

* *Given* filtros activos
* *When* hago clic en “Limpiar filtros”
* *Then* lista vuelve al estado inicial.

**Asignación:**

* **FE:** UI de filtros dinámicos
* **BE:** query params en `/careers?area=...&duration=...&type=...`
* **QA:** combinación de filtros y reset
* **UX:** experiencia clara con chips/tags de filtros activos

**Estado:** Por hacer

---

## Historia #14: Ver detalle de carrera

**Título:** *Consultar ficha detallada de carrera seleccionada*
**COMO** usuario • **QUIERO** abrir una carrera y ver detalles completos • **PARA** comprender mejor su perfil y plan académico.

**Criterios:**

1. **Detalle completo**

* *Given* lista de carreras
* *When* hago clic en una
* *Then* veo ficha con nombre, duración, modalidad, plan de estudios básico, perfil de egreso, instituciones que la ofrecen.

2. **Navegación**

* *Given* ficha abierta
* *When* vuelvo atrás
* *Then* regreso al mismo punto en el listado con filtros aplicados.

3. **Errores**

* *Given* carrera inexistente (ID inválido)
* *When* intento abrirla
* *Then* recibo 404 y mensaje claro (“Carrera no encontrada”).

**Asignación:**

* **FE:** vista detalle + navegación
* **BE:** `/careers/{id}` GET con info ampliada
* **QA:** validación de campos obligatorios
* **UX:** layout legible con secciones

**Estado:** Por hacer

---

## Historia #15: Marcar carreras favoritas

**Título:** *Agregar o quitar carreras de la lista de favoritos*
**COMO** usuario • **QUIERO** marcar carreras como favoritas o quitarlas • **PARA** guardar opciones que me interesan para revisarlas después.

**Criterios:**

1. **Agregar favorito**

* *Given* listado/detalle de carrera
* *When* hago clic en “⭐ Favorito”
* *Then* se guarda en mi perfil y cambia el icono a marcado.

2. **Quitar favorito**

* *Given* carrera ya en favoritos
* *When* vuelvo a hacer clic
* *Then* se elimina de mi lista.

3. **Vista favoritos**

* *Given* tengo favoritos
* *When* abro “Mis favoritos”
* *Then* veo listado exclusivo de esas carreras.

4. **Persistencia**

* *Given* guardé favoritos
* *When* cierro sesión y vuelvo a entrar
* *Then* los favoritos siguen disponibles.

**Asignación:**

* **FE:** botón favoritos y lista “Mis favoritos”
* **BE:** `/users/me/favorites` (POST/DELETE/GET)
* **QA:** persistencia cross-session, consistencia lista
* **UX:** iconografía clara y feedback inmediato

**Estado:** Por hacer

---

## Sugerencias técnicas mínimas

* **Modelo:** `Career(id, name, area, duration, type, description, details)` y `UserFavorite(userId, careerId)`
* **Performance:** indexación en DB por `area` y `type` para filtros rápidos
* **Frontend:** hooks de sincronización favoritos + local storage fallback

---

¿Quieres que siga con el **Módulo 4 (Ruta de Aprendizaje Personalizada, #16–#19)** en el mismo formato?




**Resumen:** Backlog del Módulo 4 (Ruta de Aprendizaje Personalizada): 1 epic (#16) + 4 historias (#17–#20) con COMO/QUIERO/PARA, criterios Given–When–Then, responsables y estado.

---

# Módulo 4 — Ruta de Aprendizaje Personalizada

**Issue #16 (Epic):** *Ruta de Aprendizaje basada en perfil vocacional*
**COMO** estudiante • **QUIERO** acceder a materiales sugeridos alineados a mi test y favoritos • **PARA** explorar contenidos que me preparen mejor para una decisión académica.
**Alcance:** sugerencias de materiales, guardado, descargas, y gestión de recursos.
**Cierre del Epic:** #17–#20 completadas e integradas al perfil del usuario.
**Asignado a:** PM/Tech Lead • **Estado:** Por hacer

---

## Historia #17: Ver materiales sugeridos

**Título:** *Acceder a materiales recomendados por carrera o área*
**COMO** usuario autenticado • **QUIERO** ver materiales sugeridos (videos, lecturas, enlaces) • **PARA** profundizar en las áreas afines a mis intereses.

**Criterios:**

1. **Listado sugerido**

* *Given* resultados de test vocacional
* *When* entro a “Ruta de Aprendizaje”
* *Then* veo lista con recursos categorizados por carrera/área (ej. vídeos, artículos, PDF).

2. **Acceso a enlaces**

* *Given* recurso externo (ej. video de YouTube)
* *When* hago clic
* *Then* se abre en nueva pestaña o reproductor embebido.

3. **Relevancia**

* *Given* mis intereses en perfil
* *When* abro la sección
* *Then* los materiales sugeridos corresponden a mis áreas principales.

**Asignación:**

* **FE:** UI de lista de materiales, iconos por tipo
* **BE:** `/learning-path` GET con filtro por perfil
* **QA:** validación de categorías y consistencia con áreas del test
* **UX:** tags por tipo de recurso y botones claros

**Estado:** Por hacer

---

## Historia #18: Guardar materiales como favoritos

**Título:** *Guardar materiales en lista personal de favoritos*
**COMO** usuario • **QUIERO** marcar materiales como favoritos • **PARA** revisarlos después fácilmente.

**Criterios:**

1. **Marcar favorito**

* *Given* listado de materiales
* *When* hago clic en “⭐ Guardar”
* *Then* aparece en mi lista “Mis materiales favoritos”.

2. **Quitar favorito**

* *Given* material ya guardado
* *When* clic en “⭐ Quitar”
* *Then* desaparece de mi lista.

3. **Persistencia**

* *Given* sesión cerrada
* *When* vuelvo a entrar
* *Then* mis favoritos siguen guardados.

**Asignación:**

* **FE:** botón favoritos en cada recurso y vista de “Mis favoritos”
* **BE:** `/users/me/materials/favorites` (POST/DELETE/GET)
* **QA:** persistencia y consistencia de favoritos
* **UX:** feedback visual inmediato

**Estado:** Por hacer

---

## Historia #19: Descargar materiales en PDF o guardar enlaces

**Título:** *Descargar materiales en PDF y guardar links externos*
**COMO** usuario • **QUIERO** descargar ciertos materiales o guardar enlaces externos • **PARA** tener acceso offline o registrar recursos de interés.

**Criterios:**

1. **Descarga PDF**

* *Given* recurso en formato descargable
* *When* hago clic en “Descargar”
* *Then* se descarga un PDF con el material.

2. **Guardar enlace externo**

* *Given* tengo un link adicional
* *When* lo guardo en “Mis materiales”
* *Then* aparece en mi lista personal con título editable.

3. **Validaciones**

* *Given* URL inválida
* *When* intento guardarla
* *Then* aparece mensaje “Enlace no válido”.

**Asignación:**

* **FE:** botón de descarga y formulario de agregar link
* **BE:** `/materials/{id}/download` y `/users/me/materials/custom-links`
* **QA:** descarga funcional, validación de URL, consistencia
* **UX:** indicaciones claras de qué se descarga y qué se guarda como link

**Estado:** Por hacer

---

## Historia #20: Eliminar materiales guardados

**Título:** *Eliminar materiales que ya no son de interés*
**COMO** usuario • **QUIERO** borrar materiales guardados/favoritos • **PARA** mantener mi ruta organizada y relevante.

**Criterios:**

1. **Eliminar favorito**

* *Given* lista “Mis materiales”
* *When* clic en “Eliminar”
* *Then* el recurso desaparece de la lista.

2. **Confirmación**

* *Given* recurso importante (ej. PDF)
* *When* intento eliminarlo
* *Then* aparece confirmación “¿Seguro que deseas eliminarlo?”.

3. **Refresco automático**

* *Given* elimino un material
* *When* vuelve la vista
* *Then* lista se actualiza sin necesidad de recargar página.

**Asignación:**

* **FE:** botón eliminar y confirmación modal
* **BE:** `DELETE /users/me/materials/{id}`
* **QA:** consistencia tras borrar, control de ownership
* **UX:** mensaje de advertencia claro

**Estado:** Por hacer

---

## Notas técnicas mínimas

* **Modelo:** `LearningMaterial(id, type, title, url/filePath, careerId, areaId)` y `UserMaterial(userId, materialId, isFavorite, customLink)`
* **Seguridad:** validación de ownership en favoritos y links custom
* **Offline:** descargas gestionadas con headers correctos (Content-Type: application/pdf)

---

¿Quieres que siga con el **Módulo 5 (#21–#24: Reportes y recomendaciones)** con el mismo formato?



**Resumen:** Backlog del Módulo 5 (Reportes y recomendaciones): 1 epic (#21) + 4 historias (#22–#25) con COMO/QUIERO/PARA, criterios Given–When–Then, responsables y estado.

---

# Módulo 5 — Reportes y recomendaciones

**Issue #21 (Epic):** *Reportes globales y recomendaciones personalizadas*
**COMO** estudiante • **QUIERO** acceder a reportes visuales y sugerencias • **PARA** analizar mis resultados y planificar mis siguientes pasos.
**Alcance:** gráficos de intereses, lista de carreras recomendadas, informes en PDF y comparaciones de intentos.
**Cierre del Epic:** todas las historias #22–#25 completadas, integradas con módulos 2–4.
**Asignado a:** PM/Tech Lead • **Estado:** Por hacer

---

## Historia #22: Visualizar gráfico de intereses predominantes

**Título:** *Mostrar gráfico (barras o radar) con mis áreas de interés*
**COMO** usuario • **QUIERO** ver mis áreas de interés en un gráfico visual • **PARA** entender claramente mis fortalezas vocacionales.

**Criterios:**

1. **Gráfico inicial**

* *Given* resultados de test
* *When* accedo a “Reportes”
* *Then* aparece gráfico de barras o radar con puntajes por área.

2. **Interactividad básica**

* *Given* gráfico cargado
* *When* paso el mouse o pulso en un área
* *Then* veo valor numérico y descripción breve.

3. **Comparación visual (opcional)**

* *Given* dos intentos seleccionados
* *When* activo “Comparar en gráfico”
* *Then* se muestran ambos en el mismo gráfico (colores distintos).

**Asignación:**

* **FE:** integración con librería de gráficos (ej. Chart.js/Recharts)
* **BE:** endpoint `/reports/interests` con data estructurada
* **QA:** validación de cálculos y consistencia con resultados del test
* **UX:** colores accesibles y etiquetas claras

**Estado:** Por hacer

---

## Historia #23: Ver lista de carreras recomendadas

**Título:** *Recibir lista priorizada de carreras afines a mi test vocacional*
**COMO** usuario • **QUIERO** ver un listado de carreras recomendadas • **PARA** explorar opciones relacionadas con mis intereses principales.

**Criterios:**

1. **Listado ordenado**

* *Given* resultados del test
* *When* entro a “Recomendaciones”
* *Then* recibo ≥5 carreras ordenadas por relevancia (ej. score).

2. **Acceso rápido**

* *Given* listado
* *When* clic en una carrera
* *Then* me lleva a su ficha detallada (#14).

3. **Actualización dinámica**

* *Given* cambio intereses en el perfil
* *When* vuelvo a “Recomendaciones”
* *Then* la lista se actualiza automáticamente.

**Asignación:**

* **FE:** listado con enlaces a detalle
* **BE:** `/reports/recommendations` GET filtrado por intereses/resultados
* **QA:** verificación de ranking y consistencia
* **UX:** mostrar razones de recomendación (ej. “Relacionado a tu interés en X”).

**Estado:** Por hacer

---

## Historia #24: Descargar informe vocacional en PDF

**Título:** *Generar PDF con mis resultados, carreras favoritas y materiales revisados*
**COMO** usuario • **QUIERO** descargar un informe completo en PDF • **PARA** compartirlo con orientadores, familia o guardar para mí.

**Criterios:**

1. **Contenido mínimo**

* *Given* resultados de test + favoritos + materiales revisados
* *When* solicito descarga
* *Then* el PDF incluye: datos básicos, gráfico de intereses, top carreras, lista de favoritos, recursos más visitados.

2. **Formato estándar**

* *Given* PDF generado
* *When* lo abro en cualquier visor
* *Then* mantiene formato legible (logo, tablas, gráficos simples).

3. **Protección de datos**

* *Given* intento abrir informe de otro usuario
* *When* accedo vía URL directa
* *Then* obtengo 403 (acceso denegado).

**Asignación:**

* **FE:** botón de descarga en “Reportes”
* **BE:** `/reports/vocational.pdf` con generación dinámica (ej. ReportLab)
* **QA:** validación de contenido y formato
* **UX:** diseño de portada simple con branding de la app

**Estado:** Por hacer

---

## Historia #25: Comparar resultados entre intentos anteriores

**Título:** *Comparar resultados de evaluaciones anteriores*
**COMO** usuario • **QUIERO** comparar mis resultados de distintos intentos • **PARA** ver evolución o cambios en mis intereses.

**Criterios:**

1. **Selección de intentos**

* *Given* historial con ≥2 intentos completos
* *When* selecciono dos
* *Then* aparece tabla comparativa de puntajes y áreas.

2. **Vista gráfica**

* *Given* comparación activa
* *When* elijo “Ver en gráfico”
* *Then* se muestran en barras/radar superpuestas.

3. **Interpretación textual**

* *Given* comparación generada
* *When* abro detalle
* *Then* veo texto “Tus intereses en X aumentaron/disminuyeron respecto al intento anterior”.

**Asignación:**

* **FE:** tabla + toggle gráfico/texto
* **BE:** `/reports/comparison?attempt1=...&attempt2=...`
* **QA:** validación de cálculos y diferencias
* **UX:** explicaciones legibles, sin sobrecargar datos

**Estado:** Por hacer

---

## Notas técnicas mínimas

* **Modelo:** `Report(userId, attemptId, interests[], recommendedCareers[], favorites[], resources[])`
* **Infra:** generación de PDF bajo demanda (evitar almacenar todos los informes en disco).
* **Seguridad:** ownership check en endpoints de reportes.

---

👉 ¿Quieres que arme también un **resumen global de los 5 módulos** con dependencias entre ellos y sugerencia de orden de implementación para el tablero?
