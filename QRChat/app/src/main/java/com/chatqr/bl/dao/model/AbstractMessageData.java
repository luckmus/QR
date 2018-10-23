package com.chatqr.bl.dao.model;

import com.chatqr.bl.dao.model.transport.TranspProtocolIf;

public abstract class AbstractMessageData {
    public enum MessageFormat{
        Text
    }
    private byte[] data;
    private TranspProtocolIf transport;

    public abstract MessageFormat getMessageFormat();

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
