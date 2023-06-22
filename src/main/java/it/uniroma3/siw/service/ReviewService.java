package it.uniroma3.siw.service;


import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ReviewService {


    protected final ReviewRepository reviewRepository;


    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public void save(Review review) {
        reviewRepository.save(review);
    }

    public Double calculateAverageVote(Movie movie) {
        Double result = reviewRepository.calculateAverageRating(movie.getId());
        return result != null ? result : 0D;
    }

    public boolean existsById(Long id) {
        return reviewRepository.existsById(id);
    }

    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    public boolean existsByUserAndMovie(Long userId, Long movieId) {
        return reviewRepository.countByUserIdAndMovieId(userId, movieId) > 0;
    }
}
