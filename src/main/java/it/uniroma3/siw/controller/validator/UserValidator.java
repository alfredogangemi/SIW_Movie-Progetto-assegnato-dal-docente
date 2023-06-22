package it.uniroma3.siw.controller.validator;

import io.micrometer.common.util.StringUtils;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.UserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Slf4j
@Component
public class UserValidator implements Validator {

    protected UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void validate(@NonNull Object o, @NonNull Errors errors) {
        User user = (User) o;
        if (StringUtils.isBlank(user.getName())) {
            errors.reject("user.empty.name");
            log.debug("User name is empty.");
            return;
        }
        if (StringUtils.isBlank(user.getSurname())) {
            errors.reject("user.empty.surname");
            log.debug("User surname is empty.");
            return;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            errors.reject("user.empty.email");
            log.debug("User email is empty.");
        } else if (!EmailValidator.getInstance()
                .isValid(user.getEmail())) {
            errors.reject("user.not.valid.email");
            log.debug("User email is not valid.");
        } else if (userService.existsByEmail(user.getEmail())) {
            errors.reject("user.with.this.email.already.present");
            log.debug("User with this email already exists.");
        }
    }

    @Override
    public boolean supports(@NonNull Class<?> aClass) {
        return User.class.equals(aClass);
    }


}
