package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.controller.validator.ImageValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.ImageData;
import it.uniroma3.siw.service.ArtistService;
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
import java.util.Base64;

@Controller
public class ArtistController {

    private final ArtistService artistService;
    private final ArtistValidator artistValidator;
    private final ImageValidator imageValidator;
    private final Logger logger = LoggerFactory.getLogger(ArtistController.class);

    @Autowired
    public ArtistController(ArtistService artistService, ArtistValidator artistValidator, ImageValidator imageValidator) {
        this.artistService = artistService;
        this.artistValidator = artistValidator;
        this.imageValidator = imageValidator;
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
        //1. Validazione dell'artista
        artistValidator.validate(artist, bindingResult, true);
        //2. Validazione dell'immagine se presente
        if (file != null && !file.isEmpty()) {
            imageValidator.validate(file, bindingResult);
        }
        if (bindingResult.hasErrors()) {
            return "formNewArtist";
        }
        try {
            artistService.save(artist, file);
        } catch (IOException ioex) {
            logger.error("Errore nella gestione dell'allegato nell'artista", ioex);
            bindingResult.reject("image.upload.generic.error");
            return "formNewArtist";
        } catch (Exception ex) {
            logger.error("Errore generico durante la creazione dell'artista", ex);
            bindingResult.reject("artist.generic.error");
            return "formNewArtist";
        }
        model.addAttribute("artist", artist);
        return "redirect:/artist/" + artist.getId();
    }


    @PostMapping("/updateArtist")
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
            logger.error("Errore nella gestione dell'allegato nell'artista", ioex);
            bindingResult.reject("image.upload.generic.error");
            return "formNewArtist";
        } catch (Exception ex) {
            logger.error("Errore generico durante l'aggiornamento dell'artista");
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
            if (artist.getImage() != null) {
                model.addAttribute("image", generateHtmlSource(artist.getImage()));
            }
        }
        return "artist";
    }

    @GetMapping("/artist/edit/{id}")
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


    @PostMapping("/artist/delete")
    public String delete(@ModelAttribute("id") Long id, BindingResult bindingResult) {
        if (id != null && artistService.existsById(id)) {
            bindingResult.reject("artist.generic.error");
            return "admin/formUpdateArtist";
        }
        artistService.deleteById(id);
        return "redirect:/";
    }

    public String generateHtmlSource(ImageData image) {
        return "data:" + image.getType() + ";base64," + Base64.getEncoder()
                .encodeToString(image.getContent());
    }


}
