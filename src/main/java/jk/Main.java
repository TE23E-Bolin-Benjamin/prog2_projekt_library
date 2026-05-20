package jk;

import java.util.Scanner;
import java.util.UUID;


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
                    1. Hämta & Visa (Undermeny)
                    2. Skapa & Lägg till (Undermeny)
                    3. Sökfunktioner (Undermeny)
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
                case 2 -> createMenu(); 
                case 3 -> searchMenu(); 
                case 4 -> {
                    System.out.print("Ange exakt titel på den media du vill ta bort från servern: ");
                    manager.removeMediaFromServer(scanner.nextLine());
                }
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
                \n--- UNDERMENY: HÄMTA & VISA ---
                1. Hämta böcker från Servern
                2. Hämta tidningar från Servern
                3. Skriv ut all hämtad media (Sorterad)
                4. Skriv ut alla kunder (Sorterat på Namn)
                """);
        System.out.print("Val: ");
        try {
            int val = Integer.parseInt(scanner.nextLine());
            if (val == 1) manager.fetchBooks();
            else if (val == 2) manager.fetchMagazines();
            else if (val == 3) manager.printMediaSorted();
            else if (val == 4) manager.printUsersSorted();
   
        } catch (Exception e) 
        { 
            System.out.println("Felaktigt format."); 

        }
    }

    // Undermeny för att skapa och lägga till på server via POST
    private static void createMenu() {
        System.out.println("""
                \n--- UNDERMENY: SKAPA NYTT ---
                1. Ny Bok
                2. Ny Tidning
                3. Ny Användare
                """);
        System.out.print("Val (1-3): ");
        try {
            int val = Integer.parseInt(scanner.nextLine());
            switch (val) {
                case 1 -> {
                    System.out.print("Ange titel: "); String t = scanner.nextLine();
                    System.out.print("Ange författare: "); String a = scanner.nextLine();
                    System.out.print("Ange genre: "); String g = scanner.nextLine();
                    System.out.print("Ange antal sidor: "); int p = Integer.parseInt(scanner.nextLine());
                    manager.addMediaToServer(new Book(UUID.randomUUID().toString(), t, true, a, g, p), "books");
                }
                case 2 -> {
                    System.out.print("Ange titel: "); String t = scanner.nextLine();
                    System.out.print("Ange kategori: "); String c = scanner.nextLine();
                    System.out.print("Ange utgåvonummer: "); int num = Integer.parseInt(scanner.nextLine());
                    System.out.print("Ange utgivningsår: "); int y = Integer.parseInt(scanner.nextLine());
                    manager.addMediaToServer(new Magazine(UUID.randomUUID().toString(), t, true, num, c, y), "magazines");
                }
                case 3 -> {
                    System.out.print("Ange kundens namn: "); String n = scanner.nextLine();
                    System.out.print("Ange e-postadress: "); String em = scanner.nextLine();
                    manager.addUserToServer(new User(UUID.randomUUID().toString(), n, em));
                }
                default -> System.out.println("Ogiltigt val i undermenyn.");
            }
        } catch (Exception e) {
            System.out.println("Felaktig inmatning. Avbryter.");
        }
    }

    // Undermeny för sökfunktioner och kontroll av lånestatus
    private static void searchMenu() {
        System.out.println("""
                \n--- UNDERMENY: SÖKFUNKTIONER ---
                1. Sök efter bok/tidning på titel
                2. Hitta kund med hjälp av e-postadress
                3. Visa vilka som får låna
                4. Visas suspenderade
                """);
        System.out.print("Val (1-4): ");
        try {
            int val = Integer.parseInt(scanner.nextLine());
            if (val == 1) {
                System.out.print("Mata in sökord på titel: ");
                manager.findBookOrMagazine(scanner.nextLine());
            } else if (val == 2) {
                System.out.print("Mata in kundens e-post: ");
                manager.findCustomerByEmail(scanner.nextLine());
            }else if (val == 3) {
                System.out.print("Kunder som får låna är: ");
                manager.findAvailableCustomers();
            }else if (val == 4) {
                System.out.print("Kunder som inte får låna är: ");
                manager.findSuspendedCustomers();
            }

        } catch (Exception e) {
            System.out.println("Felaktigt val.");
        }
    }
}