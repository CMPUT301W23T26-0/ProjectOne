package com.example.qradventure;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class QRController {

    // Creates image by stacking drawable layers and changing colors
    public Drawable generateImage(Context context, String hash) {
        int[] colors = {
                Color.rgb(255,206,0), Color.RED, Color.rgb(0,171,56), Color.CYAN, Color.rgb(255,20,10),
                Color.rgb(154, 240, 0), Color.rgb(130,0,172), Color.rgb(204,114,245), Color.rgb(255,179,0),
                Color.rgb(0,149,67), Color.rgb(2,136,217), Color.rgb(0,71,189), Color.rgb(255,100,59),
                Color.rgb(255,130,42), Color.rgb(182,16,191), Color.rgb(7, 185, 252), Color.rgb(234, 0, 52)};

        // Choose background color
        Drawable backgroundDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_background_64, null);
        Drawable wrappedBGDrawable = DrawableCompat.wrap(backgroundDrawable);

        // Choose shape based on modulo and change color
        Integer secondValue = Integer.parseInt(String.valueOf(hash.charAt(1)), 16);
        Drawable shapeDrawable;
        if (secondValue % 4 == 0) {
            shapeDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_shape_plus_64, null);
        } else if (secondValue % 4 == 1) {
            shapeDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_shape_star_64, null);
        } else if (secondValue % 4 == 2) {
            shapeDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_shape_heart_64, null);
        } else {
            shapeDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_shape_diamond_64, null);
        }
        Drawable wrappedSHDrawable = DrawableCompat.wrap(shapeDrawable);

        // Choose outline color
        Drawable outlineDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_out_line_64, null);
        Drawable wrappedOUTDrawable = DrawableCompat.wrap(outlineDrawable);

        DrawableCompat.setTint(wrappedBGDrawable, colors[Integer.parseInt(String.valueOf(hash.charAt(0)), 16)]);
        DrawableCompat.setTint(wrappedSHDrawable, colors[Integer.parseInt(String.valueOf(hash.charAt(2)), 16)]);
        DrawableCompat.setTint(wrappedOUTDrawable, colors[Integer.parseInt(String.valueOf(hash.charAt(3)), 16)]);

        LayerDrawable finalDrawable = new LayerDrawable(new Drawable[] {wrappedBGDrawable, wrappedSHDrawable, wrappedOUTDrawable});

        return finalDrawable;
    }

    // Generate name using preAdjective + Adjectives + Noun based on hex to integer index
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

    // Content to SHA256 hash
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
