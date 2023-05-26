package it.uniroma3.siw.service;


import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.ImageData;
import it.uniroma3.siw.repository.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final Logger logger = LoggerFactory.getLogger(ArtistService.class);

    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Transactional
    public void save(Artist artist) {
        artistRepository.save(artist);
    }

    @Transactional
    public void save(Artist artist, MultipartFile file) throws IOException {
        if (file != null) {
            ImageData image = ImageData.builder()
                    .name(file.getOriginalFilename())
                    .content(file.getBytes())
                    .type(file.getContentType())
                    .build();
            artist.setImage(image);
        }
        artistRepository.save(artist);
    }

    public Artist findArtistById(Long id) {
        return artistRepository.findById(id)
                .orElse(null);
    }

    public Iterable<Artist> getAll() {
        return artistRepository.findAll();
    }

    public void deleteById(Long id) {
        logger.info("Deleting artist with id {}", id);
        artistRepository.findById(id)
                .ifPresent(artistRepository::delete);
    }


    public boolean existsById(Long id) {
        return artistRepository.existsById(id);
    }


    public void saveWithPresentImage(Artist artist) {
        Artist persistedArtist = artistRepository.findById(artist.getId())
                .get();
        ImageData oldImage = persistedArtist.getImage();
        artist.setImage(oldImage);
        artistRepository.save(artist);
    }

    public boolean existsByNameAndSurnameAndDateOfBirth(Artist artist) {
        return artistRepository.existsByNameAndSurnameAndDateOfBirth(artist.getName(), artist.getSurname(), artist.getDateOfBirth());
    }

    public Iterable<Artist> searchArtistsByNameOrSurname(String artistToSearch) {
        return artistRepository.searchArtistsByNameOrSurname(artistToSearch);
    }
}
