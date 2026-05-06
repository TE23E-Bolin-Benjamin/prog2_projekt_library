package jk;

public abstract class Media {
    protected String id;
    protected String title;
    protected boolean isAvailable;

    public Media(String id, String title, boolean isAvailable){
        this.id = id;
        this.title = title;
        this.isAvailable = isAvailable;
    }

    public String getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public boolean getIsAvailable(){
        return isAvailable;
    }

    public void setisAvailable(boolean isAvailable){
        this.isAvailable = isAvailable;
    }

    public abstract String getInfo();


}
