package io.github.wladiare.ghostnet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.wladiare.ghostnet.model.NetzStatus;
import io.github.wladiare.ghostnet.service.GeisternetzService;

@Controller
public class NetzController {

    private final GeisternetzService service;

    public NetzController(GeisternetzService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String uebersicht(Model model) {
        var spalten = service.netzeNachStatus();
        model.addAttribute("spalten", spalten);
        model.addAttribute("nochZuBergen",
                spalten.get(NetzStatus.GEMELDET).size() + spalten.get(NetzStatus.BERGUNG_BEVORSTEHEND).size());
        return "uebersicht";
    }

    @GetMapping("/netze/neu")
    public String meldenFormular(Model model) {
        model.addAttribute("meldung", new MeldungForm());
        return "melden";
    }

    @PostMapping("/netze")
    public String melden(@Validated @ModelAttribute("meldung") MeldungForm meldung,
                         BindingResult fehler, RedirectAttributes redirect) {
        if (fehler.hasErrors()) {
            return "melden";
        }
        String name = meldung.isAnonym() ? null : meldung.getName();
        String telefon = meldung.isAnonym() ? null : meldung.getTelefonnummer();
        var netz = service.melden(meldung.getBreitengrad(), meldung.getLaengengrad(),
                meldung.getGeschaetzteGroesse(), name, telefon);
        redirect.addFlashAttribute("hinweis", "Netz Nr. " + netz.getId() + " wurde gemeldet. Danke!");
        return "redirect:/";
    }

    @GetMapping("/netze/{id}/bergung")
    public String bergungFormular(@PathVariable Long id, Model model) {
        model.addAttribute("netz", service.findeNetz(id));
        model.addAttribute("bergung", new BergungForm());
        return "bergung";
    }

    @PostMapping("/netze/{id}/bergung")
    public String bergungUebernehmen(@PathVariable Long id,
                                     @Validated @ModelAttribute("bergung") BergungForm bergung,
                                     BindingResult fehler, Model model, RedirectAttributes redirect) {
        if (fehler.hasErrors()) {
            model.addAttribute("netz", service.findeNetz(id));
            return "bergung";
        }
        try {
            service.bergungUebernehmen(id, bergung.getName(), bergung.getTelefonnummer());
            redirect.addFlashAttribute("hinweis", "Du bist für die Bergung von Netz Nr. " + id + " eingetragen.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("fehler", e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/netze/{id}/geborgen")
    public String alsGeborgenMelden(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.alsGeborgenMelden(id);
            redirect.addFlashAttribute("hinweis", "Netz Nr. " + id + " ist als geborgen gemeldet.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("fehler", e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/netze/{id}/abgeben")
    public String bergungAbgeben(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.bergungAbgeben(id);
            redirect.addFlashAttribute("hinweis", "Die Bergung von Netz Nr. " + id + " ist wieder frei.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("fehler", e.getMessage());
        }
        return "redirect:/";
    }

    // z. B. /netze/999/bergung aufgerufen
    @ExceptionHandler(IllegalArgumentException.class)
    public String unbekanntesNetz(IllegalArgumentException e, RedirectAttributes redirect) {
        redirect.addFlashAttribute("fehler", e.getMessage());
        return "redirect:/";
    }
}
