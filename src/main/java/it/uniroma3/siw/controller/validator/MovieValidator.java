package it.uniroma3.siw.controller.validator;

import io.micrometer.common.util.StringUtils;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.service.MovieService;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class MovieValidator implements Validator {
    private final Logger logger = LoggerFactory.getLogger(ArtistValidator.class);
    private final MovieService movieService;

    @Autowired
    public MovieValidator(MovieService movieService) {
        this.movieService = movieService;
    }

    public void validate(@NonNull Object o, @NonNull Errors errors, boolean isNew) {
        validate(o, errors);
        if (isNew) {
            exist((Movie) o, errors);
        }
    }


    @Override
    public void validate(@NonNull Object o, @NonNull Errors errors) {
        Movie movie = (Movie) o;
        logger.debug("Starting validate new movie...");
        if (StringUtils.isBlank(movie.getTitle())) {
            logger.debug("Not valid title..");
            errors.reject("movie.not.valid.title");
        }
        Integer year = movie.getYear();
        if (year == null) {
            errors.reject("movie.not.valid.year");
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
