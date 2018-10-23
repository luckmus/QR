package com.chatqr.bl.dao.model;

import android.util.Xml;

import java.io.UnsupportedEncodingException;

public class TextMessageData extends AbstractMessageData {

    private String text;

    public TextMessageData(byte[] data, String encode){
        try {
            text = new String(data, encode);
        } catch (UnsupportedEncodingException e) {
            text = new String(data);
        }
    }

    public TextMessageData(byte[] data){
        this.text = new String(data);
    }

    @Override
    public MessageFormat getMessageFormat() {
        return MessageFormat.Text;
    }

    public String getText() {
        return text;
    }
}
