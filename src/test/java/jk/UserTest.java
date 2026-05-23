package jk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    // TEST 1: Kontrollera att getInfo() formaterar strängen helt korrekt
    @Test
    public void testGetInfoFormatting() {
        // Arrange (Förbered testdata)
        User user = new User("123", "Anna Andersson", "anna@email.com");

        // Act (Anropa metoden som ska testas)
        String resultat = user.getInfo();

        // Assert (Kontrollera att resultatet blev det förväntade)
        String forvantadStrang = "Kund: 123, Namn: Anna Andersson, E-post: anna@email.com";
        
        assertEquals(forvantadStrang, resultat, "Metoden getInfo() returnerade felaktigt format!");
    }

    // TEST 2: Kontrollera att din sorteringsalgoritm (compareTo) sorterar alfabetiskt
    @Test
    public void testUserSortingAlphabetically() {
        // Arrange (Skapa två användare där "Anna" logiskt sett ska komma före "Benny")
        User userA = new User("1", "Anna", "anna@email.com");
        User userB = new User("2", "Benny", "benny@email.com");

        // Act (Jämför användarna med din compareTo-metod)
        int jamforAB = userA.compareTo(userB); // Ska bli negativt eftersom A kommer före B
        int jamforBA = userB.compareTo(userA); // Ska bli positivt eftersom B kommer efter A

        // Assert (Verifiera att talen har rätt tecken)
        assertTrue(jamforAB < 0, "Anna borde sorteras före Benny (resultatet ska vara negativt).");
        assertTrue(jamforBA > 0, "Benny borde sorteras efter Anna (resultatet ska vara positivt).");
    }
}