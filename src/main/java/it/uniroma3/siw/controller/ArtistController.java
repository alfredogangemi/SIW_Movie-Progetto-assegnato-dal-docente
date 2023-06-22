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
        //1. Validazione dell'artista
        artistValidator.validate(artist, bindingResult, true);
        //2. Validazione dell'immagine se presente
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
        } catch (IOException ioex) {
            log.error("Errore nella gestione dell'allegato nell'artista", ioex);
            bindingResult.reject("image.upload.generic.error");
            return "admin/formNewArtist";
        } catch (Exception ex) {
            log.error("Errore generico durante la creazione dell'artista", ex);
            bindingResult.reject("artist.generic.error");
            return "admin/formNewArtist";
        }
        model.addAttribute("artist", artist);
        return "redirect:/artist/" + artist.getId();
    }


    @PostMapping("/admin/updateArtist")
    public String updateArtist(@Validated @ModelAttribute("artist") Artist artist, @RequestParam("coverFile") MultipartFile file, Model model,
            BindingResult bindingResult, @ModelAttribute("id") Long id) {
        if (id != null && !artistService.existsById(id)) {
            bindingResult.reject("artist.generic.error");
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
        } catch (IOException ioex) {
            log.error("Errore nella gestione dell'allegato nell'artista", ioex);
            bindingResult.reject("image.upload.generic.error");
            return "admin/formNewArtist";
        } catch (Exception ex) {
            log.error("Errore generico durante l'aggiornamento dell'artista");
            ex.printStackTrace();
            bindingResult.reject("artist.generic.error");
            return "admin/formUpdateArtist";
        }
        model.addAttribute("artist", artist);
        return "redirect:/artist/" + artist.getId();
    }


    @GetMapping("/artist/{id}")
    public String getArtist(@PathVariable("id") Long id, Model model) {
        Artist artist = artistService.findArtistById(id);
        if (artist != null) {
            model.addAttribute("artist", artist);
        }
        return "artist";
    }

    @GetMapping("/admin/artist/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Artist artist = artistService.findArtistById(id);
        model.addAttribute("artist", artist);
        return "admin/formUpdateArtist";
    }

    @GetMapping("/searchArtist")
    public String searchArtist(Model model) {
        Iterable<Artist> artists = artistService.getAll();
        model.addAttribute("artists", artists);
        return "searchArtist";
    }


    @PostMapping("/admin/artist/delete")
    public String delete(@ModelAttribute("id") Long id) {
        if (id != null && artistService.existsById(id)) {
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
        } else {
            log.warn("Errore durante l'emininazione dell'artista con id {}", id);
        }
        return "redirect:/";
    }


}
