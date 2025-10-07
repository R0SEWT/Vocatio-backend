package com.acme.vocatio.model;

/**
 * Enum que define los diferentes tipos de recursos de aprendizaje disponibles.
 * Los valores coinciden con el enum de la base de datos PostgreSQL.
 */
public enum TipoRecurso {
    
    /**
     * Archivo PDF descargable.
     */
    PDF("PDF", true, false),
    
    /**
     * Video en línea o descargable.
     */
    VIDEO("Video", false, true),
    
    /**
     * Enlace externo a sitio web.
     */
    ENLACE("Enlace Externo", false, true),
    
    /**
     * Testimonio de estudiante o profesional.
     */
    TESTIMONIO("Testimonio", false, true);
    
    private final String descripcion;
    private final boolean esDescargable;
    private final boolean esEnlaceExterno;
    
    TipoRecurso(String descripcion, boolean esDescargable, boolean esEnlaceExterno) {
        this.descripcion = descripcion;
        this.esDescargable = esDescargable;
        this.esEnlaceExterno = esEnlaceExterno;
    }
    
    /**
     * Obtiene la descripción amigable del tipo de recurso.
     */
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * Indica si el recurso es descargable.
     */
    public boolean isDescargable() {
        return esDescargable;
    }
    
    /**
     * Indica si el recurso es un enlace externo.
     */
    public boolean isEnlaceExterno() {
        return esEnlaceExterno;
    }
}