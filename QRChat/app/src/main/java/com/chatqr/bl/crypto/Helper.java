package com.chatqr.bl.crypto;

import com.chatqr.bl.AppSettings;
import com.chatqr.bl.ChatController;
import com.chatqr.bl.Container;
import com.chatqr.bl.dao.DAO;
import com.chatqr.bl.dao.model.AbstractMessageData;
import com.chatqr.bl.dao.model.Chat;
import com.chatqr.bl.dao.model.Key;
import com.chatqr.bl.dao.model.Message;
import com.chatqr.bl.dao.model.TextMessageData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Collection;
import java.util.zip.CRC32;

public class Helper {



    public static Key generateKey(String base){
        Key res = new Key();
        res.setBase(base);
        res.setVer(ChatController.VERSION);
        res.setHash(calcHash(base));
        return res;
    }

    public static byte[] decodeMsgData(String msgData){
        return android.util.Base64.decode(msgData, android.util.Base64.DEFAULT);
    }

    static String encodeMsgData( byte[] msgData){
        return android.util.Base64.encodeToString(msgData, android.util.Base64.DEFAULT);
    }





    static boolean checkCRC(byte[] data, ByteArrayOutputStream baos){
        baos.write(data,0, data.length-Long.BYTES);
        CRC32 crc = new CRC32();
        crc.update(baos.toByteArray());
        long res = crc.getValue();
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(data, data.length-Long.BYTES, Long.BYTES);
        buffer.flip();//need flip
        return res == buffer.getLong();
    }

    public static byte[] addCrc(AbstractMessageData amd) throws Exception {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            byte[] data = amd.toJSONBytes();
            baos.write(data);
            baos.write(calcCRC(data));
            return baos.toByteArray();
        }
    }

    public static byte[] addCrc(byte[] data) throws Exception {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            baos.write(data);
            baos.write(calcCRC(data));
            return baos.toByteArray();
        }
    }

    static byte[] calcCRC(byte[] data){
        CRC32 crc = new CRC32();
        crc.update(data);
        long res = crc.getValue();
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(res);
        return buffer.array();
    }

    public static int calcHash(String base){
        return base.hashCode();
    }

    public static byte[] cryptoKey(Key key){
        byte[] res = new byte[24];
        byte[] baseAr = key.getBase().getBytes();
        System.arraycopy(baseAr, 0, res, 0, baseAr.length>res.length? res.length:baseAr.length);
        return res;
    }

    public static Collection<Key> getKeys(int hash){
        return DAO.getInstance().getKeysByHash(hash);
    }
}
