package it.uniroma3.siw.controller.validator;

import io.micrometer.common.util.StringUtils;
import it.uniroma3.siw.model.Review;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@Slf4j
public class ReviewValidator implements Validator {

    @Autowired
    public ReviewValidator() {
        super();
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        Review review = (Review) target;
        if (StringUtils.isBlank(review.getTitle())) {
            errors.reject("review.not.valid.title");
            log.error("Invalid review title: {}", review.getTitle());
        } else if (review.getTitle()
                .length() > 100) {
            errors.reject("review.title.too.long");
            log.error("Review title exceeds maximum length: {}", review.getTitle());
        }
        if (StringUtils.isBlank(review.getText())) {
            errors.reject("review.text.not.valid");
            log.error("Invalid review text");
        }
    }

    @Override
    public boolean supports(@NonNull Class<?> aClass) {
        return Review.class.equals(aClass);
    }

}
