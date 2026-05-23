package jk;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import java.lang.reflect.Type;
//import java.security.cert.CertPathValidatorException.Reason;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Benjamin Bolin
 * LibraryManager hanterar alla REST-API (GET, POST, DELETE) samt sök- och
 * sorteringsalgoritmer.
 */
public class LibraryManager {
    // Aggregat-listor enligt mitt klassdiagram
    private ArrayList<Media> mediaList = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<SuspendedUser> suspendedList = new ArrayList<>();

    // Ny Map för C-kravet, uppslagning av användare via ID
    private java.util.HashMap<String, User> userMap = new java.util.HashMap<>();

    private final String baseURL = "http://localhost:3000/";
    private final Gson gson = new Gson();

    // --- Hämtningar via GET ---
    public void fetchBooks() {
        try {
            HttpResponse<String> response = Unirest.get(baseURL + "books").asString();
            if (response.getStatus() == 200) {
                Type listType = new TypeToken<ArrayList<Book>>() {
                }.getType();
                ArrayList<Book> serverBooks = gson.fromJson(response.getBody(), listType);
                mediaList.addAll(serverBooks);
                System.out.println("Lyckades hämta " + serverBooks.size() + " böcker!");
            }
        } catch (Exception e) {
            System.out.println("Kunde inte hämta böcker.");
        }
    }

    public void fetchMagazines() {
        try {
            HttpResponse<String> response = Unirest.get(baseURL + "magazines").asString();
            if (response.getStatus() == 200) {
                Type listType = new TypeToken<ArrayList<Magazine>>() {
                }.getType();
                ArrayList<Magazine> serverMags = gson.fromJson(response.getBody(), listType);
                mediaList.addAll(serverMags);
                System.out.println("Lyckades hämta " + serverMags.size() + " tidningar!");
            }
        } catch (Exception e) {
            System.out.println("Kunde inte hämta tidningar.");
        }
    }

    // både användare och suspenderade användare hämtas via denna method
    public void fetchUsersAndSuspended() {
        try {
            String uBody = Unirest.get(baseURL + "users").asString().getBody();
            userList = gson.fromJson(uBody, new TypeToken<ArrayList<User>>() {}.getType());
            
            String key = "";
            // HashMap för snabba uppslag 
            userMap.clear(); // Tömmer den först så vi inte får gamla dubbletter
            for (User u : userList) {
                key = u.getId();
                userMap.put(key, u); //Key blir användarens ID och objekt blir value                                
            }
            // exempel på användning av key System.out.println(userMap.get(key)+ "är key"); 

            // Forsätter med arraylist
            String sBody = Unirest.get(baseURL + "suspendedUsers").asString().getBody();
            suspendedList = gson.fromJson(sBody, new TypeToken<ArrayList<SuspendedUser>>() {}.getType());
        } catch (Exception e) {
            System.out.println("Kunde inte hämta användardata från servern."); 
        }
    }

    // --- Kommunikation via POST ---
    public void addMediaToServer(Media m, String endpoint) {
        try {
            HttpResponse<String> response = Unirest.post(baseURL + endpoint)
                    .header("Content-Type", "application/json").body(gson.toJson(m)).asString();
            if (response.getStatus() == 201) {
                mediaList.add(m);
                System.out.println("Feedback: Media '" + m.getTitle() + "' har sparats på servern.");
            }
        } catch (Exception e) {
            System.out.println("Fel vid POST till servern.");
        }
    }
    // post anrop till servern
    public void addUserToServer(User u, SuspendedUser su) {
        try { 
            HttpResponse<String> response = Unirest.post(baseURL + "users")
                    .header("Content-Type", "application/json").body(gson.toJson(u)).asString();
            if (response.getStatus() == 201) {
                userList.add(u);
                System.out.println("Feedback: Användaren '" + u.getName() + "' har sparats på servern.");
            }
        } catch (Exception e) {
            System.out.println("Fel vid skapande av användare på servern.");
        }

        if (u != null && su != null) {
            addSuspendedUserToServer(u, su);
        }
    }

    // method när man ska välja befintlig användare och suspendera den
    public void addUserToSuspendedViaUsername(String name, String uuid, String reason) {    
        User u = getCustomerByName(name);            
        addSuspendedUserToServer(u, new SuspendedUser(uuid, u.getId(), reason));
   }

