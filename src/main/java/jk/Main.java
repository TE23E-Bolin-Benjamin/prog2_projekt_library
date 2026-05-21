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
                    4. Ta bort (Undermeny)
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
                case 4 -> deletehMenu();
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
                5. Skriv ut alla suspenderade kunder
                """);
        System.out.print("Val: ");
        try {
            int val = Integer.parseInt(scanner.nextLine());
            if (val == 1)
                manager.fetchBooks();
            else if (val == 2)
                manager.fetchMagazines();
            else if (val == 3)
                manager.printMediaSorted();
            else if (val == 4)
                manager.printUsersSorted();
            else if (val == 5) {
                System.out.println("Kunder som inte får låna är: ");
                manager.findSuspendedCustomers();
            }

        } catch (Exception e) {
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
                4. Ny suspended användare
                5. Suspendera en befintlig användare
                6. Aktivera suspenderad användare
                """);
        System.out.print("Val (1-6): ");
        try {
            int val = Integer.parseInt(scanner.nextLine());
            switch (val) {
                case 1 -> {
                    System.out.print("Ange titel: ");
                    String t = scanner.nextLine();
                    System.out.print("Ange författare: ");
                    String a = scanner.nextLine();
                    System.out.print("Ange genre: ");
                    String g = scanner.nextLine();
                    System.out.print("Ange antal sidor: ");
                    int p = Integer.parseInt(scanner.nextLine());
                    manager.addMediaToServer(new Book(UUID.randomUUID().toString(), t, true, a, g, p), "books");
                }
                case 2 -> {
                    System.out.print("Ange titel: ");
                    String t = scanner.nextLine();
                    System.out.print("Ange kategori: ");
                    String c = scanner.nextLine();
                    System.out.print("Ange utgåvonummer: ");
                    int num = Integer.parseInt(scanner.nextLine());
                    System.out.print("Ange utgivningsår: ");
                    int y = Integer.parseInt(scanner.nextLine());
                    manager.addMediaToServer(new Magazine(UUID.randomUUID().toString(), t, true, num, c, y),
                            "magazines");
                }
                case 3 -> {
                    System.out.print("Ange kundens namn: ");
                    String n = scanner.nextLine();
                    System.out.print("Ange e-postadress: ");
                    String em = scanner.nextLine();
                    manager.addUserToServer(new User(UUID.randomUUID().toString(), n, em), null);
                }
                case 4 -> {
                    System.out.print("Ange kundens namn: ");
                    String n = scanner.nextLine();
                    System.out.print("Ange e-postadress: ");
                    String em = scanner.nextLine();
                    System.out.print("Ange anledning: ");
                    String reason = scanner.nextLine();
                    User u = new User(UUID.randomUUID().toString(), n, em);
                    manager.addUserToServer(u, new SuspendedUser(UUID.randomUUID().toString(), u.getId(), reason));
                }
                case 5 -> {
                    System.out.print("Ange kundens namn: ");
                    String n = scanner.nextLine();
                    System.out.print("Ange anledning för att suspendera: ");
                    String reason = scanner.nextLine();                            
                    manager.addUserToSuspendedViaUsername(n, UUID.randomUUID().toString(), reason);
                }
                case 6 -> {
                    System.out.print("Ange kundens namn: ");
                    String n = scanner.nextLine();                                        
                    manager.removeSuspendedUserViaUsername(n);
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
                2. Hitta kund med hjälp av namn eller e-postadress
                3. Visa vilka som får låna
                """);
        System.out.print("Val (1-3): ");
        try {
            int val = Integer.parseInt(scanner.nextLine());
            if (val == 1) {
                System.out.print("Mata in sökord på titel: ");
                manager.findBookOrMagazine(scanner.nextLine());
            } else if (val == 2) {
                System.out.print("Mata in kundens namn eller e-post: ");
                manager.findCustomerByNameAndEmail(scanner.nextLine());
            } else if (val == 3) {
                System.out.print("Kunder som får låna är: ");
                manager.findAvailableCustomers();
            }
        } catch (Exception e) {
            System.out.println("Felaktigt val.");
        }
    }

    private static void deletehMenu() {
        System.out.println("""
                \n--- UNDERMENY:  ---
                1. Ta bort Media
                2. Aktivera avstängd användare via namn
                3. Aktivera avstängd användare via id
                4. Ta bort användare via namn
                """);
        System.out.print("Val (1-3): ");
        try {
            int val = Integer.parseInt(scanner.nextLine());
            if (val == 1) {
                System.out.print("Ange exakt titel på den media du vill ta bort från servern: ");
                manager.removeMediaFromServer(scanner.nextLine());
            } else if (val == 2) {
                System.out.print("Ange kundens namn som ska aktiveras: ");
                String n = scanner.nextLine();
                manager.removeSuspendedUserViaUsername(n);
            } else if (val == 3) {
                System.out.print("Ange kundens id som ska aktiveras: ");
                String n = scanner.nextLine();
                manager.removeSuspended(n);
            } else if (val == 4) {
                System.out.print("Ange kundens namn som ska tas bort: ");
                String n = scanner.nextLine();
                manager.removeUser(n);
            }
        } catch (Exception e) {
            System.out.println("Felaktigt val.");
        }
    }
}