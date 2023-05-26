package it.uniroma3.siw.controller;


import it.uniroma3.siw.controller.validator.ImageValidator;
import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.ImageData;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
public class MovieController {
    private final Logger logger = LoggerFactory.getLogger(ArtistController.class);
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



    @GetMapping(value = "/createNewMovie")
    public String formNewArtist(Model model) {
        model.addAttribute("movie", new Movie());
        logger.info("Redirecting to form new movie");
        return "formNewMovie";
    }

    @PostMapping("/newMovie")
    public String createNewArtist(@Validated @ModelAttribute("movie") Movie movie, @RequestParam("coverFile") MultipartFile file, Model model,
            BindingResult bindingResult) {
        movieValidator.validate(movie, bindingResult, true);
        if (file != null && !file.isEmpty()) {
            imageValidator.validate(file, bindingResult);
        } else {
            bindingResult.reject("movie.empty.cover");
        }
        if (bindingResult.hasErrors()) {
            return "formNewMovie";
        }
        //TODO -> Gestire altre immagini
        try {
            movie.setCreationDate(LocalDateTime.now());
            movie.setAverageVote(0.0D);
            if (file != null) {
                ImageData image = ImageData.builder()
                        .name(file.getOriginalFilename())
                        .content(file.getBytes())
                        .type(file.getContentType())
                        .build();
                movie.setCover(image);
            }
            movieService.save(movie);
        } catch (IOException ioex) {
            logger.error("Errore nella gestione degli allegati del film", ioex);
            bindingResult.reject("image.upload.generic.error");
            return "formNewMovie";
        } catch (Exception ex) {
            logger.error("Errore generico durante la creazione del film", ex);
            bindingResult.reject("movie.generic.error");
            return "formNewMovie";
        }
        model.addAttribute("movie", movie);
        return "redirect:/movie/" + movie.getId();
    }

    @GetMapping("/movie/{id}")
    public String movie(@PathVariable("id") Long id, Model model) {
        Movie movie = movieService.findMovieById(id);
        if (movie != null) {
            model.addAttribute("movie", movie);
            if (movie.getCover() != null) {
                model.addAttribute("image", movie.getCover()
                        .generateHtmlSource());
            }
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
    public String setDirectorToMovie(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, Model model) {
        Artist director = artistService.findArtistById(directorId);
        Movie movie = movieService.findMovieById(movieId);
        movie.setDirector(director);
        movieService.save(movie);
        return "redirect:/movie/" + movieId;
    }


    @GetMapping(value = "/admin/addDirector/{id}")
    public String addDirector(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artists", artistService.getAll());
        model.addAttribute("movie", movieService.findMovieById(id));
        return "admin/addDirectorToMovie";
    }

}
