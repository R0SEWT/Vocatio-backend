package com.acme.vocatio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Entidad que representa un recurso guardado como favorito por un usuario.
 */
@Entity
@Table(name = "recursosguardados")
@Getter
@Setter
@NoArgsConstructor
public class SavedResource {

    @EmbeddedId
    private SavedResourceId id;

    @Column(name = "fecha_guardado", nullable = false)
    private Instant fechaGuardado;

    @PrePersist
    void onCreate() {
        this.fechaGuardado = Instant.now();
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class SavedResourceId implements java.io.Serializable {
        @Column(name = "id_usuario")
        private Long userId;

        @Column(name = "id_recurso")
        private Long resourceId;

        public SavedResourceId(Long userId, Long resourceId) {
            this.userId = userId;
            this.resourceId = resourceId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SavedResourceId)) return false;
            SavedResourceId that = (SavedResourceId) o;
            return userId.equals(that.userId) && resourceId.equals(that.resourceId);
        }

        @Override
        public int hashCode() {
            return userId.hashCode() + resourceId.hashCode();
        }
    }
}

