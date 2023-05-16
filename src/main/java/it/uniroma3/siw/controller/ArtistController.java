package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.ImageData;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.utils.ImageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ArtistController {

    private final ArtistRepository artistRepository;
    private final ArtistValidator artistValidator;
    private final ImageHandler imageHandler;
    private final Logger logger = LoggerFactory.getLogger(ArtistController.class);

    @Autowired
    public ArtistController(ArtistRepository artistRepository, ArtistValidator artistValidator, ImageHandler imageHandler) {
        this.artistRepository = artistRepository;
        this.artistValidator = artistValidator;
        this.imageHandler = imageHandler;
    }


    @GetMapping(value = "/createNewArtist")
    public String formNewArtist(Model model) {
        model.addAttribute("artist", new Artist());
        logger.info("Redirecting to form new artist");
        return "formNewArtist";
    }


    @PostMapping("/newArtist")
    public String createNewArtist(@Validated @ModelAttribute("artist") Artist artist, @RequestParam("coverFile") MultipartFile file, Model model,
            BindingResult bindingResult) {
        artistValidator.validate(artist, bindingResult);
        if (bindingResult.hasErrors()) {
            return "formNewArtist";
        }
        if (file != null && !file.isEmpty()) {
            ImageData image = imageHandler.handleImage(file, bindingResult);
            if (bindingResult.hasErrors()) {
                return "formNewArtist";
            } else {
                artist.setImage(image);
            }
        }
        artistRepository.save(artist);
        model.addAttribute("artist", artist);
        return "redirect:/artist/" + artist.getId();
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
