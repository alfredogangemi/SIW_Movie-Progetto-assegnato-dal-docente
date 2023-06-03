package it.uniroma3.siw.controller.validator;

import io.micrometer.common.util.StringUtils;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.service.ReviewService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ReviewValidator implements Validator {
    private final ReviewService reviewService;

    @Autowired
    public ReviewValidator(ReviewService reviewService) {
        this.reviewService = reviewService;
    }



    @Override
    public void validate(@NonNull Object o, @NonNull Errors errors) {
        Review review = (Review) o;
        if (StringUtils.isBlank(review.getTitle())) {
            errors.reject("review.not.valid.title");
        } else if (review.getTitle()
                .length() > 100) {
            errors.reject("review.title.too.long");
        }
        if (StringUtils.isBlank(review.getText())) {
            errors.reject("reviews.text.not.valid");
        }
    }

    @Override
    public boolean supports(@NonNull Class<?> aClass) {
        return Artist.class.equals(aClass);
    }

}
