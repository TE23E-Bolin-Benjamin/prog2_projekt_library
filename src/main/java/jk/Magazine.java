package jk;

public class Magazine extends Media{

    private int issueNumber;
    private String category;
    private int publishedYear;

    public Magazine(String id, String title, boolean isAvailable, int issueNumber, String category, int publishedYear){
        super(id, title, isAvailable);
        this.issueNumber = issueNumber;
        this.category = category;
        this.publishedYear = publishedYear;
    }

    public int getIssueNumber(){
        return issueNumber;
    }

    public String getCategory(){
        return category;
    }

    public int getPublishedYear(){
        return publishedYear;
    }

    @Override
    public String getInfo(){
        return "Magazine - ID" + id + ", title: " + title + ", isAvailbe: " + isAvailable + ", issueNumber: " + issueNumber + ", category: " + category + ", publishedYear: " + publishedYear;
    }

}
