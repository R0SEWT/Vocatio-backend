-- Datos iniciales para la Plataforma de Orientación Vocacional
-- Este archivo contiene datos de ejemplo y configuración inicial

-- -----------------------------------------------------
-- Datos iniciales para Áreas de Interés
-- -----------------------------------------------------
INSERT INTO AreaInteres (nombre_area, descripcion) VALUES
('Ciencias Exactas', 'Matemáticas, Física, Química y disciplinas relacionadas'),
('Tecnología e Informática', 'Programación, Sistemas, Inteligencia Artificial, Ciberseguridad'),
('Ciencias de la Salud', 'Medicina, Enfermería, Psicología, Terapias'),
('Ciencias Sociales', 'Sociología, Trabajo Social, Ciencias Políticas, Relaciones Internacionales'),
('Humanidades', 'Literatura, Historia, Filosofía, Idiomas'),
('Arte y Creatividad', 'Diseño, Música, Teatro, Artes Visuales'),
('Negocios y Administración', 'Administración, Marketing, Finanzas, Recursos Humanos'),
('Ingeniería', 'Ingeniería Civil, Mecánica, Eléctrica, Industrial'),
('Comunicación', 'Periodismo, Publicidad, Comunicación Social, Medios Digitales'),
('Educación', 'Pedagogía, Docencia, Educación Infantil, Educación Especial'),
('Deportes y Recreación', 'Educación Física, Entrenamiento Deportivo, Recreación'),
('Ciencias Ambientales', 'Ecología, Conservación, Gestión Ambiental, Sostenibilidad');

-- -----------------------------------------------------
-- Test Vocacional Básico
-- -----------------------------------------------------
INSERT INTO TestVocacional (nombre, descripcion) VALUES
('Test de Orientación Vocacional Básico', 'Evaluación inicial para identificar áreas de interés principales del estudiante');

-- -----------------------------------------------------
-- Preguntas del Test (ejemplo de algunas preguntas)
-- -----------------------------------------------------
INSERT INTO Pregunta (id_test, texto_pregunta) VALUES
(1, '¿Qué actividad te resulta más atractiva?'),
(1, '¿En qué tipo de ambiente prefieres trabajar?'),
(1, '¿Qué tipo de problemas te gusta resolver?'),
(1, '¿Cuál de estas habilidades consideras tu fortaleza?'),
(1, '¿Qué tipo de impacto te gustaría tener en la sociedad?');

-- -----------------------------------------------------
-- Opciones para las preguntas (ejemplos)
-- -----------------------------------------------------
-- Pregunta 1: ¿Qué actividad te resulta más atractiva?
INSERT INTO Opcion (id_pregunta, id_area_interes, texto_opcion) VALUES
(1, 1, 'Resolver ecuaciones matemáticas complejas'),
(1, 2, 'Programar una aplicación móvil'),
(1, 3, 'Ayudar a personas con problemas de salud'),
(1, 6, 'Crear una obra de arte o diseño');

-- Pregunta 2: ¿En qué tipo de ambiente prefieres trabajar?
INSERT INTO Opcion (id_pregunta, id_area_interes, texto_opcion) VALUES
(2, 2, 'En una oficina con tecnología de punta'),
(2, 3, 'En un hospital o clínica'),
(2, 7, 'En una empresa o corporación'),
(2, 12, 'Al aire libre en contacto con la naturaleza');

-- Pregunta 3: ¿Qué tipo de problemas te gusta resolver?
INSERT INTO Opcion (id_pregunta, id_area_interes, texto_opcion) VALUES
(3, 1, 'Problemas lógicos y matemáticos'),
(3, 4, 'Problemas sociales y comunitarios'),
(3, 8, 'Problemas técnicos y de ingeniería'),
(3, 9, 'Problemas de comunicación y medios');

-- Pregunta 4: ¿Cuál de estas habilidades consideras tu fortaleza?
INSERT INTO Opcion (id_pregunta, id_area_interes, texto_opcion) VALUES
(4, 5, 'Análisis de textos y escritura'),
(4, 6, 'Creatividad y expresión artística'),
(4, 10, 'Enseñanza y explicación de conceptos'),
(4, 11, 'Liderazgo y trabajo en equipo');

-- Pregunta 5: ¿Qué tipo de impacto te gustaría tener en la sociedad?
INSERT INTO Opcion (id_pregunta, id_area_interes, texto_opcion) VALUES
(5, 3, 'Mejorar la salud y bienestar de las personas'),
(5, 10, 'Educar y formar a las futuras generaciones'),
(5, 12, 'Proteger el medio ambiente'),
(5, 4, 'Promover la justicia social y los derechos humanos');

-- -----------------------------------------------------
-- Carreras de ejemplo
-- -----------------------------------------------------
INSERT INTO Carrera (nombre_carrera, descripcion_detallada, perfil_requerido) VALUES
('Ingeniería de Sistemas', 
 'Carrera enfocada en el diseño, desarrollo y mantenimiento de sistemas de información y software.',
 'Habilidades analíticas, pensamiento lógico, interés por la tecnología, capacidad de resolución de problemas.'),

('Medicina', 
 'Carrera dedicada al diagnóstico, tratamiento y prevención de enfermedades.',
 'Vocación de servicio, resistencia física y emocional, habilidades de comunicación, dedicación al estudio.'),

('Psicología', 
 'Estudio científico del comportamiento humano y los procesos mentales.',
 'Empatía, habilidades de escucha, interés por el comportamiento humano, paciencia.'),

('Administración de Empresas', 
 'Formación en gestión, liderazgo y dirección de organizaciones.',
 'Liderazgo, habilidades de comunicación, pensamiento estratégico, orientación a resultados.'),

('Diseño Gráfico', 
 'Carrera enfocada en la comunicación visual y creación de contenido gráfico.',
 'Creatividad, sensibilidad estética, manejo de herramientas digitales, comunicación visual.');

-- -----------------------------------------------------
-- Recursos de aprendizaje de ejemplo
-- -----------------------------------------------------
INSERT INTO RecursoAprendizaje (id_carrera, titulo, tipo_recurso, url_recurso) VALUES
(1, 'Introducción a la Programación', 'video', 'https://ejemplo.com/video-programacion'),
(1, 'Fundamentos de Algoritmos', 'pdf', 'https://ejemplo.com/algoritmos.pdf'),
(2, 'Anatomía Humana Básica', 'video', 'https://ejemplo.com/anatomia'),
(2, 'Testimonio: Un día en la vida de un médico', 'testimonio', 'https://ejemplo.com/testimonio-medico'),
(3, 'Psicología del Desarrollo', 'enlace', 'https://ejemplo.com/psicologia-desarrollo'),
(4, 'Principios de Marketing', 'pdf', 'https://ejemplo.com/marketing.pdf'),
(5, 'Diseño con Adobe Illustrator', 'video', 'https://ejemplo.com/illustrator-tutorial');