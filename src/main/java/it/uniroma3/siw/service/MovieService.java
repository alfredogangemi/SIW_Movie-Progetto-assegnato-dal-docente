package it.uniroma3.siw.service;

import it.uniroma3.siw.dto.MoviePreviewDto;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class MovieService {


    protected final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }


    public boolean existsByTitleAndYear(Movie movie) {
        return movieRepository.existsByTitleAndYear(movie.getTitle(), movie.getYear());
    }

    @Transactional
    public void save(Movie movie) {
        log.info("Saving new movie:  {}", movie.getTitle());
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


    public boolean existsById(Long id) {
        return movieRepository.existsById(id);
    }


    @Transactional
    public void deleteById(Long id) {
        movieRepository.deleteById(id);
    }


    @Transactional
    public List<Movie> getArtistStarredMovies(Artist artist) {
        Set<Artist> set = new HashSet<Artist>();
        set.add(artist);
        return movieRepository.findAllByActorsIn(set);
    }

    @Transactional
    public List<Movie> getArtistDirectedMovies(Artist artist) {
        return movieRepository.findAllByDirector(artist);
    }
}
