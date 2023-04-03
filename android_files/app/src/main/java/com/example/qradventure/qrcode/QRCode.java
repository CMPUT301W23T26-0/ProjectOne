package com.example.qradventure.qrcode;

/**
 * This class represents a QR code
 */
public class QRCode {
    private String name;
    private int score;
    private String comment;
    private String hashValue;

    private QRController qrController = new QRController();

    /**
     * Empty constructor for the QRCode class
     */
    public QRCode() {
        // Empty
    }

    /**
     * Creates a qrcode class with the source content
     * @param qrContent A string containing the qrcode content after scanning
     */
    // Future: Add comments parameter, as well as longitude / latitude and scanner id (repeats?)
    public QRCode(String qrContent) {
        this.hashValue = qrController.getHash(qrContent);
        this.name = qrController.generateName(this.hashValue);
        this.comment = "A comment."; // Previously hashValue for debug
        this.score = (int) Math.round(qrController.calculateScore(this.hashValue));
    }

    /**
     * Sets the qrcode's name
     * @param name A string containing the qrcode's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the score of the qrcode class
     * @param score An integer containing the qrcode's score value
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Sets the comment for this qrcode Class
     * @param comment A string containing a user's comment about this qrcode
     */
    public void setComment(String comment) { this.comment = comment; }

    /**
     * Sets the hash of this qrcode class
     * @param hashValue A string containing the hash value of this qrcode's content
     */
    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    /**
     * Gets the qrcode's name
     * @return A string representing this qrcode's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the qrcode's score value
     * @return An integer representing this qrcode's score value
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Gets the qrcode's comment
     * @return A string representing this qrcode's comments
     */
    public String getComment() {
        return this.comment;
    }

    /**
     * Gets the qrcode's hash value
     * @return A string representing this qrcode's hash value
     */
    public String getHashValue() {
        return this.hashValue;
    }
}
