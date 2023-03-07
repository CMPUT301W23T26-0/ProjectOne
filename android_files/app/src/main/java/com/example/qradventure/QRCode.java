package com.example.qradventure;

import org.w3c.dom.Comment;

public class QRCode {
    private String Name;
    private int Score;
    private String Comment;
    private String hashValue;
    private String qrContent;
    QRCode(String Name, int Score, String Comment, String hashValue, String qrContent) {
        this.Name = Name;
        this.Score = Score;
        this.Comment = Comment;
        this.hashValue = hashValue;
        this.qrContent = qrContent;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setScore(int Score) {
        this.Score = Score;
    }

    public void setComment(String Comment) {
        this.Comment = Comment;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public void setQrContent(String qrContent) {
        this.qrContent = qrContent;
    }

    public String getName() {
        return this.Name;
    }

    public int getScore() {
        return this.Score;
    }

    public String getComment() {
        return this.Comment;
    }

    public String getHashValue() {
        return this.hashValue;
    }

    public String getQrContent() {
        return this.qrContent;
    }
}
