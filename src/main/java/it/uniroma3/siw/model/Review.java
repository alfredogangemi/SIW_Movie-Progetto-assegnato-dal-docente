package it.uniroma3.siw.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer vote;

    @Column(nullable = false, columnDefinition = "TEXT")
    private Integer text;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    //TODO Utente


}
