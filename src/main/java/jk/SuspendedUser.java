package jk;

/**
 * Benjamin Bolin
 * Representerar en avstängd kund i systemet.
 */
public class SuspendedUser {
    private String id;
    private String userId;
    private String reason;

    public SuspendedUser(String id, String userId, String reason) {
        this.id = id;
        this.userId = userId;
        this.reason = reason;
    }

    public String getId() { 
        return id; }

    public String getUserId() { 
        return userId; }

    public String getReason() { 
        return reason; }
}