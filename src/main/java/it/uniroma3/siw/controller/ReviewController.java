package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@Slf4j
public class ReviewController {

    protected final MovieService movieService;
    protected final ReviewValidator reviewValidator;
    protected final ReviewService reviewService;
    protected final CredentialsService credentialsService;
    protected final AuthenticationController authenticationController;

    @Autowired
    public ReviewController(MovieService movieService, ReviewValidator reviewValidator, ReviewService reviewService,
            CredentialsService credentialsService, AuthenticationController authenticationController) {
        this.movieService = movieService;
        this.reviewValidator = reviewValidator;
        this.reviewService = reviewService;
        this.credentialsService = credentialsService;
        this.authenticationController = authenticationController;
    }

    @GetMapping("/reviewMovie/{movieId}")
    public String formNewReview(Model model, @PathVariable("movieId") Long movieId, RedirectAttributes redirectAttributes) {
        User user = authenticationController.getCurrentUser();
        if (user != null) {
            if (reviewService.existsByUserAndMovie(user.getId(), movieId)) {
                log.warn("User has already reviewed the movie with ID {}", movieId);
                redirectAttributes.addFlashAttribute("errorMessage", "Hai già recensito questo film.");
                return "redirect:/movie/" + movieId;
            }
        }
        log.debug("Rendering form for new review for movie with ID {}", movieId);
        model.addAttribute("review", new Review());
        model.addAttribute("movieId", movieId);
        return "formNewReview";
    }



    @PostMapping("/addReviewToMovie/{movieId}")
    public String addReviewToMovie(Model model, @ModelAttribute("review") Review review,
            @PathVariable("movieId") Long movieId, BindingResult bindingResult) {
        reviewValidator.validate(review, bindingResult);
        if (bindingResult.hasErrors()) {
            return "formNewReview";
        }
        log.info("Persisting new review for movie {}", movieId);
        Movie movie = movieService.findMovieById(movieId);
        if (movie != null) {
            review.setCreationDate(LocalDateTime.now());

            User user = authenticationController.getCurrentUser();
            review.setUser(user);
            reviewService.save(review);
            movie.getReviews()
                    .add(review);
            movieService.save(movie);
            Double averageVote = reviewService.calculateAverageVote(movie);
            movie.setAverageVote(averageVote);
            movieService.save(movie);
        }
        return "redirect:/movie/" + movieId;
    }


    @PostMapping("/admin/deleteReview/{reviewId}/{movieId}")
    public String delete(@PathVariable("reviewId") Long reviewId, @PathVariable("movieId") Long movieId) {
        log.debug("Deleting review with ID {} for movie with ID {}", reviewId, movieId);
        if (reviewId != null && reviewService.existsById(reviewId)) {
            reviewService.deleteById(reviewId);

            Movie movie = movieService.findMovieById(movieId);
            if (movie != null) {
                Double averageVote = reviewService.calculateAverageVote(movie);
                movie.setAverageVote(averageVote);
                movieService.save(movie);
            }
        } else {
            log.warn("Error deleting review with ID {}", reviewId);
        }
        log.debug("Review with ID {} deleted for movie with ID {}", reviewId, movieId);
        return "redirect:/movie/" + movieId;
    }

}
