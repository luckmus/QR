package com.chatqr.bl;

import android.annotation.SuppressLint;

import com.chatqr.Controller;
import com.chatqr.R;
import com.chatqr.bl.crypto.Decoder;
import com.chatqr.bl.crypto.Encoder;
import com.chatqr.bl.crypto.Helper;
import com.chatqr.bl.dao.DAO;
import com.chatqr.bl.dao.model.AbstractMessageData;
import com.chatqr.bl.dao.model.Chat;
import com.chatqr.bl.dao.model.Key;
import com.chatqr.bl.dao.model.Message;
import com.chatqr.bl.dao.model.TextMessageData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static com.chatqr.R.string.chat_name_frmt;
import static com.chatqr.bl.crypto.Helper.addCrc;
import static com.chatqr.bl.crypto.Helper.cryptoKey;


public class ChatController {

    public static final String VERSION = "1";
    public static final String MSG_DATA ="data";
    public static final String HASH ="hash";
    public static final String LOGIN ="login";
    public static final String GEN_DATE ="date";

    private static ChatController instance;
    public static ChatController getInstance() {
        if (instance == null) {
            synchronized (ChatController.class) {
                if (instance == null) {
                    ChatController res = new ChatController();
                    if (res.init()) {
                        instance = res;
                    }
                }
            }
        }
        return instance;
    }

    private boolean init() {
        return true;
    }

    public static Message generateTextMessageAndSave(String msg, String encode, Chat chat) throws Exception {
        Message m = generateTextMessage(msg, encode, chat);
        m = DAO.getInstance().save(m);
        return m;
    }

    public static Message generateTextMessage(String msg, String encode, Chat chat) throws Exception {
        Message res = generateTextMessage(AppSettings.getInstance().getLogin(), msg, encode, DAO.getInstance().getKey(chat.getIdKey()));
        res.setIdChat(chat.getDbId());
        return res;
    }

    public static Message generateTextMessage(String msg, String encode, Key key) throws Exception {
        return generateTextMessage(AppSettings.getInstance().getLogin(), msg, encode, key);
    }

    public static Message generateTextMessage(String login, String msg, String encode, Key key) throws Exception {
        Message res = new Message();
        res.setLogin(login);
        TextMessageData tmd = new TextMessageData(msg, encode);
        res.setData(Encoder.getInstance().encode(addCrc(tmd), cryptoKey(key)));
        return res;
    }

    public void addMessage(byte[] qrData) throws Exception {
        Message msg =  readMessage(qrData);
        DAO.getInstance().save(msg);
    }

    public Message readMessage(byte[] qrData) throws Exception {
        JSONObject cont = new JSONObject(new String(qrData));
        String msgData = cont.get(MSG_DATA).toString();
        byte[] binMsgData = Helper.decodeMsgData(msgData);
        int hash = Integer.parseInt(cont.get(HASH).toString());
        String version = cont.get(VERSION).toString();
        String login = cont.get(LOGIN).toString();
        Object gd = cont.get(GEN_DATE);
        Calendar genDate = Calendar.getInstance();
        if (gd != null){
            try {
                genDate.setTimeInMillis(Long.parseLong(gd.toString()));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Container<Key> keyCont = new Container<>();
        AbstractMessageData decodedMsg = Decoder.getInstance().decode(binMsgData, version, hash, keyCont);
        if (decodedMsg == null){
            throw new Exception(Controller.BASE_APP_CONTEXT.getString(R.string.unknow_key));
        }
        Message res = new Message();
        res.setLogin(login);
        res.setGenDate(genDate);
        addMsgToChat(res, keyCont.getValue());
        return res;
    }

    public Chat addMsgToChat(Message msg, Key key){
        Chat chat = DAO.getInstance().getChatForKey(key.getId());
        if (chat==null){
            chat = createChatForKey(key);
        }
        msg.setIdChat(chat.getDbId());
        msg.setChat(chat);
        return chat;
    }

    @SuppressLint("StringFormatMatches")
    Chat createChatForKey(Key key ){
        Chat chat = DAO.getInstance().getChatForKey(key.getId());
        if (chat!=null){
            return chat;
        }
        chat = new Chat();
        chat.setIdKey(key.getId());
        chat.setChangeDate(Calendar.getInstance());
        chat.setName(Controller.BASE_APP_CONTEXT.getString(R.string.chat_name_frmt, DAO.getInstance().getChats().size()));
        DAO.getInstance().save(chat);
        return chat;
    }
}
