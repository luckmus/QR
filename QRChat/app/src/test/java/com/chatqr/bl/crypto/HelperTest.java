package com.chatqr.bl.crypto;

import org.json.JSONException;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static org.junit.Assert.*;

public class HelperTest {

    @Test
    public void generateTextMessage() throws JSONException {
        //Helper.generateTextMessage("test","test","utf8", null);
    }

    @Test
    public void testCRC() throws Exception {
        byte[] res = Helper.addCrc("test".getBytes());
        assertTrue(Helper.checkCRC(res, new ByteArrayOutputStream()));
    }

    @Test
    public void testBase64() throws Exception {
        String res = Helper.encodeMsgData(UUID.randomUUID().toString().getBytes());
        System.out.println("encodeMsgData:"+res);

    }
}