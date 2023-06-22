package it.uniroma3.siw.controller;


import it.uniroma3.siw.controller.validator.ImageValidator;
import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.ImageData;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Controller
public class MovieController {
    private final MovieService movieService;
    private final ArtistService artistService;
    private final MovieValidator movieValidator;
    private final ImageValidator imageValidator;


    @Autowired
    public MovieController(MovieService movieService, ArtistService artistService, MovieValidator movieValidator, ImageValidator imageValidator) {
        this.movieService = movieService;
        this.artistService = artistService;
        this.movieValidator = movieValidator;
        this.imageValidator = imageValidator;
    }


    @GetMapping(value = "/admin/createNewMovie")
    public String formNewMovie(Model model) {
        model.addAttribute("movie", new Movie());
        log.info("Redirecting to form new movie");
        return "admin/formNewMovie";
    }

    @PostMapping("/admin/newMovie")
    public String createNewMovie(@Validated @ModelAttribute("movie") Movie movie, @RequestParam("coverImage") MultipartFile coverImage,
            @RequestParam("imageFiles") MultipartFile[] images, Model model,
            BindingResult bindingResult) {
        movieValidator.validate(movie, bindingResult, true);
        if (coverImage != null && !coverImage.isEmpty()) {
            imageValidator.validate(coverImage, bindingResult);
        } else {
            bindingResult.reject("movie.empty.cover");
        }
        if (images != null) {
            for (MultipartFile image : images) {
                imageValidator.validate(image, bindingResult);
            }
        }
        if (bindingResult.hasErrors()) {
            return "admin/formNewMovie";
        }
        try {
            movie.setCreationDate(LocalDateTime.now());
            movie.setAverageVote(0.0D);
            if (coverImage != null) {
                ImageData image = ImageData.builder()
                        .name(coverImage.getOriginalFilename())
                        .content(coverImage.getBytes())
                        .type(coverImage.getContentType())
                        .build();
                movie.setCover(image);
            }
            if (images != null) {
                Set<ImageData> movieImages = new HashSet<>();
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        ImageData imageData = ImageData.builder()
                                .name(image.getOriginalFilename())
                                .content(image.getBytes())
                                .type(image.getContentType())
                                .build();
                        movieImages.add(imageData);
                    }
                }
                log.info("Aggiunte {} immagini", movieImages.size());
                movie.setImages(movieImages.size() > 0 ? movieImages : null);
            }
            movieService.save(movie);
        } catch (IOException ioex) {
            log.error("Errore nella gestione degli allegati del film", ioex);
            bindingResult.reject("image.upload.generic.error");
            return "admin/formNewMovie";
        } catch (Exception ex) {
            log.error("Errore generico durante la creazione del film", ex);
            bindingResult.reject("movie.generic.error");
            return "admin/formNewMovie";
        }
        model.addAttribute("movie", movie);
        return "redirect:/movie/" + movie.getId();
    }

    @GetMapping("/movie/{id}")
    public String movie(@PathVariable("id") Long id, Model model) {
        Movie movie = movieService.findMovieById(id);
        if (movie != null) {
            model.addAttribute("movie", movie);
        }
        return "movie";
    }


    @GetMapping("/searchMovie")
    public String searchArtist(Model model) {
        Iterable<Movie> movies = movieService.getAll();
        model.addAttribute("movies", movies);
        return "searchMovie";
    }


    @GetMapping(value = "/admin/setDirectorToMovie/{directorId}/{movieId}")
    public String setDirectorToMovie(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId) {
        log.info("Adding director {} to movie {}", directorId, movieId);
        Movie movie = movieService.findMovieById(movieId);
        Artist director = artistService.findArtistById(directorId);
        movie.setDirector(director);
        movieService.save(movie);
        log.info("Director {} added to movie {}", directorId, movieId);
        return "redirect:/movie/" + movieId;
    }


    @GetMapping(value = "/admin/addDirector/{id}")
    public String addDirector(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artists", artistService.getAll());
        model.addAttribute("movie", movieService.findMovieById(id));
        return "admin/addDirectorToMovie";
    }


    @GetMapping("/admin/updateActors/{id}")
    public String updateActors(@PathVariable("id") Long id, Model model) {
        log.info("Form update actors for movie {}", id);
        model.addAttribute("actorsToAdd", artistService.retrieveArtistsNotInMovie(id));
        model.addAttribute("movie", movieService.findMovieById(id));
        return "admin/updateActors";
    }

    @GetMapping(value = "/admin/addActorToMovie/{actorId}/{movieId}")
    public String addActorToMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
        log.debug("Adding actor with ID {} to movie with ID {}", actorId, movieId);
        Movie movie = movieService.findMovieById(movieId);
        Artist actor = artistService.findArtistById(actorId);
        Set<Artist> actors = movie.getActors();
        actors.add(actor);
        movieService.save(movie);
        log.debug("Actor with ID {} added to movie with ID {}", actorId, movieId);
        return updateActors(movieId, model);
    }

    @GetMapping(value = "/admin/removeActorFromMovie/{actorId}/{movieId}")
    public String removeActorFromMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
        log.debug("Removing actor with ID {} from movie with ID {}", actorId, movieId);
        Movie movie = movieService.findMovieById(movieId);
        Artist actor = artistService.findArtistById(actorId);
        Set<Artist> actors = movie.getActors();
        actors.remove(actor);
        movieService.save(movie);
        log.debug("Actor with ID {} removed from movie with ID {}", actorId, movieId);
        return updateActors(movieId, model);
    }



    @PostMapping("/admin/movie/delete")
    public String delete(@ModelAttribute("id") Long id) {
        if (id != null && movieService.existsById(id)) {
            log.debug("Deleting movie with ID: {}", id);
            movieService.deleteById(id);
            log.debug("Movie with ID {} deleted successfully", id);
        } else {
            log.warn("Error while deleting movie with ID {}", id);
        }
        return "redirect:/";
    }



}
