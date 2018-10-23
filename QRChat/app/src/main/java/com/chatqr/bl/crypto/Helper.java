package com.chatqr.bl.crypto;

import com.chatqr.bl.AppSettings;
import com.chatqr.bl.dao.model.AbstractMessageData;
import com.chatqr.bl.dao.model.Key;
import com.chatqr.bl.dao.model.Message;

public class Helper {
    private static final String version = "1";

    public static Key generateKey(String base){
        Key res = new Key();
        res.setBase(base);
        res.setVer(version);
        res.setHash(calcHash(base));
        return res;
    }

    public static Message generateMessage(byte[] msg, AbstractMessageData.MessageFormat frmt, Key key){
        Message res = new Message();
        res.setLogin(AppSettings.getInstance().getLogin());
        return res;
    }

    public static int calcHash(String base){
        return base.hashCode();
    }

    public static byte[] cryptoKey(String base){
        byte[] res = new byte[32];
        byte[] baseAr = base.getBytes();
        System.arraycopy(base, 0, res, 0, baseAr.length>res.length? res.length:baseAr.length);
        return res;
    }
}
