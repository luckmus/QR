package com.chatqr.bl.crypto;

import android.util.Log;

import com.chatqr.bl.Container;
import com.chatqr.bl.dao.model.AbstractMessageData;
import com.chatqr.bl.dao.model.Key;
import com.chatqr.bl.dao.model.TextMessageData;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.UUID;

public class Decoder {

    public static final byte[] iv = new byte[8];

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
        Log.i("TAG", "decode: "+new String(org.apache.commons.codec.binary.Hex.encodeHex(data)).toUpperCase());
        res = new TextMessageData(decodeBytes(data, version, hash, key));
        try {
            Log.i("TAG", "decode res: "+res.toJSON().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            byte[] dec = Crypter.TripleDESDecodePKCS5Padding(Helper.cryptoKey(key), iv, data);
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
