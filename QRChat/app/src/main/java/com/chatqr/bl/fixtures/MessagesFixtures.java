package com.chatqr.bl.fixtures;

import com.chatqr.bl.dao.DAO;
import com.chatqr.bl.dao.modelDemo.Message;
import com.chatqr.bl.dao.modelDemo.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/*
 * Created by troy379 on 12.12.16.
 */
public final class MessagesFixtures extends FixturesData {
    private MessagesFixtures() {
        throw new AssertionError();
    }

    public static Message getImageMessage() {
        Message message = new Message(FixturesData.getRandomId(), getUser(), null);
        message.setImage(new Message.Image(getRandomImage()));
        return message;
    }

    public static Message getVoiceMessage() {
        Message message = new Message(getRandomId(), getUser(), null);
        message.setVoice(new Message.Voice("http://example.com", rnd.nextInt(200) + 30));
        return message;
    }

    public static Message getTextMessage() {
        return getTextMessage(getRandomMessage());
    }

    public static Message getTextMessage(String text) {
        return new Message(getRandomId(), getUser(), text);
    }

    public static Collection<com.chatqr.bl.dao.model.Message> getMessages(long chatId, Date startDate) {
        ArrayList<com.chatqr.bl.dao.model.Message> messages = new ArrayList<>();
        messages.addAll(DAO.getInstance().getMessages(chatId, startDate));
        return messages;
    }

    private static User getUser() {
        boolean even = rnd.nextBoolean();
        return new User(
                even ? "0" : "1",
                even ? names.get(0) : names.get(1),
                even ? avatars.get(0) : avatars.get(1),
                true);
    }
}
