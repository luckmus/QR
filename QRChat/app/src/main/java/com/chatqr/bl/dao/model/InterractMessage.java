package com.chatqr.bl.dao.model;

import java.util.Calendar;

public class InterractMessage {
    private String login;

    private int hash;
    private String version;
    private Calendar genDate;
    private Key key;
    private byte[] encData;

    public InterractMessage(Message message, Key key){
        this.login = message.getLogin();
        this.genDate = (Calendar) message.getGenDate().clone();
        this.hash = key.getHash();
        this.version = key.getVer();
        this.key = key;
        this.encData = message.getData();
    }

    public Key getKey() {
        return key;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Calendar getGenDate() {
        return genDate;
    }

    public void setGenDate(Calendar genDate) {
        this.genDate = genDate;
    }

    public byte[] getEncData() {
        return encData;
    }

    public void setEncData(byte[] encData) {
        this.encData = encData;
    }
}
