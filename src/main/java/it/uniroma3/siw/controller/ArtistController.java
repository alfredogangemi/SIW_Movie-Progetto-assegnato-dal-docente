package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.controller.validator.ImageValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
public class ArtistController {

    protected final ArtistService artistService;
    protected final ArtistValidator artistValidator;
    protected final ImageValidator imageValidator;
    protected final MovieService movieService;

    @Autowired
    public ArtistController(ArtistService artistService, ArtistValidator artistValidator, ImageValidator imageValidator, MovieService movieService) {
        this.artistService = artistService;
        this.artistValidator = artistValidator;
        this.imageValidator = imageValidator;
        this.movieService = movieService;
    }


    @GetMapping(value = "/admin/createNewArtist")
    public String formNewArtist(Model model) {
        model.addAttribute("artist", new Artist());
        log.info("Redirecting to form new artist");
        return "admin/formNewArtist";
    }


    @PostMapping("/admin/newArtist")
    public String createNewArtist(@Validated @ModelAttribute("artist") Artist artist, @RequestParam("coverFile") MultipartFile file, Model model,
            BindingResult bindingResult) {
        log.debug("Starting create new artist..");
        artistValidator.validate(artist, bindingResult, true);
        if (file != null && !file.isEmpty()) {
            imageValidator.validate(file, bindingResult);
        }
        if (bindingResult.hasErrors()) {
            return "admin/formNewArtist";
        }
        try {
            if (file != null && !file.isEmpty()) {
                artistService.save(artist, file);
            } else {
                artistService.save(artist);
            }
            log.debug("New artist created with ID: {}", artist.getId());
        } catch (IOException ioex) {
            log.error("Error handling attachment in artist", ioex);
            bindingResult.reject("image.upload.generic.error");
            return "admin/formNewArtist";
        } catch (Exception ex) {
            log.error("Generic error during artist creation", ex);
            bindingResult.reject("artist.generic.error");
            return "admin/formNewArtist";
        }
        model.addAttribute("artist", artist);
        return "redirect:/artist/" + artist.getId();
    }



    @PostMapping("/admin/updateArtist")
    public String updateArtist(@Validated @ModelAttribute("artist") Artist artist, @RequestParam("coverFile") MultipartFile file, Model model,
            BindingResult bindingResult, @ModelAttribute("id") Long id) {
        log.debug("Updating artist with ID: {}", id);
        if (id != null && !artistService.existsById(id)) {
            bindingResult.reject("artist.generic.error");
            log.warn("Artist with ID {} does not exist. Update operation aborted.", id);
            return "admin/formUpdateArtist";
        }
        artistValidator.validate(artist, bindingResult, false);
        boolean isNewImageUpdated = file != null && !file.isEmpty();
        if (isNewImageUpdated) {
            imageValidator.validate(file, bindingResult);
        }
        if (bindingResult.hasErrors()) {
            return "admin/formUpdateArtist";
        }
        try {
            artist.setId(id);
            if (isNewImageUpdated) {
                artistService.save(artist, file);
            } else {
                artistService.saveWithPresentImage(artist);
            }
            log.debug("Artist with ID {} updated successfully", id);
        } catch (IOException ioex) {
            log.error("Error handling attachment for artist", ioex);
            bindingResult.reject("image.upload.generic.error");
            return "admin/formNewArtist";
        } catch (Exception ex) {
            log.error("Generic error occurred during artist update", ex);
            bindingResult.reject("artist.generic.error");
            return "admin/formUpdateArtist";
        }

        model.addAttribute("artist", artist);
        return "redirect:/artist/" + artist.getId();
    }



    @GetMapping("/artist/{id}")
    public String getArtist(@PathVariable("id") Long id, Model model) {
        log.debug("Fetching artist with ID: {}", id);
        Artist artist = artistService.findArtistById(id);
        if (artist != null) {
            model.addAttribute("artist", artist);
        }
        return "artist";
    }

    @GetMapping("/admin/artist/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        log.info("Redirecting to artist update form for artist {}", id);
        Artist artist = artistService.findArtistById(id);
        model.addAttribute("artist", artist);
        return "admin/formUpdateArtist";
    }

    @GetMapping("/searchArtist")
    public String searchArtist(Model model) {
        log.trace("Redirecting to searchArtists...");
        Iterable<Artist> artists = artistService.getAll();
        model.addAttribute("artists", artists);
        return "searchArtist";
    }


    @PostMapping("/admin/artist/delete")
    public String delete(@ModelAttribute("id") Long id) {
        if (id != null && artistService.existsById(id)) {
            log.debug("Deleting artist with ID and removing all references: {}", id);
            Artist artist = artistService.findArtistById(id);
            List<Movie> starredMovies = movieService.getArtistStarredMovies(artist);
            for (Movie movie : starredMovies) {
                movie.getActors()
                        .remove(artist);
                movieService.save(movie);
            }
            List<Movie> directedMovies = movieService.getArtistDirectedMovies(artist);
            for (Movie movie : directedMovies) {
                movie.setDirector(null);
                movieService.save(movie);
            }
            artistService.deleteById(id);
            log.debug("Deleting artist with ID: {}", id);
        } else {
            log.warn("Errore durante l'emininazione dell'artista con id {}", id);
        }
        return "redirect:/";
    }


}
