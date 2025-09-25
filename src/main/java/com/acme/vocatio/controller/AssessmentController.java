package com.acme.vocatio.controller;

import com.acme.vocatio.dto.assessment.AssessmentRequest;
import com.acme.vocatio.dto.assessment.AssessmentResponse;
import com.acme.vocatio.dto.assessment.AssessmentResultResponse;
import com.acme.vocatio.security.UserPrincipal;
import com.acme.vocatio.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    /**
     * Endpoint: POST /api/v1/assessments
     * Descripción: Crea un nuevo intento de evaluación en estado "en_curso" para el usuario autenticado
     * Códigos de respuesta: 201, 401, 409
     * Reglas de negocio: RN-08, RN-09
     */
    @PostMapping
    public ResponseEntity<AssessmentResponse> createAssessment(
            @Valid @RequestBody AssessmentRequest request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUser().getId();
        
        AssessmentResponse response = assessmentService.createAssessment(userId, request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint: GET /api/v1/assessments/{id_test}
     * Descripción: Obtiene un intento de evaluación específico
     */
    @GetMapping("/{assessmentId}")
    public ResponseEntity<AssessmentResponse> getAssessment(
            @PathVariable String assessmentId,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUser().getId();
        
        AssessmentResponse response = assessmentService.getAssessment(assessmentId, userId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint: PUT /api/v1/assessments/{id_test}/answer
     * Descripción: Responde una pregunta específica del test
     */
    @PutMapping("/{assessmentId}/answer")
    public ResponseEntity<AssessmentResponse> answerQuestion(
            @PathVariable String assessmentId,
            @Valid @RequestBody AssessmentRequest request,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUser().getId();
        
        AssessmentResponse response = assessmentService.answerQuestion(assessmentId, userId, request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint: POST /api/v1/assessments/{id_test}/complete
     * Descripción: Finaliza un test y genera los resultados
     */
    @PostMapping("/{assessmentId}/complete")
    public ResponseEntity<AssessmentResultResponse> completeAssessment(
            @PathVariable String assessmentId,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUser().getId();
        
        AssessmentResultResponse response = assessmentService.completeAssessment(assessmentId, userId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint: GET /api/v1/assessments/{id_test}/results
     * Descripción: Obtiene los resultados de un test completado
     */
    @GetMapping("/{assessmentId}/results")
    public ResponseEntity<AssessmentResultResponse> getAssessmentResults(
            @PathVariable String assessmentId,
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUser().getId();
        
        AssessmentResultResponse response = assessmentService.getAssessmentResults(assessmentId, userId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint: GET /api/v1/assessments/history
     * Descripción: Obtiene el historial de evaluaciones del usuario
     */
    @GetMapping("/history")
    public ResponseEntity<java.util.List<AssessmentResponse>> getAssessmentHistory(
            Authentication authentication) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUser().getId();
        
        java.util.List<AssessmentResponse> history = assessmentService.getAssessmentHistory(userId);
        
        return ResponseEntity.ok(history);
    }
}