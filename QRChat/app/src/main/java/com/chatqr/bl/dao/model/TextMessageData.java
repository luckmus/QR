package com.chatqr.bl.dao.model;

import android.util.Log;
import android.util.Xml;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class TextMessageData extends AbstractMessageData {

    public static final String TEXT = "text";
    public static final String ENCODE = "enc";

    private String text;
    private String encode;

    public TextMessageData(byte[] data, String encode){
        try {
            text = new String(data, encode);
            this.encode = encode;
        } catch (UnsupportedEncodingException e) {
            text = new String(data);
        }
    }

    public TextMessageData(String text, String encode) {
        this.text = text;
        this.encode = encode;
    }

    public TextMessageData(byte[] data){
        this.text = data == null?null:new String(data);
    }

    @Override
    public MessageFormat getMessageFormat() {
        return MessageFormat.Text;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject res = new JSONObject().put(TEXT,text).put(ENCODE, encode);
        Log.i("QR", res.toString());
        return res;
    }

    public String getText() {
        return text;
    }
}
