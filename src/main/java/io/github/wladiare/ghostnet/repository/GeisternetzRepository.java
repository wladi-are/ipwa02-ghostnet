package io.github.wladiare.ghostnet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.wladiare.ghostnet.model.Geisternetz;
import io.github.wladiare.ghostnet.model.NetzStatus;

public interface GeisternetzRepository extends JpaRepository<Geisternetz, Long> {

    // Abfrage wird von Spring Data aus dem Namen erzeugt
    List<Geisternetz> findByStatusOrderByGemeldetAmAsc(NetzStatus status);
}
