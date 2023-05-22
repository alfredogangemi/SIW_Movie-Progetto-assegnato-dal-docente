package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MovieService {


    private final MovieRepository movieRepository;
    private final Logger logger = LoggerFactory.getLogger(ArtistService.class);

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }



    public boolean existsByTitleAndYear(Movie movie) {
        return movieRepository.existsByTitleAndYear(movie.getTitle(), movie.getYear());
    }

    public void save(Movie movie) throws IOException {
        logger.info("Saving new movie:  {}", movie.getTitle());
        movieRepository.save(movie);

    }

    public Movie findArtistById(Long id) {
        return movieRepository.findById(id)
                .orElse(null);
    }
}
