package jk;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Benjamin Bolin
 * LibraryManager hanterar alla REST-API (GET, POST, DELETE) samt sök- och sorteringsalgoritmer.
 */
public class LibraryManager {
    // Aggregat-listor enligt mitt klassdiagram
    private ArrayList<Media> mediaList = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<SuspendedUser> suspendedList = new ArrayList<>();

    private final String baseURL = "http://localhost:3000/";
    private final Gson gson = new Gson();

    // --- Hämtningar via GET ---
    public void fetchBooks() {
        try {
            HttpResponse<String> response = Unirest.get(baseURL + "books").asString();
            if (response.getStatus() == 200) {
                Type listType = new TypeToken<ArrayList<Book>>(){}.getType();
                ArrayList<Book> serverBooks = gson.fromJson(response.getBody(), listType);
                mediaList.addAll(serverBooks);
                System.out.println("Lyckades hämta " + serverBooks.size() + " böcker!");
            }
        } catch (Exception e) { System.out.println("Kunde inte hämta böcker."); }
    }

    public void fetchMagazines() {
        try {
            HttpResponse<String> response = Unirest.get(baseURL + "magazines").asString();
            if (response.getStatus() == 200) {
                Type listType = new TypeToken<ArrayList<Magazine>>(){}.getType();
                ArrayList<Magazine> serverMags = gson.fromJson(response.getBody(), listType);
                mediaList.addAll(serverMags);
                System.out.println("Lyckades hämta " + serverMags.size() + " tidningar!");
            }
        } catch (Exception e) { System.out.println("Kunde inte hämta tidningar."); }
    }
    // både användare och suspenderade användare hämtas via denna method
    public void fetchUsersAndSuspended() {
        try {
            String uBody = Unirest.get(baseURL + "users").asString().getBody();
            userList = gson.fromJson(uBody, new TypeToken<ArrayList<User>>(){}.getType());

            String sBody = Unirest.get(baseURL + "suspendedUsers").asString().getBody();
            suspendedList = gson.fromJson(sBody, new TypeToken<ArrayList<SuspendedUser>>(){}.getType());
        } catch (Exception e) { System.out.println("Kunde inte hämta användardata."); }
    }

    // --- C-krav: Kommunikation via POST ---
    public void addMediaToServer(Media m, String endpoint) {
        try {
            HttpResponse<String> response = Unirest.post(baseURL + endpoint)
                    .header("Content-Type", "application/json").body(gson.toJson(m)).asString();
            if (response.getStatus() == 201) {
                mediaList.add(m);
                System.out.println("Feedback: Resursen '" + m.getTitle() + "' har sparats på servern.");
            }
        } catch (Exception e) { System.out.println("Fel vid POST till servern."); }
    }

    public void addUserToServer(User u) {
        try {
            HttpResponse<String> response = Unirest.post(baseURL + "users")
                    .header("Content-Type", "application/json").body(gson.toJson(u)).asString();
            if (response.getStatus() == 201) {
                userList.add(u);
                System.out.println("Feedback: Användaren '" + u.getName() + "' har sparats på servern.");
            }
        } catch (Exception e) { System.out.println("Fel vid skapande av användare på servern."); }
    }

    // Kommunikation med servern via DELETE 
    public void removeMediaFromServer(String title) {
        Media target = null;
        for (Media m : mediaList) {
            if (m.getTitle().equalsIgnoreCase(title)) { target = m; break; }
        }
        if (target != null) {
            String endpoint = (target instanceof Book) ? "books/" : "magazines/";
            int status = Unirest.delete(baseURL + endpoint + target.getId()).asString().getStatus();
            if (status == 200 || status == 204) {
                mediaList.remove(target);
                System.out.println("Feedback: Raderade '" + title + "' från servern.");
            }
        } else { System.out.println("Feedback: Kunde inte hitta media."); }
    }

    // Algoritmer för sökning och sortering
    public void printMediaSorted() {
        if (mediaList.isEmpty()) {
            System.out.println("Samlingen är tom lokalt. Hämta data från servern först.");
        } else {
            Collections.sort(mediaList); // Sorterar samlingen automatiskt via Comparable
            for (Media m : mediaList) System.out.println(m.getInfo());
        }
    }

    public void printUsersSorted() {
        if (userList.isEmpty()) {
            System.out.println("Inga kunder inladdade.");
        } else {
            Collections.sort(userList); // Sorterar kunder automatiskt via Comparable på namn
            for (User u : userList) System.out.println(u.getInfo());
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
        if (!hittad) System.out.println("Ingen media matchade sökningen.");
    }

    public void findCustomerByEmail(String email) {
        boolean hittad = true;
        for (User u : userList) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                System.out.println(u.getInfo());
                // Kontrollerar direkt om kunden är spärrad eller inte (C-krav)
                boolean suspended = true;
                for (SuspendedUser su : suspendedList) {
                    if (su.getUserId().equals(u.getId())) { suspended = false; break; }
                }
                System.out.println("Lånestatus: " + (suspended ? "FÅR LÅNA" : "SPÄRRAD (Får ej låna!)"));
                return;
            }
        }
         if (!hittad) System.out.println("Ingen matchade sökningen.");
    }
        
    public void findAvailableCustomers() {
        for (User u : userList) {
                boolean suspended = false;
                for (SuspendedUser su : suspendedList) {
                    if (su.getUserId().equals(u.getId())) { suspended = true; break; }
                }
                if (!suspended) {System.out.println(u.getInfo());}
            }
        
        System.out.println("Hittade ingen kund med den e-postadressen.");
    }

        public void findSuspendedCustomers() {
        for (User u : userList) {
                boolean suspended = false;
                for (SuspendedUser su : suspendedList) {
                    if (su.getUserId().equals(u.getId())) { suspended = true; break; }
                }
                if (suspended) {System.out.println(u.getInfo());}
            }
        
        System.out.println("Hittade ingen kund med den e-postadressen.");
    }


}