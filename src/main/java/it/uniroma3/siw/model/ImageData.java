package it.uniroma3.siw.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageData {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private String id;

    private String name;

    @Lob
    @Column(nullable = false)
    private byte[] content;

    private String type;

    public String generateHtmlSource() {
        return "data:" + this.type + ";base64," + Base64.getEncoder()
                .encodeToString(this.content);
    }

}
