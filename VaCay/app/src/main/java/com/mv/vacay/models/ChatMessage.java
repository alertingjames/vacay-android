package com.mv.vacay.models;

/**
 * Created by a on 4/25/2017.
 */

public class ChatMessage {

    private String messageText;
    private String messageImage;
    private long messageTime;

    public ChatMessage(String messageText, String messageImage, long messageTime) {
        this.messageText = messageText;
        this.messageImage = messageImage;
        this.messageTime = messageTime;
    }

    public ChatMessage(String messageText, long messageTime) {
        this.messageText = messageText;
        this.messageTime = messageTime;
    }

    public ChatMessage() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageImage() {
        return messageImage;
    }

    public void setMessageImage(String messageImage) {
        this.messageImage = messageImage;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
