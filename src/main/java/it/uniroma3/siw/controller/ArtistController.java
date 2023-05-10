package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArtistController {

    @Autowired
    private ArtistRepository artistRepository;
    private final Logger logger = LoggerFactory.getLogger(ArtistController.class);

    //TODO -> Use validator


    @GetMapping(value = "/createNewArtist")
    public String formNewMovie(Model model) {
        model.addAttribute("artist", new Artist());
        logger.info("Redirecting to form new artist");
        return "formNewArtist";
    }


}
