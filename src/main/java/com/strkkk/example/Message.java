package com.strkkk.example;

public class Message {
    private String value;
    private long timestamp;

    public Message(String value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
