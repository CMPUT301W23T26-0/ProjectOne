package com.example.qradventure;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class QRController {

    public String generateName(String hash) {
        String[] preAdjectives = {"Super", "Amazing", "Massive", "Ultra", "Deluxe", "Giga",
                                "Little", "Tall", "Dynamic", "Nice", "Tidy", "Tough", "Cool",
                                "Mighty", "Clever", "Friendly"};
        String[] Adjectives = {"Dazzling", "Elegant", "Happy", "Gentle", "Jolly", "Lazy", "Brave",
                                "Fancy", "Beautiful", "Worried", "Clumsy", "Plain", "Eager",
                                "Victorious", "Fierce", "Angry"};
        String[] Nouns = {"Lion", "Tiger", "Leopard", "Cheetah", "Jaguar", "Cat", "Lynx",
                        "Bengal", "Ocelot", "Zebra", "Kangaroo", "Hedgehog",
                        "Koala", "Scorpion", "Cougar", "Gorilla",};
        return preAdjectives[Integer.parseInt(String.valueOf(hash.charAt(0)), 16)] + " " +
                Adjectives[Integer.parseInt(String.valueOf(hash.charAt(1)), 16)] + " " +
                Nouns[Integer.parseInt(String.valueOf(hash.charAt(2)), 16)];
    }

    public Double calculateScore(String content){
        // Calculates value from sha256 string
        // Can be improved later on, but for now it works
        Double value = 0d;
        int streak = 1;
        for (int i = 1; i < content.length()-1; i++) {
            if (content.charAt(i) == content.charAt(i-1) && content.charAt(i) != ' ') {
                streak += 1;
            }
            else if (streak > 1) {
                if (content.charAt(i-1) == '0') {
                    value += Math.pow(20, streak-1);
                } else {
                    value += Math.pow(Integer.parseInt(String.valueOf(content.charAt(i-1)), 16), streak - 1);
                }
                streak = 1;
            }
            else {
                streak = 1;
            }
        }
        return value;
    }

    public String getHash(String source) {
        byte[] hash = null;
        String hashCode = null;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(source.getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Can't calculate SHA-256");
        }

        if (hash != null) {
            StringBuilder hashBuilder = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(b);
                if (hex.length() == 1) {
                    hashBuilder.append("0");
                    hashBuilder.append(hex.charAt(0));
                } else {
                    hashBuilder.append(hex.substring(hex.length() - 2));
                }
            }
            hashCode = hashBuilder.toString();
        }

        return hashCode;
    }

}
