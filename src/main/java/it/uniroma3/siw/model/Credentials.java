package it.uniroma3.siw.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Credentials {

    public static final String DEFAULT_ROLE = "DEFAULT";
    public static final String ADMIN_ROLE = "ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String role;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;


}
