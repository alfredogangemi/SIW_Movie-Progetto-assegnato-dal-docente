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

    private String title;

    private Integer vote;

    @Column(columnDefinition = "TEXT")
    private Integer text;

    private Integer likes;

    private LocalDateTime creationDate;
}
