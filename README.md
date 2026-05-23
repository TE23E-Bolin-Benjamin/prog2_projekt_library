# Testdokumentation – LibSys

Detta dokument innehåller specifikationer och resultat för acceptanstester samt debuggtester för bibliotekssystemet LibSys, i enlighet med betygskraven för C.

---

## 1. Acceptanstester (Krav C)
Acceptanstester verifierar att systemets kärnfunktioner fungerar ur en användares perspektiv och uppfyller de uppställda verksamhetskraven.

### Acceptanstest 1: Skapa och verifiera en ny kund på servern
* **Syfte:** Att kontrollera att en helt ny användare kan registreras via ett POST-anrop till servern, sparas i databasen samt läggas till i programmets lokala cache.
* **Förutsättningar:** JSON-servern körs på `http://localhost:3000/` och applikationen är startad.

| Steg | Handling / Instruktion | Förväntat resultat | Status (Pass/Fail) |
| :--- | :--- | :--- | :--- |
| 1 | Starta applikationen (`Main.java`). | Huvudmenyn visas utan felmeddelanden. Kunddata läses in automatiskt. | Pass |
| 2 | Navigera till `2. Skapa & Lägg till` i huvudmenyn. | Undermenyn för att skapa nya entiteter visas korrekt. | Pass |
| 3 | Välj alternativ `3. Ny Användare`. | Systemet efterfrågar inmatning för namn och e-postadress. | Pass |
| 4 | Mata in namnet `Anna Andersson` och e-posten `anna@email.com`. | Texten *"Feedback: Användaren 'Anna Andersson' har sparats på servern."* skrivs ut. | Pass |
| 5 | Återgå till huvudmenyn, välj `1. Hämta & Visa` och sedan `4. Skriv ut alla kunder`. | `Anna Andersson` skrivs ut i terminalen, sorterad alfabetiskt baserat på namn. | Pass |

---

### Acceptanstest 2: Identifiering och visning av avstängda kunder
* **Syfte:** Att säkerställa att systemet korrekt kan mappa ihop en användare med dennes specifika `SuspendedUser` från servern och visa anledningen.
* **Förutsättningar:** Det finns minst en avstängd användare sparad på JSON-servern.

| Steg | Handling / Instruktion | Förväntat resultat | Status (Pass/Fail) |
| :--- | :--- | :--- | :--- |
| 1 | Välj alternativ `1. Hämta & Visa` i huvudmenyn. | Undermenyn för hämtningar visas. | Pass |
| 2 | Välj alternativ `5. Skriv ut alla suspenderade kunder`. | Systemet loopar igenom datan och mappar ihop `User` med `SuspendedUser` via ID. | Pass |
| 3 | Granska utskriften i terminalen. | Kundens namn, e-post samt avstängningsorsak (t.ex. *", Anledning: Sena böcker"*) visas tydligt. | Pass |

---

## 2. Debuggtester / Robusthetstester (Krav C)
Debuggtester fokuserar på systemets felhantering och robusthet vid extrema situationer, ogiltig indata eller korrupt databasstruktur (edge cases).

### Debuggtest 1: Hantering av korrupt JSON-data (Null-check)
* **Testscenario:** En post i `suspendedUsers` på JSON-servern saknar det obligatoriska fältet `userId` (är satt till `null`), vilket kan uppstå vid manuell redigering av `db.json` eller synkfel.
* **Syfte:** Verifiera att programmet inte kraschar med en `NullPointerException` utan hoppar över den felaktiga posten på ett säkert sätt.

* **Genomförande:**
  1. Stäng av applikationen.
  2. Öppna serverns `db.json` manuellt och lägg till en felaktig rad under `suspendedUsers`:
     ```json
     { "id": "error-id-123", "userId": null, "reason": "Korrupt data" }
     ```
  3. Starta applikationen, navigera till menyval `1` (Hämta & Visa) och välj `5` (Skriv ut alla suspenderade kunder).
* **Förväntat beteende:** Loopen i `findSuspendedUsers()` identifierar via en null-check att `userId` saknas, aktiverar en säkerhetsspärr (`continue;`) och fortsätter exekveras.
* **Resultat:** Systemet skriver ut övriga giltiga kunder utan att krascha. Inga undantag (Exceptions) kastas i terminalen.

---

### Debuggtest 2: Felaktig inmatning i terminalmenyn (Datatyp-mismatch)
* **Testscenario:** En slutanvändare matar in text eller specialtecken på ett ställe där programmet uttryckligen förväntar sig en siffra (heltal) för menyval.
* **Syfte:** Verifiera att programmets `try-catch`-block fångar upp `NumberFormatException` och förhindrar programkrasch.

* **Genomförande:**
  1. Starta applikationen och ställ dig i HUVUDMENY:n.
  2. Vid prompten *"Välj ett alternativ (1-5):"*, mata in strängen `tre` (i textform) eller lämna fältet tomt och tryck på Enter.
* **Förväntat beteende:** `Integer.parseInt(input)` kommer att kasta ett `NumberFormatException`. Detta fångas omedelbart upp av `catch`-blocket i `Main.java`.
* **Resultat:** Texten *"Nej, det där är inte en siffra. Försök igen!"* skrivs ut på skärmen. `while(run)`-loopen startar om och menyn ritas upp på nytt. Applikationen förblir aktiv.
