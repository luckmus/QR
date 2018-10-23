package com.chatqr;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chatqr.bl.crypto.Helper;
import com.chatqr.bl.dao.DAO;
import com.chatqr.bl.dao.model.Chat;
import com.chatqr.bl.dao.model.Key;
import com.chatqr.bl.dao.model.Settings;
import com.chatqr.ui.features.InitActivity;
import com.chatqr.ui.features.demo.def.DefaultDialogsActivity;

import java.io.Serializable;
import java.util.Collection;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Controller {
    public static Context BASE_APP_CONTEXT;

    private static Controller instance;

    private Settings settings;

    public static Controller getInstance() {
        if (instance == null) {
            synchronized (Controller.class) {
                if (instance==null) {
                    Controller res = new Controller();
                    instance = res;
                }
            }
        }
        return instance;
    }

    public void navigateToCurrentMenu(Context context){
        if (!DAO.getInstance().isSettingsExist()){
            showInit(context);
            return;
        }
        showChat(context);
    }


    public Chat createChat(Context context,String name, String base){
        Key key = Helper.generateKey(base);
        Chat chat = DAO.getInstance().createChatByKey(key, name);
        Toast.makeText(context,"chatId: "+chat.getDbId(), Toast.LENGTH_SHORT).show();
        navigateToCurrentMenu(context);
        return chat;
    }

    public static void showInit(Context context) {
        Intent intent = new Intent(context, InitActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        //intent.putExtra(EXTRA_TICKETS, (Serializable) tickets);
        context.startActivity(intent);
    }
    public void showChat(Context context) {
        DefaultDialogsActivity.open(context);
    }

    public void saveSettings(Context context, InitActivity view){
        try {
            Settings set = new Settings();
            EditText login = view.findViewById(R.id.login);
            set.setLogin(login.getText().toString());
            DAO.getInstance().save(set);
            navigateToCurrentMenu(context);
        }catch (Exception e){
            Toast.makeText(context,context.getString(R.string.error_on_save_settings), Toast.LENGTH_SHORT).show();
            Log.i("QR", "saveSettings error: "+ e.getMessage());
            e.printStackTrace();
        }



    }
}