    // post-anrop metod som anropas från addUserToServer eller addUserToSuspendedViaUsername
    public void addSuspendedUserToServer(User u, SuspendedUser su) {
        try {
            HttpResponse<String> response = Unirest.post(baseURL + "suspendedUsers")
                    .header("Content-Type", "application/json").body(gson.toJson(su)).asString();
            if (response.getStatus() == 201) {  // 201 Created 
                suspendedList.add(su);
                System.out.println("Feedback: Användaren '" + u.getName() + "' har suspenderats på servern.");
            }
        } catch (Exception e) {
            System.out.println("Fel vid suspendering av användare på servern.");
        }
    }

    // Kommunikation med servern via DELETE
    public void removeMediaFromServer(String title) {
        Media target = null;
        for (Media m : mediaList) {
            if (m.getTitle().equalsIgnoreCase(title)) {
                target = m;
                break;
            }
        }
        if (target != null) {
            String endpoint = (target instanceof Book) ? "books/" : "magazines/";
            int status = Unirest.delete(baseURL + endpoint + target.getId()).asString().getStatus();
            if (status == 200 || status == 204) { // 200 ok 204 ok men inget text tillbaka 
                mediaList.remove(target);
                System.out.println("Feedback: Raderade '" + title + "' från servern.");
            }
        } else {
            System.out.println("Feedback: Kunde inte hitta media.");
        }
    }

    // Kommunikation med servern via DELETE
    // denna method används om man vet userid
    public void removeSuspended(String userId) {
        SuspendedUser target = null;
        for (SuspendedUser su : suspendedList) {
            // Kontrollerar att objektet existerar och jämför för säkerhetsskull
            if (su != null && userId != null && userId.equals(su.getUserId())) {
                target = su;
                break;
            }
        }
        if (target != null) {
            // obs före target.getId() är slash annars fel url när man lägger till userId 
            int status = Unirest.delete(baseURL + "suspendedUsers/" + target.getId()).asString().getStatus();
            if (status == 200 || status == 204) {
                suspendedList.remove(target);
                System.out.println("Feedback: '" + userId + "' är nu aktiverad.");
            }
        } else {
            System.out.println("Feedback: Kunde inte hitta suspendedUser.");
        }
    }

    // om man vet namn på användaren som är suspenderad så kan man aktivera direkt
    public void removeSuspendedUserViaUsername(String name) {
        User u = getCustomerByName(name);
        if (u != null) {
            SuspendedUser target = getSuspendedCustomer(u.getId());
            if (target != null) {
                int status = Unirest.delete(baseURL + "suspendedUsers/" + target.getId()).asString().getStatus();
                if (status == 200 || status == 204) {
                    suspendedList.remove(target);
                    System.out.println("Feedback: '" + name + "' är nu aktiverad.");
                }
            } else {
                System.out.println("Feedback: Kunde inte hitta suspenderad användare.");
            }
        } else {
            System.out.println("Feedback: removeSuspendedUserViaUsername kunde inte hitta användaren.");
        }
    }

    public void removeUserByEmail(String email) {
        User target = getCustomerByEmail(email);
        if (target != null) {
            int status = Unirest.delete(baseURL + "users/" + target.getId()).asString().getStatus();
            if (status == 200 || status == 204) {
                userList.remove(target);
                System.out.println("Feedback: Användare med e-post '" + email + "' är nu borttagen.");
            }
        } else {
            System.out.println("Feedback: removeUserByEmail kunde inte hitta användare.");
        }
    }

    // Algoritmer för sökning och sortering
    public void printMediaSorted() {
        if (mediaList.isEmpty()) {
            System.out.println("Samlingen är tom lokalt. Hämta data från servern först.");
        } else {
            Collections.sort(mediaList); // Sorterar samlingen automatiskt via Comparable
            for (Media m : mediaList)
                System.out.println(m.getInfo());
        }
    }

    public void printAllUsersSorted() {
        if (userList.isEmpty()) {
            System.out.println("Inga kunder inladdade.");
        } else {
            Collections.sort(userList); // Sorterar kunder automatiskt via Comparable på namn
            for (User u : userList)
                System.out.println(u.getInfo());
        }
    }

    // metod för att använda ett mängd via HashSet
    public void printUniqueGenres() {
        java.util.HashSet<String> uniqueGenres = new java.util.HashSet<>();
        
        for (Media m : mediaList) {
            if (m instanceof Book) {
                Book b = (Book) m;
                uniqueGenres.add(b.getGenre().toLowerCase()); // Set ignorerar automatiskt dubbletter
            }
        }
        
        System.out.println("Följande unika bok-genrer finns i biblioteket (om du har hämtat dem från servern):");
        for (String genre : uniqueGenres) {
            System.out.println("- " + genre);
        }
    }

