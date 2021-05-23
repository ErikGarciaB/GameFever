package net.iessochoa.erikgarciabelen.gamefever.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {
    private String body;
    private String username;

    @ServerTimestamp
    private Date time;

    public Message(){}

    public Message(String body, String username) {
        this.body = body;
        this.username = username;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
