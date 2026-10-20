package io.github.wladiare.ghostnet.service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.wladiare.ghostnet.model.Geisternetz;
import io.github.wladiare.ghostnet.model.NetzStatus;
import io.github.wladiare.ghostnet.model.Person;
import io.github.wladiare.ghostnet.repository.GeisternetzRepository;
import io.github.wladiare.ghostnet.repository.PersonRepository;

@Service
@Transactional
public class GeisternetzService {

    private final GeisternetzRepository netzRepository;
    private final PersonRepository personRepository;

    public GeisternetzService(GeisternetzRepository netzRepository, PersonRepository personRepository) {
        this.netzRepository = netzRepository;
        this.personRepository = personRepository;
    }

    public Geisternetz melden(double breitengrad, double laengengrad, int groesse,
                              String name, String telefonnummer) {
        Geisternetz netz = new Geisternetz();
        netz.setBreitengrad(breitengrad);
        netz.setLaengengrad(laengengrad);
        netz.setGeschaetzteGroesse(groesse);
        netz.setStatus(NetzStatus.GEMELDET);
        netz.setGemeldetAm(LocalDateTime.now());
        if (name != null && !name.isBlank()) {
            netz.setMeldendePerson(findeOderLegeAn(name, telefonnummer));
        }
        return netzRepository.save(netz);
    }

    public void bergungUebernehmen(Long netzId, String name, String telefonnummer) {
        Geisternetz netz = findeNetz(netzId);
        // pro Netz nur eine bergende Person
        if (netz.getStatus() != NetzStatus.GEMELDET || netz.getBergendePerson() != null) {
            throw new IllegalStateException("Für dieses Netz ist bereits eine Bergung eingetragen.");
        }
        netz.setBergendePerson(findeOderLegeAn(name, telefonnummer));
        netz.setStatus(NetzStatus.BERGUNG_BEVORSTEHEND);
    }

    public void bergungAbgeben(Long netzId) {
        Geisternetz netz = findeNetz(netzId);
        if (netz.getStatus() != NetzStatus.BERGUNG_BEVORSTEHEND) {
            throw new IllegalStateException("Nur eine angekündigte Bergung kann abgegeben werden.");
        }
        netz.setBergendePerson(null);
        netz.setStatus(NetzStatus.GEMELDET);
    }

    public void alsGeborgenMelden(Long netzId) {
        Geisternetz netz = findeNetz(netzId);
        if (netz.getStatus() != NetzStatus.BERGUNG_BEVORSTEHEND) {
            throw new IllegalStateException("Nur ein Netz mit angekündigter Bergung kann als geborgen gemeldet werden.");
        }
        netz.setStatus(NetzStatus.GEBORGEN);
    }

    // fürs Board: eine Liste pro Status
    @Transactional(readOnly = true)
    public Map<NetzStatus, List<Geisternetz>> netzeNachStatus() {
        Map<NetzStatus, List<Geisternetz>> spalten = new EnumMap<>(NetzStatus.class);
        for (NetzStatus status : NetzStatus.values()) {
            spalten.put(status, netzRepository.findByStatusOrderByGemeldetAmAsc(status));
        }
        return spalten;
    }

    @Transactional(readOnly = true)
    public Geisternetz findeNetz(Long netzId) {
        return netzRepository.findById(netzId)
                .orElseThrow(() -> new IllegalArgumentException("Netz " + netzId + " wurde nicht gefunden."));
    }

    // vorhandene Person wiederverwenden statt doppelt anlegen
    private Person findeOderLegeAn(String name, String telefonnummer) {
        String n = name.trim();
        String t = telefonnummer.trim();
        return personRepository.findFirstByNameAndTelefonnummer(n, t)
                .orElseGet(() -> personRepository.save(new Person(n, t)));
    }
}