    public void findBookOrMagazine(String title) {
        boolean hittad = false;
        for (Media m : mediaList) {
            if (m.getTitle().toLowerCase().contains(title.toLowerCase())) {
                System.out.println(m.getInfo());
                hittad = true;
            }
        }
        if (!hittad)
            System.out.println("Ingen media matchade sökningen.");
    }

    // methoden visar alla användare som matchar
    public void findCustomerByNameAndEmail(String nameAndEmail) {
        boolean found = false;
        for (User u : userList) {
            if (u.getEmail().equalsIgnoreCase(nameAndEmail) || u.getName().equalsIgnoreCase(nameAndEmail)) {
                System.out.println(u.getInfo()); // kan inte visa reason pga modellering och vill inte söka i suspended
                found = true; // ingen breake visar alla användare
            }
        }
        if (!found)
            System.out.println("Ingen användare matchade sökningen.");
    }
    
    // Method för att visa icke suspenderade kunder
    public void findAvailableCustomers() {
        if (userList == null || suspendedList == null) {
            System.out.println("Användardata är inte korrekt inladdad.");
            return;
        }
        boolean showError = true; // ytttre variabiel för kontroll av utksrift
        for (User u : userList) {
            if (u == null || u.getId() == null) continue;
            boolean suspended = false;
            //  Kontrollerar om den aktuella användaren finns i listan över avstängda
            for (SuspendedUser su : suspendedList) {
                if (su == null || su.getUserId() == null) continue;
                
                if (u.getId().equals(su.getUserId())) {
                    suspended = true;
                    showError = false; //hittar suspenderad
                    break;
                }
            }
            if (!suspended) {
                System.out.println(u.getInfo());
            }
        }

        if (showError) {
            System.out.println("Hittade ingen Kund");
        }
    }
    // printar suspenderade användare
    public void findSuspendedUsers() {
        System.out.println("Kunder som inte får låna är: ");
        // kontroll ifall listorna skulle saknas helt
        if (userList == null || suspendedList == null) {
            System.out.println("Användardata är inte korrekt inladdad.");
            return;
        }
        boolean showError = true;
        for (User u : userList) {
            if (u == null || u.getId() == null) continue; // Hoppa över om användaren är korrupt
            for (SuspendedUser su : suspendedList) {
                if (su == null || su.getUserId() == null) continue; // Hoppa över om den suspenderade är korrupt
                
                // Genom att starta med u.getId() (som vi vet inte är null) undviker vi NullPointerException
                if (u.getId().equals(su.getUserId())) {
                    System.out.println(u.getInfo() + ", Anledning: " + (su.getReason() != null ? su.getReason() : "Ingen anledning angiven"));
                    showError = false;
                    break;
                }
            }
        }
        if (showError) {
            System.out.println("Hittade ingen Suspenderad kund");
        }
    }

    // get-anrop metod för att hämta EN specifik kund från servern via ID och printa den inklusive json
    public void fetchSingleUserFromServer(String userId) {
        try {
            // Anropar t.ex. http://localhost:3000/users/123
            HttpResponse<String> response = Unirest.get(baseURL + "users/" + userId).asString();            
            //System.out.println("json respons: " + response); // 404 
            
            if (response.getStatus() == 200) { 
                String json = response.getBody(); 
                System.out.println("Fick json svar från servern: " + json); 
                User u = gson.fromJson(json , User.class); // json till klassinstans 
                System.out.println("Hittade användare på servern: " + u.getInfo());
            } else {
                System.out.println("Kunde inte hitta användaren på servern (Status: " + response.getStatus() + ")."); // 404 not found
            }
        } catch (Exception e) {
            System.out.println("Fel vid hämtning av enskild användare från servern.");
        }
    }

    // funktion så att man hittar användare så att den kan tas bort
    public User getCustomerByName(String name) {
        for (User u : userList) {
            if (u.getName().equalsIgnoreCase(name)) {
                return u; // risk för dubletter första tas bort iaf
            }
        }
        System.out.println("Ingen användare matchade sökningen.");
        return null;
    }

    // funktion så att man hittar användare via epost
    public User getCustomerByEmail(String email) {
        for (User u : userList) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u; // risk för dubletter men retunerar bara första
            }
        }
        System.out.println("Ingen användare hade matchande e-mail.");
        return null;
    }

    // för att kunna ta bort avstängningen behöver jag hämta suspendeduser
    public SuspendedUser getSuspendedCustomer(String userId) {
        for (SuspendedUser su : suspendedList) {
            // Kontrollerar att objektet existerar och jämför säkert
            if (su != null && userId != null && userId.equals(su.getUserId()))
                return su;
        }
        System.out.println("Ingen suspenderad användare matchade sökningen.");
        return null;
    }

}