package it.uniroma3.siw.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @DateTimeFormat(pattern = "yyyy-MM-dd") //formato utile per thymeleaf
    private LocalDate dateOfBirth;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfDeath;

    @OneToOne(cascade = {CascadeType.ALL})
    private ImageData image;

    @OneToMany
    private Set<Movie> directedMovies;

    @ManyToMany
    private Set<Movie> starredMovies;

}
