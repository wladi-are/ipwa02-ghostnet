package io.github.wladiare.ghostnet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.wladiare.ghostnet.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findFirstByNameAndTelefonnummer(String name, String telefonnummer);
}
