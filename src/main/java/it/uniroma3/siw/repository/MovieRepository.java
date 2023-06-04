package it.uniroma3.siw.repository;


import it.uniroma3.siw.model.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MovieRepository extends CrudRepository<Movie, Long> {
    boolean existsByTitleAndYear(String title, Integer year);

    @Query("SELECT m FROM Movie m ORDER BY m.creationDate DESC LIMIT 5")
    List<Movie> findLatestMovies();

    @Query(value = "SELECT * FROM movie m JOIN (SELECT movie_id, COUNT(id) as review_count FROM review GROUP BY movie_id ORDER BY review_count DESC LIMIT 5) AS top_movies ON m.id = top_movies.movie_id",
            nativeQuery = true)
    List<Movie> findTopRatedMovies();

    @Query(value = "SELECT AVG(r.vote) FROM review r WHERE r.movie_id = :movieId", nativeQuery = true)
    Double getAverageRatingByMovieId(Long movieId);


}
