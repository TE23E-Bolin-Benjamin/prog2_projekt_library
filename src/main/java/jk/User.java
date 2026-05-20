package jk;

/**
 * Benjamin Bolin
 * Representerar en användare i biblioteket
 * Implementerar Comparable för att tillåta automatisk sortering på namn 
 */
public class User implements Comparable<User> {
    private String id;
    private String name;
    private String email;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
         return id; }

    public String getName() { 
        return name; }

    public String getEmail() { 
        return email; }

    public String getInfo() {
        return "Kund: " + id + ", Namn: " + name + ", E-post: " + email;
    }

    //Denna metod talar om för Collections.sort() hur kunder ska sorteras (på namn)
    @Override
    public int compareTo(User other) {
        return this.name.compareToIgnoreCase(other.getName());
    }
}