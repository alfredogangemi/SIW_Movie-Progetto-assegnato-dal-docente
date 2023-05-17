package it.uniroma3.siw.controller.validator;

import it.uniroma3.siw.model.Artist;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Component
public class ImageValidator implements Validator {


    @Value("${siwmovie.image.maxFileSize}")
    private Long maxImageFileSize;


    @Override
    public void validate(@NonNull Object o, @NonNull Errors errors) {
        MultipartFile file = (MultipartFile) o;
        if (!isImage(file)) {
            errors.reject("artist.upload.file.is.not.a.valid.image");
        } else if (file.getSize() > maxImageFileSize) {
            errors.reject("image.upload.file.exceeds.max.size");
        }
    }

    @Override
    public boolean supports(@NonNull Class<?> aClass) {
        return Artist.class.equals(aClass);
    }

    /**
     * Verifica se un MultiPartFile é una immagine
     *
     * @param file - MultipartFile
     * @return true se è una immagine, false altrimenti
     */
    private boolean isImage(MultipartFile file) {
        return isImage(MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())));
    }


    /**
     * @param type - Mediatype
     * @return true se è una immagine, false altrimenti
     */
    private boolean isImage(MediaType type) {
        if (type == null) {
            return false;
        }
        
        return type.equals(MediaType.IMAGE_JPEG) ||
                type.equals(MediaType.IMAGE_PNG) ||
                type.equals(MediaType.IMAGE_GIF);
    }


}
