# Testprotokoll

Datum: 27.10.2026

Getestet mit dem Skript `browsertests.py` (Playwright, Chromium 141) gegen eine leere MySQL-Datenbank (MySQL 8.0, Linux-Testrechner). Ein erster Lauf mit denselben Prüfungen fand am 17.10.2026 statt.

| Nr. | Prüfung | Ergebnis |
| --- | --- | --- |
| 1 | Anonyme Meldung erscheint unter „Gemeldet“ mit dem Vermerk „anonym“ | ok |
| 2 | Meldung mit Name und Telefonnummer, Name steht auf der Karte | ok |
| 3 | Nicht anonym, aber ohne Telefonnummer wird abgelehnt | ok |
| 4 | Breitengrad 95 und leere Pflichtfelder werden abgelehnt | ok |
| 5 | Bergung übernehmen, Name und Telefonnummer sind sichtbar | ok |
| 6 | Bergungsformular leer bzw. Telefonnummer „abc“ ergibt zwei Fehlermeldungen | ok |
| 7 | Zweite Bergung für dasselbe Netz wird abgelehnt | ok |
| 8 | Eine Person übernimmt zwei Netze, nur ein Datensatz in `person` | ok |
| 9 | Als geborgen melden | ok |
| 10 | Bergung abgeben, Netz steht wieder unter „Gemeldet“ | ok |
| 11 | Geborgenes Netz lässt sich per direkter Anfrage nicht erneut ändern | ok |
| 12 | Unbekannte Netznummer führt zur Übersicht mit Hinweis | ok |
| 13 | Bei 390 Pixeln Breite kein seitliches Scrollen | ok |
| 14 | Nach Neustart der Anwendung sind alle Netze, Status und Personen unverändert | ok |

Nicht getestet: andere Browser, echte Handys, viele gleichzeitige Zugriffe.

## Tests selbst ausführen

```
pip install playwright
playwright install chromium
```

1. Datenbank leeren und die Anwendung starten (`./mvnw spring-boot:run`).
2. `python browsertests.py vor`
3. Anwendung beenden und neu starten.
4. `python browsertests.py nach`
