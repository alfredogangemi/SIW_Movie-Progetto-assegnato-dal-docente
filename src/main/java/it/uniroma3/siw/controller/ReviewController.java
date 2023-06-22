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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
            if (reviewService.existsByUser(user)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Hai già recensito questo film.");
                return "redirect:/movie/" + movieId;
            }
        }
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
            String username = ((UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal()).getUsername();
            User user = credentialsService.getCredentials(username)
                    .getUser();
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

    @GetMapping("/deleteReview/{reviewId}/{movieId}")
    public String delete(@PathVariable("reviewId") Long reviewId, @PathVariable("movieId") Long movieId) {
        if (reviewId != null && reviewService.existsById(reviewId)) {
            reviewService.deleteById(reviewId);
            Movie movie = movieService.findMovieById(movieId);
            if (movie != null) {
                Double averageVote = reviewService.calculateAverageVote(movie);
                movie.setAverageVote(averageVote);
                movieService.save(movie);
            }
        } else {
            log.warn("Errore durante l'emininazione della recensione con id {}", reviewId);
        }
        return "redirect:/movie/" + movieId;
    }


}
