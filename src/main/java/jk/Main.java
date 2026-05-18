package jk;

/**
 * Benjamin Bolin
 */
public class Main {
    public static void main(String[] args) {
        
        System.out.println("--- Testar att skapa objekt ---");

        // Skapa bok (id, title, isAvailable, author, genre, pages)
        Book minBok = new Book("1", "Ringo Can Read", true, "Daniel Harper", "Drama", 250);

        // Skapa tidning (id, title, isAvailable, issueNumber, category, publishedYear)
        Magazine minTidning = new Magazine("2", "Tech World", true, 12, "Technology", 2025);

        // Anropa objekten 
        System.out.println("\n=== om boken ===");
        System.out.println(minBok.getInfo());

        System.out.println("\n=== om tidningen ===");
        System.out.println(minTidning.getInfo());
    }
}