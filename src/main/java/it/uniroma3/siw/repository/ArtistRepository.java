package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Movie;
import org.springframework.data.repository.CrudRepository;

public interface ArtistRepository<Artist, Long> extends CrudRepository<Movie, Long> {

}
