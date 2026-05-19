package jk;

import java.util.Scanner;


/**
 * Benjamin Bolin
 * Libsys startas via Main klassen som visar menyer i flera nivåer
 */
public class Main {
    private static LibraryManager manager = new LibraryManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean run = true;
        manager.fetchUsersAndSuspended(); // Hämtar kunddata vid uppstart

        System.out.println("Välkommen till LibSys!");  

        while (run) {
            System.out.println("""
                    
                    === HUVUDMENY ===
                    1. Hämta & Visa
                    2. Skapa & Lägg till (Undermeny)
                    3. Sökfunktioner
                    4. Ta bort media från servern
                    5. Avsluta LibSys
                    """);
            System.out.print("Välj ett alternativ (1-5) och tryck på Enter: ");
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
                case 1 -> fetchMenu(); 
                case 5 -> {
                    System.out.println("Avslutar programmet. Hejdå!");
                    run = false;
                    scanner.close();
                }
                default -> System.out.println("Instruktion: Ogiltigt val. Vänligen ange en siffra mellan 1 och 5.");
            }
        }
    }

    
    private static void fetchMenu() {
        System.out.println("""
                \n--- UNDERMENY:  ---
                1. Hämta böcker från Servern
                2. Hämta tidningar från Servern
                """);
        System.out.print("Val: ");
        try {
            int val = Integer.parseInt(scanner.nextLine());
            if (val == 1) manager.fetchBooks();
            else if (val == 2) manager.fetchMagazines();
   
        } catch (Exception e) 
        { 
            System.out.println("Felaktigt format."); 

        }
    }


}