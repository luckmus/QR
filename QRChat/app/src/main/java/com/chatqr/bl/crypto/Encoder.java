package com.chatqr.bl.crypto;

import android.util.Log;
import android.util.Xml;

import com.chatqr.bl.dao.model.AbstractMessageData;

import org.json.JSONException;

import static com.chatqr.bl.crypto.Decoder.iv;

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
        Log.i("TAG", "encode: "+new String(data));
        byte[] res = Crypter.TripleDESEncodePKCS5Padding(key, iv, data);
        Log.i("TAG", "encode res: "+new String(org.apache.commons.codec.binary.Hex.encodeHex(res)).toUpperCase());
        return res;
    }
}
