package it.uniroma3.siw.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
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

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<ImageData> images;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private ImageData cover;

    @ManyToOne
    private Artist director;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Artist> actors;

    @OneToMany(cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id")
    private Set<Review> reviews;

    @Column(columnDefinition = "Decimal(1,1) default '0.0'")
    private Double averageVote;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Movie movie)) {
            return false;
        }
        return movie.getId()
                .equals(this.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode() + title.hashCode() + year.hashCode();
    }

}
