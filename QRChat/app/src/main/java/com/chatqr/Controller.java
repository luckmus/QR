package com.chatqr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chatqr.bl.ChatController;
import com.chatqr.bl.QRCodeHelper;
import com.chatqr.bl.crypto.Helper;
import com.chatqr.bl.dao.DAO;
import com.chatqr.bl.dao.model.Chat;
import com.chatqr.bl.dao.model.InterractMessage;
import com.chatqr.bl.dao.model.Key;
import com.chatqr.bl.dao.model.Message;
import com.chatqr.bl.dao.model.Settings;
import com.chatqr.ui.ImportActivity;
import com.chatqr.ui.features.InitActivity;
import com.chatqr.ui.features.demo.def.DefaultDialogsActivity;
import com.chatqr.ui.features.demo.def.DefaultMessagesActivity;
import com.chatqr.utils.AppUtils;

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

    public void export(Context context, Message message, Key key){
        InterractMessage imsg = new InterractMessage(message, key);
        try {
            String data = ChatController.getInstance().generateBinaryDataForQR(imsg);
            Log.i("TAG", data);
            Bitmap bMap =QRCodeHelper.newInstance().setContent(data).generate();
            String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bMap,"title", null);
            Uri bitmapUri = Uri.parse(bitmapPath);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "download this image");
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            intent.setType("image/*");
            context.startActivity(Intent.createChooser(intent, "Share image via..."));
        }catch (Exception e){
            e.printStackTrace();
            AppUtils.showToast(context,
                    e.getMessage(),
                    false);
        }
    }
}
