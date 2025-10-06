package com.acme.vocatio.model;

/**
 * Enum que define los diferentes tipos de recursos de aprendizaje disponibles.
 */
public enum TipoRecurso {
    
    /**
     * Archivo PDF descargable.
     */
    PDF("PDF", true, false),
    
    /**
     * Enlace externo a sitio web.
     */
    EXTERNAL_LINK("Enlace Externo", false, true),
    
    /**
     * Video en línea o descargable.
     */
    VIDEO("Video", false, true),
    
    /**
     * Artículo o blog post.
     */
    ARTICLE("Artículo", false, true),
    
    /**
     * Curso en línea.
     */
    COURSE("Curso", false, true);
    
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