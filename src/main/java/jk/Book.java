/**
 * Benjamin Bolin
 * Barnklass till Media för hantering av böcker
 */

package jk;

public class Book extends Media{

    private String author;
    private String genre;
    private int pages;

    public Book(String id, String title, boolean isAvailable, String author, String genre, int pages){
        super(id, title, isAvailable);
        this.author = author;
        this.genre = genre;
        this.pages = pages;
    }

    public String getAuthor(){
        return author;
    }

    public String getGenre(){
        return genre;
    }

    public int getPages(){
        return pages;
    }

    @Override
    public String getInfo(){
        return "Book - ID" + id + ", title: " + title + ", isAvailbe: " + isAvailable + ", author: " + author + ", genre: " + genre + ", pages: " + pages;
    }


}
