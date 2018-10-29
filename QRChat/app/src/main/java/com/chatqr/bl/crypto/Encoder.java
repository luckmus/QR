package com.chatqr.bl.crypto;

import android.util.Xml;

import com.chatqr.bl.dao.model.AbstractMessageData;

import org.json.JSONException;

public class Encoder {
    private static Encoder instance;

    public static Encoder getInstance() {
        if (instance == null) {
            synchronized (Encoder.class) {
                if (instance == null) {
                    Encoder res = new Encoder();
                    if (res.init()) {
                        instance = res;
                    }
                }
            }
        }
        return instance;
    }

    private boolean init(){
        return true;
    }

    public byte[] encode(byte[] data, byte[] key) throws Exception {
        byte[] res = Crypter.DESEncodePKCS5Padding(key, data);
        return res;
    }
}
