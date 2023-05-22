package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Artist;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface ArtistRepository extends CrudRepository<Artist, Long> {

    boolean existsByNameAndSurnameAndDateOfBirth(String name, String surname, LocalDate dateOfBirth);

}


