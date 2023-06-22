package it.uniroma3.siw.dto;


import it.uniroma3.siw.model.Movie;
import lombok.Data;

@Data
public class MoviePreviewDto {

    private Long id;
    private String title;
    private String imageSrc;
    private Double vote;

    public MoviePreviewDto(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.imageSrc = movie.getCover()
                .generateHtmlSource();
        this.vote = movie.getAverageVote();
    }
}
