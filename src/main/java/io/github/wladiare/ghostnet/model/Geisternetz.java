package io.github.wladiare.ghostnet.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Geisternetz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double breitengrad;

    @Column(nullable = false)
    private Double laengengrad;

    // in m²
    @Column(nullable = false)
    private Integer geschaetzteGroesse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NetzStatus status;

    @Column(nullable = false)
    private LocalDateTime gemeldetAm;

    // null = anonym gemeldet
    @ManyToOne
    @JoinColumn(name = "meldende_person_id")
    private Person meldendePerson;

    @ManyToOne
    @JoinColumn(name = "bergende_person_id")
    private Person bergendePerson;

    public Geisternetz() {
    }

    public boolean isAnonymGemeldet() {
        return meldendePerson == null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getBreitengrad() {
        return breitengrad;
    }

    public void setBreitengrad(Double breitengrad) {
        this.breitengrad = breitengrad;
    }

    public Double getLaengengrad() {
        return laengengrad;
    }

    public void setLaengengrad(Double laengengrad) {
        this.laengengrad = laengengrad;
    }

    public Integer getGeschaetzteGroesse() {
        return geschaetzteGroesse;
    }

    public void setGeschaetzteGroesse(Integer geschaetzteGroesse) {
        this.geschaetzteGroesse = geschaetzteGroesse;
    }

    public NetzStatus getStatus() {
        return status;
    }

    public void setStatus(NetzStatus status) {
        this.status = status;
    }

    public LocalDateTime getGemeldetAm() {
        return gemeldetAm;
    }

    public void setGemeldetAm(LocalDateTime gemeldetAm) {
        this.gemeldetAm = gemeldetAm;
    }

    public Person getMeldendePerson() {
        return meldendePerson;
    }

    public void setMeldendePerson(Person meldendePerson) {
        this.meldendePerson = meldendePerson;
    }

    public Person getBergendePerson() {
        return bergendePerson;
    }

    public void setBergendePerson(Person bergendePerson) {
        this.bergendePerson = bergendePerson;
    }
}
