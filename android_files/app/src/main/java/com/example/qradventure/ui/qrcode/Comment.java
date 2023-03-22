package com.example.qradventure.ui.qrcode;

public class Comment {
    private String author;
    private String content;

    public Comment(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getContents() {
        return this.content;
    }
}
