package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
@Slf4j
public class ReviewController {

    private final MovieService movieService;
    private final ReviewValidator reviewValidator;
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(MovieService movieService, ReviewValidator reviewValidator, ReviewService reviewService) {
        this.movieService = movieService;
        this.reviewValidator = reviewValidator;
        this.reviewService = reviewService;
    }

    @GetMapping("/reviewMovie/{movieId}")
    public String formNewReview(Model model, @PathVariable("movieId") Long movieId) {
        model.addAttribute("review", new Review());
        model.addAttribute("movieId", movieId);
        return "formNewReview";
    }


    @PostMapping("/addReviewToMovie/{movieId}")
    public String addReviewToMovie(Model model, @ModelAttribute("review") Review review,
            @PathVariable("movieId") Long movieId, BindingResult bindingResult) {
        reviewValidator.validate(review, bindingResult);
        if (bindingResult.hasErrors()) {
            return formNewReview(model, movieId);
        }
        log.info("Persisting new review for movie {}", movieId);
        Movie movie = movieService.findMovieById(movieId);
        if (movie != null) {
            review.setCreationDate(LocalDateTime.now());
            reviewService.save(review);
            movie.getReviews()
                    .add(review);
            movieService.save(movie);
        }
        return "redirect:/movie/" + movieId;
    }

    @PostMapping("/deleteReview")
    public String deleteReview() {
        return "index";
    }



}
