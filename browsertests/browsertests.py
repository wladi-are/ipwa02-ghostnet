"""Browsertests für den Ghost-Net-Prototyp (Playwright, Chromium).

Voraussetzung: Die Anwendung läuft unter http://localhost:8080 mit einer
LEEREN Datenbank. Aufruf:

    pip install playwright && playwright install chromium
    python browsertests.py vor        # alle Tests außer dem Neustart
    # Anwendung beenden und neu starten
    python browsertests.py nach       # prüft, ob die Daten noch da sind
"""
import json
import sys
from pathlib import Path

from playwright.sync_api import sync_playwright

BASIS = "http://localhost:8080"
STAND = Path(__file__).with_name("board_vor_neustart.json")
ergebnisse = []


def pruefe(nr, name, bedingung):
    ergebnisse.append((nr, name, bool(bedingung)))
    print(f"{nr:>2}  {'ok    ' if bedingung else 'FEHLER'}  {name}")


def karten(seite, spalte):
    return seite.locator(f"section.spalte.{spalte} article.karte")


def absenden(seite):
    with seite.expect_navigation():
        seite.click("button[type=submit]")


def melden(seite, breite, laenge, groesse, anonym, name="", telefon=""):
    seite.goto(BASIS + "/netze/neu")
    seite.fill("#breitengrad", str(breite))
    seite.fill("#laengengrad", str(laenge))
    seite.fill("#geschaetzteGroesse", str(groesse))
    if anonym:
        seite.check("input[type=checkbox]")
    seite.fill("#name", name)
    seite.fill("#telefonnummer", telefon)
    absenden(seite)


def bergung(seite, netz, name, telefon):
    seite.goto(f"{BASIS}/netze/{netz}/bergung")
    seite.fill("#name", name)
    seite.fill("#telefonnummer", telefon)
    absenden(seite)


def board_text(seite):
    seite.goto(BASIS + "/")
    return seite.locator(".board").inner_text()


def vor_neustart(seite, browser):
    melden(seite, 95, 7.8, 40, False, "Jana Peters", "")
    pruefe(4, "Breitengrad 95 wird abgelehnt", "zwischen -90 und 90" in seite.content())
    pruefe(3, "Nicht anonym ohne Telefonnummer wird abgelehnt", "Name und Telefonnummer" in seite.content())
    melden(seite, "", "", "", True)
    pruefe(4, "Leere Pflichtfelder werden abgelehnt", seite.locator(".feldfehler").count() >= 3)

    melden(seite, 54.1782, 7.8865, 40, True)
    g = karten(seite, "gemeldet")
    pruefe(1, "Anonyme Meldung erscheint unter Gemeldet mit Vermerk anonym",
           g.count() == 1 and "anonym" in g.first.inner_text())
    melden(seite, 55.0123, 6.441, 120, False, "Jana Peters", "0151 2345678")
    pruefe(2, "Meldung mit Name und Telefonnummer", "Jana Peters" in karten(seite, "gemeldet").nth(1).inner_text())
    melden(seite, 54.4512, 11.0921, 15, True)

    bergung(seite, 1, "", "abc")
    pruefe(6, "Bergungsformular prüft Pflichtfeld und Format", seite.locator(".feldfehler").count() == 2)
    bergung(seite, 1, "Tom Becker", "0171 9876543")
    b = karten(seite, "bergung_bevorstehend")
    pruefe(5, "Bergung übernommen, Name und Telefonnummer sichtbar",
           b.count() == 1 and "Tom Becker" in b.first.inner_text() and "0171 9876543" in b.first.inner_text())
    bergung(seite, 2, "Tom Becker", "0171 9876543")
    pruefe(8, "Eine Person übernimmt zwei Netze", karten(seite, "bergung_bevorstehend").count() == 2)
    bergung(seite, 1, "Lena Koch", "0160 1112223")
    pruefe(7, "Zweite Bergung für dasselbe Netz wird abgelehnt",
           "bereits eine Bergung" in seite.content()
           and "Tom Becker" in karten(seite, "bergung_bevorstehend").first.inner_text())

    karten(seite, "bergung_bevorstehend").first.locator("text=Als geborgen melden").click()
    seite.wait_for_load_state()
    pruefe(9, "Als geborgen melden", karten(seite, "geborgen").count() == 1)
    karten(seite, "bergung_bevorstehend").first.locator("text=Bergung abgeben").click()
    seite.wait_for_load_state()
    pruefe(10, "Bergung abgeben, Netz wieder unter Gemeldet",
           karten(seite, "bergung_bevorstehend").count() == 0 and karten(seite, "gemeldet").count() == 2)

    seite.request.post(BASIS + "/netze/1/geborgen")
    seite.goto(BASIS + "/")
    pruefe(11, "Geborgenes Netz lässt sich per direkter Anfrage nicht erneut ändern",
           karten(seite, "geborgen").count() == 1)
    seite.goto(BASIS + "/netze/999/bergung")
    pruefe(12, "Unbekannte Netznummer führt zur Übersicht mit Hinweis",
           seite.url.rstrip("/") == BASIS and "nicht gefunden" in seite.content())

    # Endstand für die Screenshots und den Neustart-Test
    bergung(seite, 2, "Lena Koch", "0160 1112223")
    bergung(seite, 3, "Tom Becker", "0171 9876543")
    melden(seite, 53.9, 8.6, 60, False, "Ole Hansen", "04721 55667")

    mobil = browser.new_page(viewport={"width": 390, "height": 844}, locale="de-DE")
    mobil.goto(BASIS + "/")
    pruefe(13, "Kein seitliches Scrollen bei 390 Pixeln Breite",
           mobil.evaluate("document.documentElement.scrollWidth <= window.innerWidth"))
    STAND.write_text(json.dumps(board_text(seite)), encoding="utf-8")


def nach_neustart(seite):
    vorher = json.loads(STAND.read_text(encoding="utf-8"))
    pruefe(14, "Nach dem Neustart sind alle Daten unverändert", board_text(seite) == vorher)


if __name__ == "__main__":
    phase = sys.argv[1] if len(sys.argv) > 1 else "vor"
    with sync_playwright() as p:
        browser = p.chromium.launch()
        seite = browser.new_page(viewport={"width": 1280, "height": 900}, locale="de-DE")
        if phase == "vor":
            vor_neustart(seite, browser)
        else:
            nach_neustart(seite)
        print("Chromium", browser.version)
        browser.close()
    fehler = [e for e in ergebnisse if not e[2]]
    print(f"{len(ergebnisse) - len(fehler)} von {len(ergebnisse)} Prüfungen bestanden")
    sys.exit(1 if fehler else 0)
