package it.uniroma3.siw.model;

import jakarta.persistence.*;
import lombok.Data;

import java.awt.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    @OneToMany
    private Set<ImageData> images;
    @OneToMany
    private Set<Movie> direction;
    @ManyToMany
    private Set<Movie> filmography;

}
