package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ArtistController {

    @Autowired
    private ArtistRepository artistRepository;
    private final Logger logger = LoggerFactory.getLogger(ArtistController.class);

    //TODO -> Use validator
    //
    @GetMapping(value = "/createNewArtist")
    public String formNewMovie(Model model) {
        model.addAttribute("artist", new Artist());
        logger.info("Redirecting to form new artist");
        return "formNewArtist";
    }


    @PostMapping("/newArtist")
    public String newMovie(@ModelAttribute("artist") Artist artist, Model model) {
        if (!artistRepository.existsByNameAndSurnameAndDateOfBirth(artist.getName(), artist.getSurname(), artist.getDateOfBirth())) {
            artistRepository.save(artist);
            model.addAttribute("artist", artist);
            return getArtist(artist.getId(), model);
        } else {
            model.addAttribute("errorMessage", "Questo film esiste già");
            return "formNewArtist";
        }
    }


    @GetMapping("/artist/{id}")
    public String getArtist(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artist", artistRepository.findById(id)
                .get());
        return "artist";
    }


}
