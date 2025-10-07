package com.acme.vocatio.controller;

import com.acme.vocatio.dto.career.CareerListDto;
import com.acme.vocatio.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para explorar las carreras disponibles.
 * Casos de uso: M3-01 (listar) y M3-02 (filtrar).
 */
@RestController
@RequestMapping("/api/careers")
@RequiredArgsConstructor
public class CareerController {

    private final CareerService careerService;

    /** Endpoint: GET /api/careers - Lista paginada (M3-01). */
    @GetMapping
    public ResponseEntity<Page<CareerListDto>> listCareers(Pageable pageable) {
        Page<CareerListDto> careers = careerService.listCareers(pageable);

        if (careers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(careers);
    }

    /** Endpoint: GET /api/careers/filter - Filtrado por área, duración y tipo (M3-02). */
    @GetMapping("/filter")
    public ResponseEntity<List<CareerListDto>> filterCareers(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String duracion,
            @RequestParam(required = false) String tipoFormacion) {

        List<CareerListDto> careers = careerService.filterCareers(area, duracion, tipoFormacion);

        if (careers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(careers);
    }
}
