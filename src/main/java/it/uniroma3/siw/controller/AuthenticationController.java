package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.CredentialsValidator;
import it.uniroma3.siw.controller.validator.UserValidator;
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

    protected CredentialsService credentialsService;
    protected UserService userService;
    protected MovieService movieService;
    protected UserValidator userValidator;
    protected CredentialsValidator credentialsValidator;

    @Autowired
    public AuthenticationController(MovieService movieService, UserService userService, CredentialsService credentialsService,
            UserValidator userValidator, CredentialsValidator credentialsValidator) {
        this.movieService = movieService;
        this.userService = userService;
        this.credentialsService = credentialsService;
        this.userValidator = userValidator;
        this.credentialsValidator = credentialsValidator;
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
        return "index";
    }

    @GetMapping(value = "/success")
    public String defaultAfterLogin(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        return index(model);
    }

    @PostMapping(value = {"/register"})
    public String registerUser(@ModelAttribute("user") User user,
            BindingResult bindingResult,
            @ModelAttribute("credentials") Credentials credentials,
            Model model) {
        userValidator.validate(user, bindingResult);
        credentialsValidator.validate(credentials, bindingResult);
        if (bindingResult.hasErrors()) {
            return "signUp";
        }
        userService.saveUser(user);
        credentials.setUser(user);
        credentialsService.saveCredentials(credentials);
        model.addAttribute("user", user);
        return "signUpSuccessfully";
    }


    public User getCurrentUser() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Credentials credentials = credentialsService.getCredentials(user.getUsername());
        return credentials.getUser();
    }
}
