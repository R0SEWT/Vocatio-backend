package com.acme.vocatio.repository;

import com.acme.vocatio.model.LearningResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {

    /**
     * Encuentra recursos por carrera específica.
     */
    Page<LearningResource> findByCareerId(Long careerId, Pageable pageable);

    /**
     * Encuentra recursos por área de interés.
     */
    Page<LearningResource> findByAreaInteresId(Long areaInteresId, Pageable pageable);

    /**
     * Búsqueda personalizada por múltiples carreras (recomendaciones).
     */
    @Query("SELECT lr FROM LearningResource lr WHERE lr.careerId IN :careerIds ORDER BY lr.id DESC")
    Page<LearningResource> findByCareerIdIn(@Param("careerIds") List<Long> careerIds, Pageable pageable);

    /**
     * Búsqueda por múltiples áreas de interés.
     */
    @Query("SELECT lr FROM LearningResource lr WHERE lr.areaInteresId IN :areaIds ORDER BY lr.id DESC")
    Page<LearningResource> findByAreaInteresIdIn(@Param("areaIds") List<Long> areaIds, Pageable pageable);
}

