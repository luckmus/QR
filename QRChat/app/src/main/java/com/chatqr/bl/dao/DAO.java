package com.chatqr.bl.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chatqr.Controller;
import com.chatqr.bl.crypto.Helper;
import com.chatqr.bl.dao.model.Chat;
import com.chatqr.bl.dao.model.Key;
import com.chatqr.bl.dao.model.Message;
import com.chatqr.bl.dao.model.Settings;
import com.chatqr.bl.dao.model.TextMessageData;
import com.chatqr.bl.dao.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DAO {
    private static DAO instance;
    private static final String TABLE_KEYS="keys";
    private static final String KEYS_BASE="base";
    private static final String KEYS_VER="ver";
    private static final String KEYS_HASH="hash";
    private static final String FIELD_ID = "id";
    private static final String TABLE_CHAT="chat";
    private static final String CHAT_ID_KEY="id_key";
    private static final String CHAT_NAME="name";
    private static final String CHAT_INS_DATE="ins_date";
    private static final String CHAT_CHANGE_DATE="change_date";
    private static final String TABLE_MESSAGE="message";
    private static final String MESSAGE_ID_CHAT="id_chat";
    private static final String MESSAGE_DATA="data";
    private static final String MESSAGE_INS_DATE="ins_date";
    private static final String MESSAGE_GEN_DATE="gen_date";
    private static final String MESSAGE_READ="read";
    private static final String MESSAGE_LOGIN="login";
    private static final String TABLE_SETTINGS="settings";
    private static final String SETTINGS_LOGIN="login";
    private SQLiteDatabase db;
    private static final String DB_NAME = "chat_qr.db";
    SimpleDateFormat iso8601Format = new SimpleDateFormat(     "yyyy-MM-dd HH:mm:ss");

    public static DAO getInstance() {
        if (instance == null) {
            synchronized (DAO.class) {
                if (instance == null) {
                    DAO res = new DAO();
                    if (res.init()) {
                        instance = res;
                    }
                }
            }
        }
        return instance;
    }

    private boolean init() {
        //Controller.BASE_APP_CONTEXT.deleteDatabase(DB_NAME);
        db = Controller.BASE_APP_CONTEXT.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_KEYS+" ("+FIELD_ID+" INTEGER  PRIMARY KEY AUTOINCREMENT, "+KEYS_BASE+" TEXT NOT NULL UNIQUE, "+KEYS_VER+" TEXT, "+KEYS_HASH+" INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_CHAT+" ("+FIELD_ID+" INTEGER  PRIMARY KEY AUTOINCREMENT, "+CHAT_ID_KEY+" INTEGER , "+CHAT_NAME+" TEXT, "+CHAT_INS_DATE+" TEXT, "+CHAT_CHANGE_DATE+" TEXT, FOREIGN KEY ("+CHAT_ID_KEY+") REFERENCES  "+TABLE_KEYS+"("+FIELD_ID+") )");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_MESSAGE+" ("+FIELD_ID+" INTEGER  PRIMARY KEY AUTOINCREMENT, "+MESSAGE_ID_CHAT+" INTEGER , "+MESSAGE_DATA+" BLOB, "+MESSAGE_INS_DATE+" TEXT, "+MESSAGE_GEN_DATE+" TEXT, "+MESSAGE_READ+" INTEGER DEFAULT 0, "+MESSAGE_LOGIN+" TEXT, FOREIGN KEY ("+MESSAGE_ID_CHAT+") REFERENCES  "+TABLE_CHAT+"("+FIELD_ID+") )");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_SETTINGS+" ("+SETTINGS_LOGIN+" TEXT )");

        return true;
    }

    public Collection<Key> getKeysByHash(int keyHash){
        Cursor cursor = db.query(TABLE_KEYS,
                new String[]{FIELD_ID, KEYS_BASE, KEYS_VER, KEYS_HASH},
                KEYS_HASH+"=?", new String[]{Integer.toString(keyHash)},
                null,null, null);
        Collection<Key> res = readKeys(cursor);
        return res;
    }

    public boolean isSettingsExist(){
        try(Cursor cursor = db.query(TABLE_SETTINGS, new String[]{SETTINGS_LOGIN}, null, null, null, null, null)) {
            return cursor.moveToNext();
        }
    }

    public Settings loadSettings(){
        try(Cursor cursor = db.query(TABLE_SETTINGS, new String[]{SETTINGS_LOGIN}, null, null, null, null, null)) {
            if (cursor.moveToNext()){
                Settings res = new Settings();
                res.setLogin(cursor.getString(0));
                return res;
            }
            return null;
        }
    }

    public void save(Settings settings){
        ContentValues values = new ContentValues();
        values.put(SETTINGS_LOGIN, settings.getLogin());
        if (!isSettingsExist()){
            long res = (Long)db.insert(TABLE_SETTINGS, null, values);
        }else{
            db.update(TABLE_SETTINGS, values, null, null);
        }
    }

    public void save(Message message){
        if (message == null){
            throw new NullPointerException("save message is null");
        }
        if (message.getIdChat() == null){
            throw new NullPointerException("save message id_chat is null");
        }
        ContentValues values = new ContentValues();
        values.put(MESSAGE_ID_CHAT, message.getIdChat());
        values.put(MESSAGE_DATA, message.getData());
        values.put(MESSAGE_INS_DATE, calendarTostring(message.getInsDate()));
        values.put(MESSAGE_GEN_DATE, calendarTostring(message.getGenDate()==null?Calendar.getInstance():message.getGenDate()));
        values.put(MESSAGE_READ, message.getRead());
        values.put(MESSAGE_LOGIN, message.getLogin()==null?null:message.getLogin());

        if (message.getDbId()==null){
            long res = (Long)db.insert(TABLE_MESSAGE, null, values);
            message.setDbId(res);
        }else{
            db.update(TABLE_MESSAGE, values, FIELD_ID+"=?", new String[]{Long.toString(message.getDbId())});
        }
    }

    public Collection<Message> getMessages(long idChat){
        Cursor cursor = db.query(TABLE_MESSAGE,
                new String[]{FIELD_ID, MESSAGE_ID_CHAT, MESSAGE_DATA, MESSAGE_INS_DATE, MESSAGE_GEN_DATE, MESSAGE_READ },
                MESSAGE_ID_CHAT+"=?", new String[]{Long.toString(idChat)},
                null,null, MESSAGE_GEN_DATE+" desc");
        return readMessages(cursor);
    }

    public Key getKeyForChat(long chatId){
        try {
            Chat chat = getChat(chatId);
            Key key = getKey(chat.getIdKey());
            return key;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Collection<Message> getMessages(long idChat, int offset, int size){
        Cursor cursor = db.query(TABLE_MESSAGE,
                new String[]{FIELD_ID, MESSAGE_ID_CHAT, MESSAGE_DATA, MESSAGE_INS_DATE, MESSAGE_GEN_DATE, MESSAGE_LOGIN },
                MESSAGE_ID_CHAT+"=?", new String[]{Long.toString(idChat)},
                null,null, MESSAGE_GEN_DATE+" desc",
                String.format("%d, %d", offset, size));
        return readMessages(cursor);
    }

    public List<User> getUsers(long chatId){
        Cursor cursor = db.query(true, TABLE_MESSAGE, new String[]{MESSAGE_LOGIN},
                MESSAGE_ID_CHAT+"=?",new String[]{Long.toString(chatId)}, null,
                null, null,null);
        LinkedList<User> res = new LinkedList<>();
        while (cursor.moveToNext()){
            res.add(new User(cursor.getString(0)));
        }
        return res;
    }

    private Collection<Message> readMessages(Cursor cursor) {
        Collection<Message> res = new LinkedList();
        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(FIELD_ID);
            int chatIndex = cursor.getColumnIndex(MESSAGE_ID_CHAT);
            int dataIndex = cursor.getColumnIndex(MESSAGE_DATA);
            int insIndex = cursor.getColumnIndex(MESSAGE_INS_DATE);
            int genIndex = cursor.getColumnIndex(MESSAGE_GEN_DATE);
            int readIndex = cursor.getColumnIndex(MESSAGE_READ);
            int loginIndex = cursor.getColumnIndex(MESSAGE_LOGIN);
            while (cursor.moveToNext()) {
                Message message = new Message();
                if (idIndex >= 0) {
                    message.setDbId(cursor.getLong(idIndex));
                }
                if (chatIndex >= 0) {
                    message.setIdChat(cursor.getLong(chatIndex));
                }
                if (dataIndex >= 0) {
                    message.setData(cursor.getBlob(dataIndex));
                }
                if ((insIndex >= 0) && (cursor.getString(insIndex) != null)) {
                    message.setInsDate(stringToCalendar(cursor.getString(insIndex)));
                }
                if ((genIndex >= 0) && (cursor.getString(genIndex) != null)) {
                    message.setGenDate(stringToCalendar(cursor.getString(genIndex)));
                }
                if (readIndex >= 0) {
                    message.setRead(cursor.getInt(readIndex));
                }
                if (loginIndex >= 0) {
                    message.setLogin(cursor.getString(loginIndex));
                }
                res.add(message);
            }
        }
        return res;

    }

    public Chat createChatByKey(Key key, String name){
        db.beginTransaction();
        save(key);
        Chat chat = new Chat();
        chat.setIdKey(key.getId());
        chat.setName(name);
        save(chat);
        db.setTransactionSuccessful();
        db.endTransaction();
        return chat;

    }

    public void save(Key key){
        ContentValues values = new ContentValues();
        values.put(KEYS_BASE, key.getBase());
        values.put(KEYS_VER, key.getVer());
        values.put(KEYS_HASH, Integer.toString(key.getHash()));
        if (key.getId()==null){
            long res = (Long)db.insert(TABLE_KEYS, null, values);
            key.setId(res);
        }else{
            db.update(TABLE_KEYS, values, FIELD_ID+"=?", new String[]{Long.toString(key.getId())});
        }
    }

    public Key getKey(long id){
        Cursor cursor = db.query(TABLE_KEYS,
                new String[]{FIELD_ID, KEYS_BASE, KEYS_VER, KEYS_HASH},
                FIELD_ID+"=?", new String[]{Long.toString(id)},
                null,null, null);
        Collection<Key> res = readKeys(cursor);
        return res.isEmpty()?null:res.iterator().next();
    }

    private Collection<Key> readKeys(Cursor cursor) {
        Collection<Key> res = new LinkedList();
        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(FIELD_ID);
            int baseIndex = cursor.getColumnIndex(KEYS_BASE);
            int verIndex = cursor.getColumnIndex(KEYS_VER);
            int hashIndex = cursor.getColumnIndex(KEYS_HASH);
            while (cursor.moveToNext()) {
                Key key = new Key();
                if (idIndex >= 0) {
                    key.setId(cursor.getLong(idIndex));
                }
                if (baseIndex >= 0) {
                    key.setBase(cursor.getString(baseIndex));
                }
                key.setVer(cursor.getString(verIndex));
                key.setHash(cursor.getInt(hashIndex));
                res.add(key);
            }
        }
        return res;
    }

    public void save(Chat chat){
        if (chat == null){
            throw new NullPointerException("save chat is null");
        }
        if (chat.getIdKey() == null){
            throw new NullPointerException("save chat id_key is null");
        }
        ContentValues values = new ContentValues();
        values.put(CHAT_ID_KEY, Long.toString(chat.getIdKey()));
        values.put(CHAT_NAME, chat.getName());
        values.put(CHAT_INS_DATE, calendarTostring(chat.getInsDate()));
        values.put(CHAT_CHANGE_DATE, calendarTostring(chat.getChangeDate()));
        if (chat.getDbId()==null){
            long res = (Long)db.insert(TABLE_CHAT, null, values);
            chat.setDbId(res);
        }else{
            db.update(TABLE_CHAT, values, FIELD_ID+"=?", new String[]{Long.toString(chat.getDbId())});
        }
    }

    public void deleteChat(int chatId){
        db.beginTransaction();
        db.delete(TABLE_MESSAGE, MESSAGE_ID_CHAT+"=?", new String[]{Long.toString(chatId)});
        db.delete(TABLE_CHAT, FIELD_ID+"=?", new String[]{Long.toString(chatId)});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void deleteKey(long keyId){
        ContentValues values = new ContentValues();
        values.putNull(CHAT_ID_KEY);
        db.beginTransaction();
        db.update(TABLE_CHAT, values,CHAT_ID_KEY+"=?", new String[]{Long.toString(keyId)});
        db.delete(TABLE_KEYS, FIELD_ID+"=?", new String[]{Long.toString(keyId)});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public Chat getChatForKey(long keyId){
        Cursor cursor = db.query(TABLE_CHAT,
                new String[]{FIELD_ID, CHAT_ID_KEY, CHAT_NAME, CHAT_INS_DATE, CHAT_CHANGE_DATE},
                CHAT_ID_KEY+"=?", new String[]{Long.toString(keyId)},
                null,null, CHAT_CHANGE_DATE+" desc");

        Collection<Chat> res = readChats(cursor);
        return res.isEmpty()?null:res.iterator().next();
    }

    public Chat getChat(long id){
        Cursor cursor = db.query(TABLE_CHAT,
                new String[]{FIELD_ID, CHAT_ID_KEY, CHAT_NAME, CHAT_INS_DATE, CHAT_CHANGE_DATE},
                FIELD_ID+"=?", new String[]{Long.toString(id)},
                null,null, CHAT_CHANGE_DATE+" desc");

        Collection<Chat> chats = readChats(cursor);

        return chats.isEmpty()?null:chats.iterator().next();
    }

    public Collection<Chat> getChats(){
        Cursor cursor = db.query(TABLE_CHAT,
                new String[]{FIELD_ID, CHAT_ID_KEY, CHAT_NAME, CHAT_INS_DATE, CHAT_CHANGE_DATE},
                null, null,
                null,null, CHAT_CHANGE_DATE+" desc");

        return readChats(cursor);
    }

    private Collection<Chat> readChats(Cursor cursor) {
        Collection<Chat> res = new LinkedList();
        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(FIELD_ID);
            int keyIndex = cursor.getColumnIndex(CHAT_ID_KEY);
            int nameIndex = cursor.getColumnIndex(CHAT_NAME);
            int insIndex = cursor.getColumnIndex(CHAT_INS_DATE);
            int chIndex = cursor.getColumnIndex(CHAT_CHANGE_DATE);

            while (cursor.moveToNext()) {
                Chat chat = new Chat();
                if (idIndex >= 0) {
                    chat.setDbId(cursor.getLong(idIndex));
                }
                chat.setIdKey(cursor.getLong(keyIndex));
                if (nameIndex >= 0) {
                    chat.setName(cursor.getString(nameIndex));
                }
                if ((insIndex >= 0) && (cursor.getString(insIndex) != null)) {
                    chat.setInsDate(stringToCalendar(cursor.getString(insIndex)));
                }
                if ((chIndex >= 0) && (cursor.getString(chIndex) != null)) {
                    chat.setChangeDate(stringToCalendar(cursor.getString(chIndex)));
                }
                res.add(chat);
            }
        }
        return res;
    }

    private Calendar stringToCalendar(String val){
        try {
            Date d = iso8601Format.parse(val);
            Calendar res = Calendar.getInstance();
            res.setTime(d);
            return res;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String calendarTostring(Calendar val){
        return val==null?null:iso8601Format.format(val.getTime());
    }
}
