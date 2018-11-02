package com.chatqr.ui.features.demo.def;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chatqr.Controller;
import com.chatqr.R;
import com.chatqr.bl.ChatController;
import com.chatqr.bl.QRCodeHelper;
import com.chatqr.bl.dao.DAO;
import com.chatqr.bl.dao.model.Chat;
import com.chatqr.bl.dao.model.InterractMessage;
import com.chatqr.bl.dao.model.Key;
import com.chatqr.bl.dao.model.Message;
import com.chatqr.bl.fixtures.MessagesFixtures;
import com.chatqr.ui.features.demo.DemoMessagesActivity;
import com.chatqr.utils.AppUtils;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;


public class DefaultMessagesActivity extends DemoMessagesActivity
        implements MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener {
    public static final String CHAT_EXT = "chat";

    public static void open(Context context, Chat chat) {
        Intent intent = new Intent(context, DefaultMessagesActivity.class);
        intent.putExtra(CHAT_EXT, chat);
        context.startActivity(intent);
    }

    private MessagesList messagesList;
    private Chat chat;
    private Key key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chat = (Chat) getIntent().getSerializableExtra(CHAT_EXT);
        key = DAO.getInstance().getKey(chat.getIdKey());
        setContentView(R.layout.activity_default_messages);

        this.messagesList = (MessagesList) findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setTypingListener(this);
        input.setAttachmentsListener(this);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        /*
        super.messagesAdapter.addToStart(
                MessagesFixtures.getTextMessage(input.toString()),
                true);
                */
        Toast.makeText(this,input, Toast.LENGTH_SHORT).show();
        try {
            Message message = ChatController.generateTextMessageAndSave(input.toString(), "utf8", chat);
            addToStart(message);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onAddAttachments() {
        /*
        super.messagesAdapter.addToStart(
                MessagesFixtures.getImageMessage(), true);
                */
    }

    private void initAdapter() {
        super.messagesAdapter = new MessagesListAdapter<>(super.senderId, super.imageLoader);
        super.messagesAdapter.enableSelectionMode(this);
        super.messagesAdapter.setLoadMoreListener(this);
        super.messagesAdapter.registerViewClickListener(R.id.messageUserAvatar,
                new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @Override
                    public void onMessageViewClick(View view, Message message) {
                        AppUtils.showToast(DefaultMessagesActivity.this,
                                message.getUser().getName() + " avatar click",
                                false);
                    }
                });
        this.messagesList.setAdapter(super.messagesAdapter);
        super.messagesAdapter.setOnMessageClickListener(new MessagesListAdapter.OnMessageClickListener<Message>() {
            @Override
            public void onMessageClick(Message message) {
                AppUtils.showToast(DefaultMessagesActivity.this,
                        "dbId: "+message.getDbId(),
                        false);
                Controller.getInstance().export(DefaultMessagesActivity.this, message, key);
            }
        });
    }

    @Override
    public void onStartTyping() {
        Log.v("Typing listener", getString(R.string.start_typing_status));
    }

    @Override
    public void onStopTyping() {
        Log.v("Typing listener", getString(R.string.stop_typing_status));
    }

    @Override
    public void onBackPressed() {
        Controller.getInstance().navigateToCurrentMenu(this);
    }
}
