package com.example.qradventure;

import static org.junit.Assert.assertEquals;

import com.example.qradventure.ui.qrcode.Comment;

import org.junit.Test;

public class TestComments {
    @Test
    public void testGetAuthor() {
        String author = "player1";
        String commentText = "This is my comment!";
        Comment mockComment = new Comment(author, commentText);
        assertEquals(author, mockComment.getAuthor());
    }

    @Test
    public void testGetContents() {
        String author = "player1";
        String commentText = "This is my comment!";
        Comment mockComment = new Comment(author, commentText);
        assertEquals(commentText, mockComment.getContents());
    }
}
