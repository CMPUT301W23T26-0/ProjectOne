package com.example.qradventure.ui.qrcode;

public class Comment {
    private String author;
    private String content;

    /**
     * Constructing a comment
     * @param author, Author of the comment
     * @param content, The comment's contents
     */
    public Comment(String author, String content) {
        this.author = author;
        this.content = content;
    }

    /**
     * Getter for author
     * @return
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Getter for contents
     * @return
     */
    public String getContents() {
        return this.content;
    }
}
