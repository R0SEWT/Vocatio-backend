package com.acme.vocatio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "profiles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @Id
    @Column(name = "id_usuario")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private User user;

    private String name;

    private Short age;

    private String grade;

    @Column(columnDefinition = "jsonb")
    private String personal_interests; // O un tipo más elaborado con @Convert
}