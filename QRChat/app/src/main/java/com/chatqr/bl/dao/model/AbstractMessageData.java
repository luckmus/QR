package com.chatqr.bl.dao.model;

import com.chatqr.bl.ChatController;
import com.chatqr.bl.dao.model.transport.TranspProtocolIf;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public abstract class AbstractMessageData {
    public enum MessageFormat{
        Text
    }
    private byte[] data;
    private TranspProtocolIf transport;

    public abstract MessageFormat getMessageFormat();
    public abstract JSONObject toJSON() throws JSONException;

    public byte[] toJSONBytes() throws JSONException {
        return ChatController.getInstance().jsonToBytes(toJSON());

    }

    public byte[] getData() {
        return data;
    }

    public TranspProtocolIf getTransport() {
        return transport;
    }

    public void setTransport(TranspProtocolIf transport) {
        this.transport = transport;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
