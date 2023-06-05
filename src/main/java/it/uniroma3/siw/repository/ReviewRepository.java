package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface ReviewRepository extends CrudRepository<Review, Long> {


    @Query(value = "SELECT AVG(vote) FROM review WHERE movie_id = :movieId", nativeQuery = true)
    Double calculateAverageRating(@Param("movieId") Long movieId);


}

