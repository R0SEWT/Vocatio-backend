package com.acme.vocatio.service;

import com.acme.vocatio.dto.career.CareerListDto;
import com.acme.vocatio.model.Career;
import com.acme.vocatio.repository.CareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Servicio para operaciones de carreras. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareerService {

    private final CareerRepository careerRepository;

    /** Devuelve una página de carreras mapeadas a CareerListDto (M3-01). */
    public Page<CareerListDto> listCareers(Pageable pageable) {
        return careerRepository.findAll(pageable)
                .map(this::toDto);
    }

    /** Filtra carreras por área, duración y tipo de formación (M3-02). */
    public List<CareerListDto> filterCareers(String area, String duracion, String tipoFormacion) {
        List<Career> careers;

        if (area != null && duracion != null && tipoFormacion != null) {
            careers = careerRepository.findByAreaInteresAndDuracionAndTipoFormacion(area, duracion, tipoFormacion);
        } else if (duracion != null && tipoFormacion != null) {
            careers = careerRepository.findByDuracionAndTipoFormacion(duracion, tipoFormacion);
        } else if (area != null) {
            careers = careerRepository.findByAreaInteres(area);
        } else {
            careers = careerRepository.findAll();
        }

        return careers.stream().map(this::toDto).toList();
    }

    /** Mapea entidad Career a DTO CareerListDto. */
    private CareerListDto toDto(Career career) {
        return new CareerListDto(
                career.getId(),
                career.getNombre(),
                career.getDescripcion(),
                career.getPerfilRequerido(),
                career.getAreaInteres()
        );
    }
}
