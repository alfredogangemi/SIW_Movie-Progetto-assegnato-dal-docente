package it.uniroma3.siw.dto;


import it.uniroma3.siw.model.Movie;
import lombok.Data;

@Data
public class MoviePreviewDto {

    private String title;
    private String imageSrc;
    private Double vote;

    public MoviePreviewDto(Movie movie) {
        this.title = movie.getTitle();
        this.imageSrc = movie.getCover()
                .generateHtmlSource();
        this.vote = movie.getAverageVote();
    }
}
