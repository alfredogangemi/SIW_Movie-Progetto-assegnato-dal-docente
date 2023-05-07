package it.uniroma3.siw.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ImageData {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private String id;

    private String name;

    @Lob
    @Column(nullable = false)
    private byte[] content;

    private String type;

}
