package com.chatqr.bl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.chatqr.Controller;
import com.chatqr.R;
import com.chatqr.bl.crypto.Decoder;
import com.chatqr.bl.crypto.Encoder;
import com.chatqr.bl.crypto.Helper;
import com.chatqr.bl.dao.DAO;
import com.chatqr.bl.dao.model.AbstractMessageData;
import com.chatqr.bl.dao.model.Chat;
import com.chatqr.bl.dao.model.InterractMessage;
import com.chatqr.bl.dao.model.Key;
import com.chatqr.bl.dao.model.Message;
import com.chatqr.bl.dao.model.TextMessageData;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static com.chatqr.R.string.chat_name_frmt;
import static com.chatqr.bl.crypto.Helper.addCrc;
import static com.chatqr.bl.crypto.Helper.cryptoKey;


public class ChatController {

    public static final String VERSION = "1";
    public static final String VERSION_TAG ="version";
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

    public void addMessage(String qrData) throws Exception {
        Message msg =  readMessage(qrData);
        DAO.getInstance().save(msg);
    }

    public Message readMessage(String qrData) throws Exception {
        JSONObject cont = new JSONObject(new String(qrData));
        String msgData = cont.get(MSG_DATA).toString();
        byte[] binMsgData = Helper.decodeMsgData(msgData);
        int hash = Integer.parseInt(cont.get(HASH).toString());
        String version = cont.get(VERSION_TAG).toString();
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
        //лишняя декодировка
        Container<Key> keyCont = new Container<>();
        AbstractMessageData decodedMsg = Decoder.getInstance().decode(binMsgData, version, hash, keyCont);
        if (decodedMsg == null){
            throw new Exception(Controller.BASE_APP_CONTEXT.getString(R.string.unknow_key));
        }
        Log.i("TAG", "readMessage: "+decodedMsg.toJSON().toString());
        Message res = new Message();
        res.setMessage(decodedMsg);
        res.setLogin(login);
        res.setGenDate(genDate);
        res.setData(binMsgData);
        //addMsgToChat(res, keyCont.getValue());
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

    public String generateBinaryDataForQR(InterractMessage imsg) throws Exception {
        JSONObject res = new JSONObject();
        res.put(LOGIN, imsg.getLogin());
        res.put(GEN_DATE, imsg.getGenDate().getTimeInMillis());
        //byte[] enc = Encoder.getInstance().encode(imsg.getMessageData().toJSONBytes(), cryptoKey(imsg.getKey()));
        res.put(MSG_DATA, Helper.encodeMsgData(imsg.getEncData()));
        res.put(HASH, imsg.getHash());
        res.put(VERSION_TAG, imsg.getVersion());
        return res.toString();
    }

    public byte[] jsonToBytes(JSONObject json){
        return json.toString().getBytes(java.nio.charset.Charset.forName("UTF8"));
    }


    public void scan(Context context, Bitmap bMap) {
        try {
            //https://stackoverflow.com/questions/29649673/scan-barcode-from-an-image-in-gallery-android
            String contents = null;

            int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
            //copy pixel data from the Bitmap into the 'intArray' arra
            bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

            LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Reader reader = new MultiFormatReader();
            Result result = reader.decode(bitmap);
            contents = result.getText();
            Toast.makeText(context, contents, Toast.LENGTH_LONG).show();
            readMessage(contents);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.error_on_scan_qr_code), Toast.LENGTH_LONG).show();
        }
    }
}
