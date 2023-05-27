package it.uniroma3.siw.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Artist artist)) {
            return false;
        }
        return artist.getId()
                .equals(this.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode() + name.hashCode() + surname.hashCode();
    }
}
