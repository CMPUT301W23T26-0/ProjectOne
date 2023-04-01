package com.example.qradventure.ui.leaderboard;

/**
 * This class represents a player that isn't the user
 */
public class Player {
    private String name;
    private int score;

    /**
     * Constructor for player class
     */
    public Player() {}

    /**
     * Getter for a player's name
     * @return Name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the player's score
     * @return Score of the player
     */
    public int getScore() {
        return score;
    }

    /**
     * Setter for the player's name
     * @param name Name of the player
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter for the player's score
     * @param score Score of the player
     */
    public void setScore(int score) {
        this.score = score;
    }
}
