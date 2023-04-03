package com.example.qradventure.ui.qrcode;

/**
 * This class represents a text comment that users can make on a given QR code
 */
public class Comment {
    private String author;
    private String content;

    /**
     * Constructor for a comment
     * @param author, Author of the comment
     * @param content, The comment's contents
     */
    public Comment(String author, String content) {
        this.author = author;
        this.content = content;
    }

    /**
     * Getter for author
     * @return Name of the author
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Getter for contents
     * @return Contents of the comment
     */
    public String getContents() {
        return this.content;
    }
}
