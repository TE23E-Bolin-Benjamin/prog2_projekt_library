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
            userList = gson.fromJson(uBody, new TypeToken<ArrayList<User>>() {
            }.getType());

            String sBody = Unirest.get(baseURL + "suspendedUsers").asString().getBody();
            suspendedList = gson.fromJson(sBody, new TypeToken<ArrayList<SuspendedUser>>() {
            }.getType());
        } catch (Exception e) {
            System.out.println("Kunde inte hämta användardata.");
        }
    }

    // --- C-krav: Kommunikation via POST ---
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

    public void addUserToSuspendedViaUsername(String name, String uuid, String reason) {    
        User u = getCustomerByName(name);            
        addSuspendedUserToServer(u, new SuspendedUser(uuid, u.getId(), reason));
   }


    // metod som anropas från addUserToServer eller addUserToSuspendedViaUsername
    public void addSuspendedUserToServer(User u, SuspendedUser su) {

        try {
            HttpResponse<String> response = Unirest.post(baseURL + "suspendedUsers")
                    .header("Content-Type", "application/json").body(gson.toJson(su)).asString();
1            if (response.getStatus() == 201) {
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
            if (status == 200 || status == 204) {
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
            if (su.getUserId().equals(userId)) {
                target = su;
                break;
            }
        }
        if (target != null) {
            int status = Unirest.delete(baseURL + "suspendedUsers" + target.getId()).asString().getStatus();
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
                int status = Unirest.delete(baseURL + "suspendedUsers" + target.getId()).asString().getStatus();
                if (status == 200 || status == 204) {
                    suspendedList.remove(target);
                    System.out.println("Feedback: '" + name + "' är nu aktiverad.");
                }
            } else {
                System.out.println("Feedback: Kunde inte hitta suspenderad användare.");
            }
        } else {
            System.out.println("Feedback: removeSuspendedUserViaUsername kunde inte hitta användare.");
        }

    }

    public void removeUser(String user) {
        User target = getCustomerByName(user);
        if (target != null) {
            int status = Unirest.delete(baseURL + "users" + target.getId()).asString().getStatus();
            if (status == 200 || status == 204) {
                userList.remove(target);
                System.out.println("Feedback: '" + user + "' är nu borttagen.");
            }
        } else {
            System.out.println("Feedback: Kunde inte hitta användare.");
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

    public void printUsersSorted() {
        if (userList.isEmpty()) {
            System.out.println("Inga kunder inladdade.");
        } else {
            Collections.sort(userList); // Sorterar kunder automatiskt via Comparable på namn
            for (User u : userList)
                System.out.println(u.getInfo());
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

    public void findAvailableCustomers() {
        boolean showError = true;
        for (User u : userList) {
            boolean suspended = false;
            for (SuspendedUser su : suspendedList) {
                if (su.getUserId().equals(u.getId())) {
                    suspended = true;
                    showError = false;
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

    public void findSuspendedCustomers() {
        boolean showError = true;
        for (User u : userList) {
            for (SuspendedUser su : suspendedList) {
                if (su.getUserId().equals(u.getId())) {
                    System.out.println(u.getInfo() + ", Anledning " + su.getReason());
                    showError = false;
                    break;
                }
            }
        }
        if (showError) {
            System.out.println("Hittade ingen Suspenderad kund");
            return; // Behöver inte loopa mera
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

    // för att kunna ta bort avstängningen behöver jag hämta suspendeduser
    public SuspendedUser getSuspendedCustomer(String userId) {
        for (SuspendedUser su : suspendedList) {
            if (su.getUserId().equals(userId))
                return su;
        }
        System.out.println("Ingen supsenderad användare matchade sökningen.");
        return null;
    }

}