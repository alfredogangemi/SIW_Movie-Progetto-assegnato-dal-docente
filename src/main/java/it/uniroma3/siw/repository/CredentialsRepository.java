package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Credentials;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CredentialsRepository extends CrudRepository<Credentials, Long> {

    Optional<Credentials> findByUsername(String username);

}
