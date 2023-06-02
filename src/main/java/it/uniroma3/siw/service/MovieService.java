package it.uniroma3.siw.service;

import it.uniroma3.siw.dto.MoviePreviewDto;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

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

    @Transactional
    public void save(Movie movie) {
        logger.info("Saving new movie:  {}", movie.getTitle());
        movieRepository.save(movie);
    }

    @Transactional
    public Movie findMovieById(Long id) {
        return movieRepository.findById(id)
                .orElse(null);
    }

    public Iterable<Movie> getAll() {
        return movieRepository.findAll();
    }


    @Transactional
    public List<MoviePreviewDto> getLastMoviesPreviews() {
        List<MoviePreviewDto> latestMovies = new LinkedList<>();
        movieRepository.findLatestMovies()
                .forEach(movie -> latestMovies.add(new MoviePreviewDto(movie)));
        return latestMovies;
    }



    @Transactional
    public List<MoviePreviewDto> getTopRatedMovies() {
        List<MoviePreviewDto> topRatedMovies = new LinkedList<>();
        movieRepository.findTopRatedMovies()
                .forEach(movie -> topRatedMovies.add(new MoviePreviewDto(movie)));
        return topRatedMovies;
    }


}
