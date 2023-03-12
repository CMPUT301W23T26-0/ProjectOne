package com.example.qradventure;

import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import org.w3c.dom.Comment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class QRCode {
    private String name;
    private int score;
    private String comment;
    private String hashValue;

    QRController qrController = new QRController();

    QRCode() {
        // Empty
    }

    // Future: Add comments parameter, as well as longitude / latitude and scanner id (repeats?)
    QRCode(String qrContent) {
        this.hashValue = qrController.getHash(qrContent);
        this.name = qrController.generateName(this.hashValue);
        this.comment = "A comment."; // Previously hashValue for debug
        this.score = (int) Math.round(qrController.calculateScore(this.hashValue));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setComment(String comment) { this.comment = comment; }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public String getName() {
        return this.name;
    }

    public int getScore() {
        return this.score;
    }

    public String getComment() {
        return this.comment;
    }

    public Drawable getImage() {
        return null;
    }

    public String getHashValue() {
        return this.hashValue;
    }
}
