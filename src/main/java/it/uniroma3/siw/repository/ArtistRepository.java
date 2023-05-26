package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Artist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface ArtistRepository extends CrudRepository<Artist, Long> {

    boolean existsByNameAndSurnameAndDateOfBirth(String name, String surname, LocalDate dateOfBirth);

    @Query("SELECT a FROM Artist a WHERE lower(a.name) like %:searchString% OR lower(a.surname) like %:searchString%")
    List<Artist> searchArtistsByNameOrSurname(String searchString);


}

