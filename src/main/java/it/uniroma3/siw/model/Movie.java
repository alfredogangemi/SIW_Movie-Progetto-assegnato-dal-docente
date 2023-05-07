package it.uniroma3.siw.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)

    private String title;
    @Column(nullable = false)

    private Integer year;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @OneToMany
    private Set<ImageData> images;

    @ManyToOne
    private Person director;

    @ManyToMany(mappedBy = "filmography")
    private Set<Person> actors;

    @OneToMany
    @JoinColumn(name = "movie_id")
    private List<Review> reviews;

}
