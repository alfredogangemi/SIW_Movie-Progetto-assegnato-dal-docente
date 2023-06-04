package it.uniroma3.siw.controller;

import it.uniroma3.siw.dto.MoviePreviewDto;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@Slf4j
public class AuthenticationController {

    private final CredentialsService credentialsService;
    private final UserService userService;
    private final MovieService movieService;

    @Autowired
    public AuthenticationController(MovieService movieService, UserService userService, CredentialsService credentialsService) {
        this.movieService = movieService;
        this.userService = userService;
        this.credentialsService = credentialsService;
    }

    @GetMapping(value = "/signUp")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("credentials", new Credentials());
        return "signUp";
    }

    @GetMapping(value = "/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping(value = "/")
    public String index(Model model) {
        List<MoviePreviewDto> latestMovies = movieService.getLastMoviesPreviews();
        List<MoviePreviewDto> topRatedMovies = movieService.getTopRatedMovies();
        model.addAttribute("latestMovies", latestMovies);
        model.addAttribute("topRatedMovies", topRatedMovies);
        /*
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return "index.html";
        } else {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            if (credentials.getRole()
                    .equals(Credentials.ADMIN_ROLE)) {
                return "admin/indexAdmin.html";
            }
        }*/
        return "index";
    }

    @PostMapping(value = "/doLogin")
    public String doLogin(Model model) {
        log.info("Doing login...");
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        if (credentials != null) {
            return "index";
        }
        return "login";
    }

    @PostMapping(value = {"/register"})
    public String registerUser(@ModelAttribute("user") User user,
            BindingResult userBindingResult,
            @ModelAttribute("credentials") Credentials credentials,
            BindingResult credentialsBindingResult,
            Model model) {
        //TODO -> Validate user and credentials
        if (!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
            userService.saveUser(user);
            credentials.setUser(user);
            credentialsService.saveCredentials(credentials);
            model.addAttribute("user", user);
            return "index"; //TODO -> Pagina di conferma
        }
        return "signUp";
    }
}
