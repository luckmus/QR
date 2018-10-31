package com.chatqr.bl.dao.model;

import com.chatqr.bl.dao.DAO;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class Chat implements IDialog<Message>, Serializable {
    private Long dbId;
    private Long idKey;
    private String name;
    private Calendar insDate;
    private Calendar changeDate;
    private Message lastMsg;

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public Long getIdKey() {
        return idKey;
    }

    public void setIdKey(Long idKey) {
        this.idKey = idKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getInsDate() {
        return insDate;
    }

    public void setInsDate(Calendar insDate) {
        this.insDate = insDate;
    }

    public Calendar getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Calendar changeDate) {
        this.changeDate = changeDate;
    }

    @Override
    public String getId() {
        return dbId.toString();
    }

    @Override
    public String getDialogPhoto() {
        return null;
    }

    @Override
    public String getDialogName() {
        return name;
    }

    @Override
    public List<? extends IUser> getUsers() {
        return DAO.getInstance().getUsers(dbId);
    }

    @Override
    public Message getLastMessage() {
        /*
        if (lastMsg == null){
            loadLastMsg();
        }
        return lastMsg;
        */
        Collection<com.chatqr.bl.dao.model.Message> msgs = DAO.getInstance().getMessages(dbId, 0,1);
        return   msgs.isEmpty()?null:msgs.iterator().next();
    }


    void loadLastMsg(){
        Collection<com.chatqr.bl.dao.model.Message> msgs = DAO.getInstance().getMessages(dbId, 0,1);
        lastMsg =  msgs.isEmpty()?null:msgs.iterator().next();
    }


    @Override
    public void setLastMessage(Message message) {

    }

    @Override
    public int getUnreadCount() {
        return 0;
    }
}
