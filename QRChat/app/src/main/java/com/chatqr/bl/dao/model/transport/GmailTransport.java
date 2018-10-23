package com.chatqr.bl.dao.model.transport;

public class GmailTransport implements TranspProtocolIf {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
