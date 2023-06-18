package it.uniroma3.siw.controller.validator;

import io.micrometer.common.util.StringUtils;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.service.MovieService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@Slf4j
public class MovieValidator implements Validator {
    private final MovieService movieService;

    @Autowired
    public MovieValidator(MovieService movieService) {
        this.movieService = movieService;
    }

    public void validate(@NonNull Object o, @NonNull Errors errors, boolean isNew) {
        log.info("Starting validating movie. is new -> {}", isNew);
        validate(o, errors);
        if (isNew) {
            exist((Movie) o, errors);
        }
    }


    @Override
    public void validate(@NonNull Object o, @NonNull Errors errors) {
        Movie movie = (Movie) o;
        if (StringUtils.isBlank(movie.getTitle())) {
            errors.reject("movie.not.valid.title");
            log.debug("Invalid movie title: {}", movie.getTitle());
        }
        Integer year = movie.getYear();
        if (year == null) {
            errors.reject("movie.not.valid.year");
            log.debug("Invalid movie year: null");
        }
    }

    @Override
    public boolean supports(@NonNull Class<?> aClass) {
        return Movie.class.equals(aClass);
    }

    public void exist(Movie movie, Errors errors) {
        if (movieService.existsByTitleAndYear(movie)) {
            errors.reject("movie.already.exist");
        }
    }


}
