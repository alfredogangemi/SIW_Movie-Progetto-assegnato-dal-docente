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
    public void validate(@NonNull Object o, @NonNull Errors errors) {
        Credentials credentials = (Credentials) o;
        if (StringUtils.isBlank(credentials.getUsername())) {
            errors.reject("user.empty.username");
            return;
        } else if (credentials.getUsername().length() < 5) {
            errors.reject("user.username.too.short");
        } else if (credentialsService.existsByUsername(credentials.getUsername())) {
            errors.reject("user.username.already.present");
        }
        if (StringUtils.isBlank(credentials.getPassword())) {
            errors.reject("user.empty.password");
        } else if (credentials.getPassword().length() < 8) {
            errors.reject("user.password.too.short");
        }

    }

    @Override
    public boolean supports(@NonNull Class<?> aClass) {
        return Credentials.class.equals(aClass);
    }


}
