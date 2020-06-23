package com.example.whizzz.services.model;

public class Chats {
    private String receiverId;
    private String senderId;
    private String message;
    private String timestamp;
    private boolean seen;

    public Chats(String receiverId, String senderId, String message, String timestamp, boolean seen) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public Chats() {

    }


    public boolean getSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen= seen;
    }


    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
