package io.github.wladiare.ghostnet.controller;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MeldungForm {

    @NotNull(message = "Bitte den Breitengrad angeben.")
    @DecimalMin(value = "-90.0", message = "Der Breitengrad liegt zwischen -90 und 90.")
    @DecimalMax(value = "90.0", message = "Der Breitengrad liegt zwischen -90 und 90.")
    private Double breitengrad;

    @NotNull(message = "Bitte den Längengrad angeben.")
    @DecimalMin(value = "-180.0", message = "Der Längengrad liegt zwischen -180 und 180.")
    @DecimalMax(value = "180.0", message = "Der Längengrad liegt zwischen -180 und 180.")
    private Double laengengrad;

    @NotNull(message = "Bitte die geschätzte Größe angeben.")
    @Positive(message = "Die Größe muss größer als 0 sein.")
    @Max(value = 1000000, message = "Bitte eine realistische Größe angeben.")
    private Integer geschaetzteGroesse;

    private boolean anonym;

    @Size(max = 100, message = "Der Name ist zu lang.")
    private String name;

    @Pattern(regexp = "^$|^[+0-9 ()/-]{5,30}$", message = "Bitte eine gültige Telefonnummer angeben.")
    private String telefonnummer;

    // Name und Telefon sind nur Pflicht, wenn nicht anonym gemeldet wird
    @AssertTrue(message = "Ohne Haken bei „anonym“ werden Name und Telefonnummer benötigt.")
    public boolean isKontaktVollstaendig() {
        return anonym || (istGefuellt(name) && istGefuellt(telefonnummer));
    }

    private static boolean istGefuellt(String wert) {
        return wert != null && !wert.isBlank();
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

    public boolean isAnonym() {
        return anonym;
    }

    public void setAnonym(boolean anonym) {
        this.anonym = anonym;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }
}
