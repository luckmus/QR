package com.chatqr.bl;

import com.chatqr.bl.dao.DAO;
import com.chatqr.bl.dao.model.Settings;

public class AppSettings {
    private static AppSettings instance;
    private Settings settings;

    public static AppSettings getInstance() {
        if (instance == null) {
            synchronized (AppSettings.class) {
                if (instance == null) {
                    AppSettings res = new AppSettings();
                    if (res.init()) {
                        instance = res;
                    }
                }
            }
        }
        return instance;
    }

    public String getLogin(){
        return settings.getLogin();
    }

    private boolean init(){
        settings = DAO.getInstance().loadSettings();
        return settings!=null;
    }
}
