package com.example.detector.model;

import java.io.Serializable;

public class MainMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String result;
    private String sender;
    private String message;
    private String time;

    public MainMessage(String result, String sender, String message, String time) {
        this.result = result;
        this.sender = sender;
        this.message = message;
        this.time = time;
    }

    public String getResult() { return result; }

    public void setResult(String result) { this.result = result; }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) { this.message = message; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

}
