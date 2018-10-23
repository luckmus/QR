package com.chatqr.bl.crypto;

import com.chatqr.bl.dao.model.AbstractMessageData;
import com.chatqr.bl.dao.model.TextMessageData;

import java.util.UUID;

public class Decoder {

    public static Decoder instance;

    public static Decoder getInstance() {
        if (instance == null) {
            Decoder res = new Decoder();
            if (res.init()) {
                instance = res;
            }
        }
        return instance;
    }

    private boolean init() {
        return true;
    }

    public AbstractMessageData decode(byte[] data, String version, int hash){
        AbstractMessageData res = null;
        res = new TextMessageData(decodeBytes(data, version, hash));
        return res;
    }

    byte[] decodeBytes(byte[] data, String version, int hash){
        return UUID.randomUUID().toString().getBytes();
    }
}
