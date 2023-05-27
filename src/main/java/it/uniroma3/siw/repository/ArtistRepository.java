package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Artist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;


public interface ArtistRepository extends CrudRepository<Artist, Long> {

    boolean existsByNameAndSurnameAndDateOfBirth(String name, String surname, LocalDate dateOfBirth);

    @Query(value = "select * from artist a where a.id not in (select actors_id from movie_actors "
            + "where movie_actors.movie_id = :movieId)", nativeQuery = true)
    Iterable<Artist> findActorsNotInMovie(@Param("movieId") Long id);


}

