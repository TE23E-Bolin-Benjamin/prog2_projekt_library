package jk;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Scanner;


/**
 * Benjamin Bolin
 * Libsys: Hanterar menyn, gör REST GET-anrop 
 */
public class Main {
    public static void main(String[] args) {
        String baseURL = "http://localhost:3000/"; // Fake serveradress 
        Gson gson = new Gson(); 

        // En generisk ArrayList för att lagra all media lokalt i programmet dvs bara i listan
        ArrayList<Media> samlingar = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        boolean run = true;

        System.out.println("Välkommen till LibSys!"); 

        while (run) {
            System.out.println("""
                    
                    === MENY ===
                    1. Hämta böcker från Servern
                    2. Hämta tidningar från Servern
                    3. Skriva ut hämtad media på skärmen
                    4. Lägg till bok lokalt
                    5. Lägg till tidning lokalt
                    6. Avsluta LibSys
                    """);
            System.out.print("Välj ett alternativ (1-6) och tryck på Enter: ");
            String input = scanner.nextLine();
            int choice = 0;
            choice = Integer.parseInt(input);
            switch (choice) {
                case 1:
                    System.out.println("Hämtar alla böcker från servern...");
                    try {
                        // gör ett GET-anrop mot /books mot slutet av urlen 
                        HttpResponse<String> response = Unirest.get(baseURL + "books").asString();
                        
                        if (response.getStatus() == 200) {
                            Type bookListType = new TypeToken<ArrayList<Book>>(){}.getType();
                            ArrayList<Book> serverBooks = gson.fromJson(response.getBody(), bookListType);
                            
                            samlingar.addAll(serverBooks); // Sparar ner i ArrayList 
                            System.out.println("Lyckades hämta " + serverBooks.size() + " böcker!"); 
                        } else {
                            
                            System.out.println("Servern svarade med felkod: " + response.getStatus());
                        }
                    } catch (UnirestException e) {
                        System.out.println("Kunde inte ansluta till servern. Kontrollera att json-server körs.");
                    }
                    break; // för att komma till menyn igen
                case 6:
                    System.out.println("Avslutar LibSys-programmet.");
                    run = false;
                    scanner.close(); //förhindrar minnesläckage

                    break;


                default:
                    System.out.println("Instruktion: Ogiltigt val. Vänligen ange en siffra mellan 1 och 6."); 
                    
                    break;
            }
        }

        Unirest.shutDown(); 
    }
}