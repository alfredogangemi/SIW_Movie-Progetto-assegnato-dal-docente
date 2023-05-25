package it.uniroma3.siw.repository;


import it.uniroma3.siw.model.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MovieRepository extends CrudRepository<Movie, Long> {
    boolean existsByTitleAndYear(String title, Integer year);

    @Query("SELECT m FROM Movie m ORDER BY m.creationDate DESC LIMIT 5")
    List<Movie> findLatestMovies();
}
