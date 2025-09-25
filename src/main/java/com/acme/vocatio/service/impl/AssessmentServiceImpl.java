package com.acme.vocatio.service.impl;

import com.acme.vocatio.dto.assessment.AssessmentRequest;
import com.acme.vocatio.dto.assessment.AssessmentResponse;
import com.acme.vocatio.dto.assessment.AssessmentResultResponse;
import com.acme.vocatio.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssessmentServiceImpl implements AssessmentService {

    @Override
    @Transactional
    public AssessmentResponse createAssessment(Long userId, AssessmentRequest request) {
        // Generar ID único para la evaluación
        String assessmentId = "asmt-a" + generateShortId();
        
        // Crear la primera pregunta
        AssessmentResponse.QuestionData questionData = new AssessmentResponse.QuestionData(
            1L,
            "¿Qué actividad te resulta más atractiva?",
            Arrays.asList(
                new AssessmentResponse.Option(1L, "Resolver ecuaciones matemáticas complejas", "Ciencias Exactas"),
                new AssessmentResponse.Option(2L, "Programar una aplicación móvil", "Tecnología"),
                new AssessmentResponse.Option(3L, "Ayudar a personas con problemas de salud", "Ciencias de la Salud"),
                new AssessmentResponse.Option(4L, "Crear una obra de arte o diseño", "Arte y Creatividad")
            )
        );
        
        AssessmentResponse.Questions questions = new AssessmentResponse.Questions(
            request.testType(),
            5, // Total de preguntas
            1, // Pregunta actual
            questionData
        );
        
        return new AssessmentResponse(
            assessmentId,
            "en_curso",
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(2), // Expira en 2 horas
            questions
        );
    }

    @Override
    public AssessmentResponse getAssessment(String assessmentId, Long userId) {
        // Simular obtención de evaluación existente
        AssessmentResponse.QuestionData questionData = new AssessmentResponse.QuestionData(
            2L,
            "¿En qué tipo de ambiente prefieres trabajar?",
            Arrays.asList(
                new AssessmentResponse.Option(5L, "En una oficina con tecnología de punta", "Tecnología"),
                new AssessmentResponse.Option(6L, "En un hospital o clínica", "Ciencias de la Salud"),
                new AssessmentResponse.Option(7L, "En una empresa o corporación", "Negocios"),
                new AssessmentResponse.Option(8L, "Al aire libre en contacto con la naturaleza", "Ciencias Ambientales")
            )
        );
        
        AssessmentResponse.Questions questions = new AssessmentResponse.Questions(
            "Test Vocacional Básico",
            5,
            2,
            questionData
        );
        
        return new AssessmentResponse(
            assessmentId,
            "en_curso",
            LocalDateTime.now().minusMinutes(15),
            LocalDateTime.now().plusHours(2),
            questions
        );
    }

    @Override
    @Transactional
    public AssessmentResponse answerQuestion(String assessmentId, Long userId, AssessmentRequest request) {
        // Simular respuesta y avance a la siguiente pregunta
        AssessmentResponse.QuestionData nextQuestion = new AssessmentResponse.QuestionData(
            request.questionId() + 1,
            "¿Qué tipo de problemas te gusta resolver?",
            Arrays.asList(
                new AssessmentResponse.Option(9L, "Problemas lógicos y matemáticos", "Ciencias Exactas"),
                new AssessmentResponse.Option(10L, "Problemas sociales y comunitarios", "Ciencias Sociales"),
                new AssessmentResponse.Option(11L, "Problemas técnicos y de ingeniería", "Ingeniería"),
                new AssessmentResponse.Option(12L, "Problemas de comunicación y medios", "Comunicación")
            )
        );
        
        AssessmentResponse.Questions questions = new AssessmentResponse.Questions(
            "Test Vocacional Básico",
            5,
            3, // Pregunta 3
            nextQuestion
        );
        
        return new AssessmentResponse(
            assessmentId,
            "en_curso",
            LocalDateTime.now().minusMinutes(20),
            LocalDateTime.now().plusHours(2),
            questions
        );
    }

    @Override
    @Transactional
    public AssessmentResultResponse completeAssessment(String assessmentId, Long userId) {
        // Simular finalización y cálculo de resultados
        Map<String, Double> areaScores = new HashMap<>();
        areaScores.put("Tecnología", 8.5);
        areaScores.put("Ciencias Exactas", 7.2);
        areaScores.put("Ingeniería", 6.8);
        areaScores.put("Ciencias de la Salud", 5.4);
        areaScores.put("Arte y Creatividad", 4.1);
        
        AssessmentResultResponse.Results results = new AssessmentResultResponse.Results(
            areaScores,
            "Tecnología",
            6.8,
            "Advanced"
        );
        
        List<AssessmentResultResponse.CareerRecommendation> careers = Arrays.asList(
            new AssessmentResultResponse.CareerRecommendation(
                1L,
                "Ingeniería de Sistemas",
                "Carrera enfocada en el diseño y desarrollo de sistemas de información",
                92.5,
                "Tu alta puntuación en Tecnología e Ingeniería indica una fuerte afinidad"
            ),
            new AssessmentResultResponse.CareerRecommendation(
                2L,
                "Ciencias de la Computación",
                "Estudio teórico y práctico de algoritmos y estructuras de datos",
                88.3,
                "Combina perfectamente tus intereses en Tecnología y Ciencias Exactas"
            )
        );
        
        List<AssessmentResultResponse.ResourceRecommendation> resources = Arrays.asList(
            new AssessmentResultResponse.ResourceRecommendation(
                1L,
                "Curso de Programación Básica",
                "video",
                "https://ejemplo.com/programacion-basica",
                "Introducción a los conceptos fundamentales de programación"
            ),
            new AssessmentResultResponse.ResourceRecommendation(
                2L,
                "Matemáticas para Ingeniería",
                "pdf",
                "https://ejemplo.com/matematicas.pdf",
                "Fundamentos matemáticos aplicados a la ingeniería"
            )
        );
        
        AssessmentResultResponse.Recommendations recommendations = new AssessmentResultResponse.Recommendations(
            careers,
            resources,
            "Te recomendamos explorar carreras en el área de Tecnología y considerar realizar cursos preparatorios en programación y matemáticas."
        );
        
        return new AssessmentResultResponse(
            assessmentId,
            "completado",
            LocalDateTime.now(),
            results,
            recommendations
        );
    }

    @Override
    public AssessmentResultResponse getAssessmentResults(String assessmentId, Long userId) {
        // Simular obtención de resultados existentes
        return completeAssessment(assessmentId, userId);
    }

    @Override
    public List<AssessmentResponse> getAssessmentHistory(Long userId) {
        // Simular historial de evaluaciones
        return Arrays.asList(
            new AssessmentResponse(
                "asmt-a1b2c3d4",
                "completado",
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now().minusDays(7).plusHours(2),
                null
            ),
            new AssessmentResponse(
                "asmt-e5f6g7h8",
                "en_curso",
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                null
            )
        );
    }
    
    private String generateShortId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}