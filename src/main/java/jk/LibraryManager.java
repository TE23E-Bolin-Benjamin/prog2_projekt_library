package jk;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import java.lang.reflect.Type;
import java.util.ArrayList;

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

    public void findBookOrMagazine(String title) {
        for (Media m : mediaList) {
            if (m.getTitle().toLowerCase().contains(title.toLowerCase())) System.out.println(m.getInfo());
        }
    }

    public void findCustomerByEmail(String email) {
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
        System.out.println("Hittade ingen kund med den e-postadressen.");
    }
}