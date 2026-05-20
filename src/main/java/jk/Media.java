package jk;

/**
 * Benjamin Bolin
 * Media fungerar som en bas för böcker och tidningar. 
 */
public abstract class Media implements Comparable<Media> {
    protected String id;
    protected String title;
    protected boolean isAvailable;

    public Media(String id, String title, boolean isAvailable){
        this.id = id;
        this.title = title;
        this.isAvailable = isAvailable;
    }

    public String getId(){ 
        return id; }

    public String getTitle(){ 
        return title; }

    public boolean getIsAvailable(){ 
        return isAvailable; }

    public void setisAvailable(boolean isAvailable){ 
        this.isAvailable = isAvailable; }

    // Algoritm som jämför och möjliggör sortering alfabetiskt efter titel, retunrar 0 om objekten är lika
    @Override
    public int compareTo(Media other) {
        return this.title.compareToIgnoreCase(other.getTitle());
    }

    public String getInfo(){
        return "ID: " + id + ", Titel: " + title + ", Tillgänglighet: " + isAvailable;
    }
}