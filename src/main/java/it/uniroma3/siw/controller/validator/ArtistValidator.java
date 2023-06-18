package it.uniroma3.siw.controller.validator;

import io.micrometer.common.util.StringUtils;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.service.ArtistService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
@Slf4j
public class ArtistValidator implements Validator {
    private final ArtistService artistService;
    private final Logger logger = LoggerFactory.getLogger(ArtistValidator.class);

    @Autowired
    public ArtistValidator(ArtistService artistService) {
        this.artistService = artistService;
    }

    public void validate(@NonNull Object o, @NonNull Errors errors, boolean isNew) {
        log.info("Starting validating artist. Is new artist -> {}", isNew);
        validate(o, errors);
        if (isNew) {
            exist((Artist) o, errors);
        }
    }


    @Override
    public void validate(Object target, Errors errors) {
        Artist artist = (Artist) target;
        // Checking name and surname
        if (StringUtils.isBlank(artist.getName())) {
            log.debug("Empty or null name provided for artist");
            errors.reject("artist.not.valid.name");
        }
        if (StringUtils.isBlank(artist.getSurname())) {
            log.debug("Empty or null surname provided for artist");
            errors.reject("artist.not.valid.surname");
        }
        // Checking date of birth
        if (artist.getDateOfBirth() == null) {
            log.debug("Null date of birth provided for artist");
            errors.reject("artist.not.valid.date.of.birth");
        } else {
            if (artist.getDateOfBirth()
                    .isAfter(LocalDate.now())) {
                log.debug("Invalid date of birth provided for artist: future date");
                errors.reject("artist.not.valid.date.of.birth");
            }
        }

        // Checking date of death
        if (artist.getDateOfDeath() != null) {
            LocalDate deathDate = artist.getDateOfDeath();
            if (deathDate.isAfter(LocalDate.now()) || deathDate.isBefore(artist.getDateOfBirth())) {
                log.warn("Invalid date of death provided for artist: {}", deathDate);
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
            logger.debug("");
        }
    }


}
