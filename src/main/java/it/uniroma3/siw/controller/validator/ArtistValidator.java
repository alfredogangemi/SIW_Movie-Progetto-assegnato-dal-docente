package it.uniroma3.siw.controller.validator;

import io.micrometer.common.util.StringUtils;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.service.ArtistService;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
public class ArtistValidator implements Validator {
    private final ArtistService artistService;
    private final Logger logger = LoggerFactory.getLogger(ArtistValidator.class);

    @Autowired
    public ArtistValidator(ArtistService artistService) {
        this.artistService = artistService;
    }

    public void validate(@NonNull Object o, @NonNull Errors errors, boolean isNew) {
        validate(o, errors);
        if (isNew) {
            exist((Artist) o, errors);
        }
    }


    @Override
    public void validate(@NonNull Object o, @NonNull Errors errors) {
        Artist artist = (Artist) o;
        logger.debug("Starting validate new artist...");
        //1. Checking name ad surname
        if (StringUtils.isBlank(artist.getName())) {
            errors.reject("artist.not.valid.name");
        }
        if (StringUtils.isBlank(artist.getSurname())) {
            errors.reject("artist.not.valid.surname");
        }
        //2.Checking data
        if (artist.getDateOfBirth() == null) {
            errors.reject("artist.not.valid.date.of.birth");
        } else {
            if (artist.getDateOfBirth()
                    .isAfter(LocalDate.now())) {
                errors.reject("artist.not.valid.date.of.birth");
            }
        }
        if (artist.getDateOfDeath() != null) {
            LocalDate deathDate = artist.getDateOfDeath();
            if (deathDate.isAfter(LocalDate.now()) || deathDate.isBefore(artist.getDateOfBirth())) {
                errors.reject("artist.not.valid.date.of.death");
            }
        }
    }

    @Override
    public boolean supports(@NonNull Class<?> aClass) {
        return Artist.class.equals(aClass);
    }

    public void exist(Artist artist, Errors errors) {
        if (artistService.existsByNameAndSurnameAndDateOfBirth(artist)) {
            errors.reject("artist.already.exist");
        }
    }


}
