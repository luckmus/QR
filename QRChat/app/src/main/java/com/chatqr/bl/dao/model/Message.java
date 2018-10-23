package com.chatqr.bl.dao.model;

import com.chatqr.bl.crypto.Decoder;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Calendar;
import java.util.Date;

public class Message implements IMessage  {
    private Long dbId;
    private Long idChat;
    private byte[] data;
    private String login;
    private Calendar insDate;
    private Calendar genDate;
    private int read;

    private Key key;
    private AbstractMessageData message;

    @Override
    public String getId() {
        return dbId.toString();
    }

    @Override
    public String getText() {
        return ((TextMessageData)getMessage()).getText();
    }

    @Override
    public IUser getUser() {
        return new User(login);
    }

    @Override
    public Date getCreatedAt() {
        return genDate.getTime();
    }

    public AbstractMessageData getMessage() {
        if (message == null){
            message = (Decoder.getInstance().decode(data, key.getVer(), key.getHash()));
        }
        return message;
    }

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long id) {
        this.dbId = id;
    }

    public Long getIdChat() {
        return idChat;
    }

    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Calendar getInsDate() {
        return insDate;
    }

    public void setInsDate(Calendar insDate) {
        this.insDate = insDate;
    }

    public Calendar getGenDate() {
        return genDate;
    }

    public void setGenDate(Calendar genDate) {
        this.genDate = genDate;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
