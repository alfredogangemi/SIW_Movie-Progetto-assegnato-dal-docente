package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.ArtistValidator;
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

    private final ArtistRepository artistRepository;
    private final ArtistValidator artistValidator;
    private final Logger logger = LoggerFactory.getLogger(ArtistController.class);

    @Autowired
    public ArtistController(ArtistRepository artistRepository, ArtistValidator artistValidator) {
        this.artistRepository = artistRepository;
        this.artistValidator = artistValidator;
    }


    @GetMapping(value = "/createNewArtist")
    public String formNewMovie(Model model) {
        model.addAttribute("artist", new Artist());
        logger.info("Redirecting to form new artist");
        return "formNewArtist";
    }


    @PostMapping("/newArtist")
    public String createNewArtist(@ModelAttribute("artist") Artist artist, Model model) {
        if (!artistRepository.existsByNameAndSurnameAndDateOfBirth(artist.getName(), artist.getSurname(),
                artist.getDateOfBirth())) { //TODO -> Utilizzare validator
            artistRepository.save(artist);
            model.addAttribute("artist", artist);
            return "redirect:/artist/" + artist.getId();
        } else {
            model.addAttribute("errorMessage", "Questo artista esiste già");
            return "formNewArtist";
        }
    }


    @GetMapping("/artist/{id}")
    public String getArtist(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artist", artistRepository.findById(id)
                .get());
        return "artist";
    }

    @GetMapping("/artist/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid artist id:" + id));
        model.addAttribute("artist", artist);
        model.addAttribute("update", true);
        return "formNewArtist";
    }

    @GetMapping("/searchArtist")
    public String searchArtist(Model model) {
        Iterable<Artist> artists = artistRepository.findAll();
        model.addAttribute("artists", artists);
        return "searchArtist";
    }


    @PostMapping("/artist/delete")
    public String delete(@ModelAttribute("id") Long id) {
        //TODO -> Capire come si comporta nel caso di eliminazione di artista presente in film
        logger.info("Deleting artist with id {}", id);
        artistRepository.findById(id)
                .ifPresent(artistRepository::delete);
        return "redirect:/";
    }


}
