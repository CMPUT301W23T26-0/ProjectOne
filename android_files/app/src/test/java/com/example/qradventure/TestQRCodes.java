package com.example.qradventure;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.qradventure.qrcode.QRCode;

public class TestQRCodes {
    private QRCode mockCode() {
        return new QRCode("2f0eb1859e295bcd183127558f3c205270e7a8004ad362e5123bd5b2774e0f9c");
    }

    private QRCode mockOtherCode() {
        return new QRCode("d187a6972ec711177594de7c7c89651fa2d4c924ef7d5252fdaf5f05a0d6e0b3");
    }

    @Test
    public void testName() {
        QRCode code = mockCode();
        assertEquals("Tall Eager Bengal", code.getName());
        code = mockOtherCode();
        assertEquals("Nice Victorious Koala", code.getName());
    }

    @Test
    public void testScore() {
        QRCode code = mockCode();
        assertEquals(33, code.getScore());
        code = mockOtherCode();
        assertEquals(35, code.getScore());
    }
}
