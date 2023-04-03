package com.example.qradventure;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.qradventure.users.UserDataClass;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class TestUserDataClass {

    @Test
    public void testSetData() {
        UserDataClass user = UserDataClass.getInstance();
        Map<String, Object> data = new HashMap<>();
        String username = "user";
        String email = "abc@123";
        String phone = "1234567890";
        int totalScore = 231;
        int highestQrScore = 123;
        String highestQrHash = "def";

        data.put("username", username);
        data.put("email", email);
        data.put("phone", phone);
        data.put("totalScore", totalScore);
        data.put("highestQrScore", highestQrScore);
        data.put("highestQrHash", highestQrHash);
        user.setData(data);

        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmailInfo());
        assertEquals(phone, user.getPhoneInfo());
        assertEquals(totalScore, user.getTotalScore());
        assertEquals(highestQrScore, user.getHighestQrScore());
        assertEquals(highestQrHash, user.getHighestQrHash());
    }

    @Test
    public void testPhoneInfo() {
        UserDataClass user = UserDataClass.getInstance();
        String phone = "1234567890";
        user.setPhoneInfo(phone);
        assertEquals(phone, user.getPhoneInfo());
    }

    @Test
    public void testEmailInfo() {
        UserDataClass user = UserDataClass.getInstance();
        String email = "abc@123";
        user.setPhoneInfo(email);
        assertEquals(email, user.getPhoneInfo());
    }

    @Test
    public void testUsername() {
        UserDataClass user = UserDataClass.getInstance();
        String username = "user";
        user.setUsername(username);
        assertEquals(username, user.getUsername());
    }
    @Test
    public void testTotalScore() {
        UserDataClass user = UserDataClass.getInstance();
        int totalScore = 231;
        user.setTotalScore(totalScore);
        assertEquals(totalScore, user.getTotalScore());   }

    @Test
    public void testUserRef() {

    }

    @Test
    public void testUserCodesRef() {

    }
    @Test
    public void testHighestQrScore() {

    }@Test
    public void testHighestQrHash() {

    }
    @Test
    public void testHighestQr() {

    }

    @Test
    public void testAddCode() {

    }

    @Test
    public void testDeleteCode() {

    }

    @Test
    public void testUpdateField() {

    }

    @Test
    public void testRefreshHighestQr() {

    }


}
