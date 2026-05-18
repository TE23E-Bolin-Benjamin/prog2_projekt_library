package jk;

import java.util.ArrayList;

/**
 * Benjamin Bolin
 * Enkel Main-klass som skapar objekt, sparar dem i en 
 * ArrayList (samlingar) och skriver ut dem på skärmen.
 */
public class Main {
    public static void main(String[] args) {
        
        // Skapa en generisk ArrayList som sparar objekt av typen Media 
        ArrayList<Media> samlingar = new ArrayList<>();

        System.out.println("--- Skapar objekt och lägger till i listan ---");

        //Skapa en bok och en tidning
        Book minBok = new Book("1", "Ringo Can Read", true, "Daniel Harper", "Drama", 250);
        Magazine minTidning = new Magazine("1", "Tech World", true, 12, "Technology", 2025);

        Magazine minTidning2 = new Magazine("3", "Aftonbladet", false, 12, "News", 2025);

        //Stoppa in båda objekten i ArrayList "samlingar" 
        samlingar.add(minBok);
        samlingar.add(minTidning);

        samlingar.add(minTidning2);

        System.out.println("Feedback: Objekten har sparats lokalt i listan.");

        //Loopa igenom listan och printa ut informationen på skärmen 
        System.out.println("\n=== Printar från listan 'samlingar' om available ===");
        for (Media m : samlingar) {
            if (m.getIsAvailable()) {
                System.out.println(m.getInfo()); // Hämtar getInfo() dynamiskt beroende på objekttyp        
            }
        
        }
    }
}