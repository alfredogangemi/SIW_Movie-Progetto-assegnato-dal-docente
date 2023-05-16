package it.uniroma3.siw.utils;

import it.uniroma3.siw.model.ImageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@PropertySource("classpath:application.properties")
public class ImageHandler {

    @Value("${siwmovie.image.maxFileSize}")
    private Long maxImageFileSize;
    private final Logger logger = LoggerFactory.getLogger(ImageHandler.class);

    public ImageData handleImage(MultipartFile file, Errors errors) {
        logger.info("Starting handle image. Max file size {}", maxImageFileSize);
        try {
            if (!isImage(file)) {
                errors.reject("artist.upload.file.is.not.a.valid.image");
                return null;
            } else if (file.getSize() > maxImageFileSize) {
                errors.reject("image.upload.file.exceeds.max.size");
                return null;
            }
            return ImageData.builder()
                    .name(file.getName())
                    .content(file.getBytes())
                    .type(file.getContentType())
                    .build();
        } catch (Exception e) {
            errors.reject("image.upload.generic.error");
            logger.warn("Errore generico durante l'upload dell'immagine", e);
        }
        return null;
    }


    /**
     * Verifica se un MultiPartFile é una immagine
     *
     * @param file
     * @return true se è una immagine, false altrimenti
     */
    private boolean isImage(MultipartFile file) {
        return isImage(MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())));
    }


    /**
     * @param type
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
