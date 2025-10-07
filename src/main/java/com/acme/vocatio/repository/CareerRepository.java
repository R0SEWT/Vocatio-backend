package com.acme.vocatio.repository;

import com.acme.vocatio.model.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Career.
 * Incluye métodos para filtros dinámicos según diferentes combinaciones de campos.
 */
@Repository
public interface CareerRepository extends JpaRepository<Career, Long> {

    // ----------------------------
    // Filtros por un solo campo
    // ----------------------------
    List<Career> findByAreaInteres(String areaInteres);

    // ----------------------------
    // Filtros por combinaciones de campos
    // ----------------------------
    List<Career> findByDuracionAndTipoFormacion(String duracion, String tipoFormacion);

    List<Career> findByAreaInteresAndDuracionAndTipoFormacion(String areaInteres, String duracion, String tipoFormacion);

    // ----------------------------
    // Filtros (4 campos)
    // ----------------------------
    List<Career> findByAreaInteresAndDuracionAndModalidadAndTipoFormacion(
            String areaInteres, String duracion, String modalidad, String tipoFormacion);

    List<Career> findByDuracionAndModalidadAndTipoFormacion(
            String duracion, String modalidad, String tipoFormacion);
}
