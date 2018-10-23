package com.chatqr.bl.dao.model;

import com.stfalcon.chatkit.commons.models.IUser;

public class User implements IUser {

    private String name;

    public User(String name) {
        this.name = name;
    }

    public User() {
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return null;
    }
}
