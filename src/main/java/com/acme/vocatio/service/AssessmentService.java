package com.acme.vocatio.service;

import com.acme.vocatio.dto.assessment.AssessmentRequest;
import com.acme.vocatio.dto.assessment.AssessmentResponse;
import com.acme.vocatio.dto.assessment.AssessmentResultResponse;
import java.util.List;

public interface AssessmentService {
    
    AssessmentResponse createAssessment(Long userId, AssessmentRequest request);
    
    AssessmentResponse getAssessment(String assessmentId, Long userId);
    
    AssessmentResponse answerQuestion(String assessmentId, Long userId, AssessmentRequest request);
    
    AssessmentResultResponse completeAssessment(String assessmentId, Long userId);
    
    AssessmentResultResponse getAssessmentResults(String assessmentId, Long userId);
    
    List<AssessmentResponse> getAssessmentHistory(Long userId);
}