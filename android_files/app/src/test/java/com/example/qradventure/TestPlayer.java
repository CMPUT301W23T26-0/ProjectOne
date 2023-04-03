package com.example.qradventure;

import com.example.qradventure.ui.leaderboard.Player;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestPlayer {
    @Test
    public void testName() {
        Player player = new Player();
        String name = "player1";
        player.setName(name);
        assertEquals(name, player.getName());
    }

    @Test
    public void testScore() {
        Player player = new Player();
        int score = 100;
        player.setScore(score);
        assertEquals(score, player.getScore());
    }
}
