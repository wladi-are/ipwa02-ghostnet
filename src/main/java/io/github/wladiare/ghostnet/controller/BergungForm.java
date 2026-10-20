package io.github.wladiare.ghostnet.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class BergungForm {

    @NotBlank(message = "Bitte den Namen angeben.")
    @Size(max = 100, message = "Der Name ist zu lang.")
    private String name;

    @NotBlank(message = "Bitte die Telefonnummer angeben.")
    @Pattern(regexp = "^[+0-9 ()/-]{5,30}$", message = "Bitte eine gültige Telefonnummer angeben.")
    private String telefonnummer;

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
