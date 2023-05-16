package it.uniroma3.siw.controller.validator;

import io.micrometer.common.util.StringUtils;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
public class ArtistValidator implements Validator {
    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistValidator(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;

    }

    @Override
    public void validate(@NonNull Object o, @NonNull Errors errors) {
        Artist artist = (Artist) o;
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
        if (!artistRepository.existsByNameAndSurnameAndDateOfBirth(artist.getName(), artist.getSurname(), artist.getDateOfBirth())) {
            errors.reject("artist.already.exist");
        }
    }


}
