package it.uniroma3.siw.controller.validator;

import io.micrometer.common.util.StringUtils;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Slf4j
@Component
public class CredentialsValidator implements Validator {

    protected CredentialsService credentialsService;

    @Autowired
    public CredentialsValidator(CredentialsService credentialsService) {
        this.credentialsService = credentialsService;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Credentials credentials = (Credentials) target;
        if (StringUtils.isBlank(credentials.getUsername())) {
            log.debug("Empty or null username provided");
            errors.reject("user.empty.username");
            return;
        } else if (credentials.getUsername()
                .length() < 5) {
            log.debug("Username is too short: {}", credentials.getUsername());
            errors.reject("user.username.too.short");
        } else if (credentialsService.existsByUsername(credentials.getUsername())) {
            log.debug("Username {} already present", credentials.getUsername());
            errors.reject("user.username.already.present");
        }
        if (StringUtils.isBlank(credentials.getPassword())) {
            log.debug("Empty or null password provided");
            errors.reject("user.empty.password");
        } else if (credentials.getPassword()
                .length() < 8) {
            log.debug("Password is too short");
            errors.reject("user.password.too.short");
        }
    }

    @Override
    public boolean supports(@NonNull Class<?> aClass) {
        return Credentials.class.equals(aClass);
    }


}
