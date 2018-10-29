package com.chatqr.bl.crypto;

import com.chatqr.bl.Container;
import com.chatqr.bl.dao.model.AbstractMessageData;
import com.chatqr.bl.dao.model.Key;
import com.chatqr.bl.dao.model.TextMessageData;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
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

    public AbstractMessageData decode(byte[] data, String version, int hash, Container<Key> key){
        AbstractMessageData res = null;
        res = new TextMessageData(decodeBytes(data, version, hash, key));
        return res;
    }

    byte[] decodeBytes(byte[] data, String version, int hash, Container<Key> keyCont){
        Collection<Key> keys =  Helper.getKeys(hash);
        for (Key key:keys){
            byte[] res = decodeBytes(data, version, key);
            if (res != null){
                if (keyCont!=null) {
                    keyCont.setValue(key);
                }
                return res;
            }
        }
        return null;
    }

    byte[] decodeBytes(byte[] data, String version, Key key) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] dec = Crypter.DESDecodePKCS5Padding(Helper.cryptoKey(key), data);
            if (Helper.checkCRC(dec, baos)){
                return baos.toByteArray();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
