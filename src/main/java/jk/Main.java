package jk;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

import java.lang.reflect.Type;
import java.util.ArrayList; // krav och här stoppas media objekt oavsett barnklass
import java.util.Scanner;
import java.util.UUID;

/**
 * Benjamin Bolin
 * Libsys: Hanterar menyerna, gör REST-API GET-anrop på port 3000
 */
public class Main {
    public static void main(String[] args) {
        String baseURL = "http://localhost:3000/"; // Fake serveradress till rest-servern
        Gson gson = new Gson(); 

        // En generisk ArrayList för att lagra all media lokalt i programmet 
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
                    4. Lägg till bok i listan
                    5. Lägg till tidning i listan
                    6. Avsluta LibSys
                    """);
            System.out.print("Välj ett alternativ (1-6) och tryck på Enter: ");
            String input = scanner.nextLine();
            int choice = 0;

            // Try-catch för att förhindra krasch vid felaktig menyinmatning 
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Nej, det där är inte en siffra. Försök igen!"); 
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("Hämtar alla böcker från servern...");
                    try {
                        // gör ett HTTP GET-anrop mot /books mot slutet av urlen 
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
                    break;

                case 2:
                    System.out.println("Hämtar alla tidningar från servern...");
                    try {
                        // HTTP GET-anrop mot /magazines  
                        HttpResponse<String> response = Unirest.get(baseURL + "magazines").asString();
                        
                        if (response.getStatus() == 200) {
                            Type magazineListType = new TypeToken<ArrayList<Magazine>>(){}.getType();
                            ArrayList<Magazine> serverMagazines = gson.fromJson(response.getBody(), magazineListType);
                            
                            samlingar.addAll(serverMagazines); 
                            System.out.println("Lyckades hämta " + serverMagazines.size() + " tidningar!");  
                        } else 
                            {
                   
                                System.out.println("Servern svarade med felkod: " + response.getStatus());
                        }
                    } catch (UnirestException e) 
                    {
                        System.out.println("Kunde inte ansluta till servern.");
                    }
                    break;

                case 3:
                    if (samlingar.isEmpty()) {
                        System.out.println("Samlingen är tom. Hämta data från servern eller lägg till något först via menyn.");
                    } else 
                        {
                        System.out.println("\n=== Hämtat media i samlingen ===");
                        for (Media m : samlingar) {
                            System.out.println(m.getInfo()); // Använder getInfo() oavsätt media 
                        }
                    }
                    break;

                case 4:
                    System.out.println("--- Lägg till en bok i listan ---");
                    System.out.print("Ange titel: "); String bTitle = scanner.nextLine();
                    System.out.print("Ange författare: "); String bAuthor = scanner.nextLine();
                    System.out.print("Ange genre: "); String bGenre = scanner.nextLine();
                    
                    int bPages = 0;
                    try {
                        System.out.print("Ange antal sidor: ");
                        bPages = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) 
                    {
                        System.out.println("Felaktigt format för sidantal. Avbryter.");
                        break;
                    }

                    // Skapar ett bokobjekt och lägger till det i samlingen (id via randomeUUID)
                    Book newBook = new Book(getUUID(), bTitle, true, bAuthor, bGenre, bPages);
                    samlingar.add(newBook);
                    System.out.println("Boken '" + bTitle + "' lades till i listan.");
                    break;

                case 5:
                    System.out.println("--- Lägg till en tidning i listan ---");
                    System.out.print("Ange titel: "); String mTitle = scanner.nextLine();
                    System.out.print("Ange kategori: "); String mCategory = scanner.nextLine();
                    
                    int mIssue = 0;
                    int mYear = 0;
                    try {
                        System.out.print("Ange utgåvonummer: "); mIssue = Integer.parseInt(scanner.nextLine());
                        System.out.print("Ange utgivningsår: "); mYear = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Felaktig sifferinmatning för nummer/år. Avbryter.");
                        break;
                    }

                    // Skapar ett tidningsobjekt och lägger till det i samlingen
                    Magazine newMagazine = new Magazine(getUUID(), mTitle, true, mIssue, mCategory, mYear);
                    samlingar.add(newMagazine);
                    System.out.println("Tidningen '" + mTitle + "' lades till i listan.");
                    
                    break;

                case 6:
                    System.out.println("Avslutar programmet. Hejdå!");
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

    private static String getUUID() {
        return UUID.randomUUID().toString(); // krav att ha funktion
    }
}