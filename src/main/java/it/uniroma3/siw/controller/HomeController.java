package it.uniroma3.siw.controller;

import it.uniroma3.siw.dto.MoviePreviewDto;
import it.uniroma3.siw.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class HomeController {

    private final MovieService movieService;

    @Autowired
    public HomeController(MovieService movieService) {
        this.movieService = movieService;
    }

    @RequestMapping("/")
    public String home(Model model) {
        List<MoviePreviewDto> latestMovies = movieService.getLastMoviesPreviews();
        List<MoviePreviewDto> topRatedMovies = movieService.getTopRatedMovies();
        model.addAttribute("latestMovies", latestMovies);
        model.addAttribute("topRatedMovies", topRatedMovies);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signUp")
    public String signUp() {
        return "signUp";
    }

}
