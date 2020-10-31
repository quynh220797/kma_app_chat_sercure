package com.kma.securechatapp.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.adapter.viewholder.MessageReceivederViewHolder;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.viewholder.MessageSenderViewHolder;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.utils.common.EncryptFileLoader;
import com.kma.securechatapp.utils.common.StringHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {
    public List<MessagePlaneText> messages;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                return new MessageSenderViewHolder(view);
            case 10:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_image_sent, parent, false);
                return new MessageSenderViewHolder(view);
            case 20:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_audio_sent, parent, false);
                return new MessageSenderViewHolder(view);
            case 30:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_sticker_sent, parent, false);
                return new MessageSenderViewHolder(view);
            case 11:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_image_received, parent, false);
                return new MessageReceivederViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new MessageReceivederViewHolder(view);
            case 21:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_audio_received, parent, false);
                return new MessageReceivederViewHolder(view);
            case 31:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_sticker_received, parent, false);
                return new MessageReceivederViewHolder(view);
        }
        return null;
    }

    //Get type of Message (text, image,...)
    @Override
    public int getItemViewType(int position) {
        MessagePlaneText message = (MessagePlaneText) messages.get(position);
        if (message == null) {
            return 0;
        }
        if (message.senderUuid.equals(AppData.getInstance().currentUser.uuid)) {
            // If the current user is the sender of the message
            if (message.type == 0)
                return 0;
            else if (message.type == 1)
                return 10;
            else if (message.type == 2) {
                return 20;
            } else if (message.type == 3) {
                return 30;
            }
        } else {
            // If some other user sent the message
            if (message.type == 0) //text
                return 1;
            else if (message.type == 1) //image
                return 11;
            else if (message.type == 2) { //audio
                return 21;
            } else if (message.type == 3) { //sticker
                return 31;
            }
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        boolean hide = false;//ẩn icon (avatar) đi
        boolean showTime = false;
        if (position < messages.size() - 1) {//position hợp lệ
            MessagePlaneText msg1 = messages.get(position);
            MessagePlaneText msg2 = messages.get(position + 1);
            //Phân tách các tin nhắn theo ngày.
            if (msg1.senderUuid.equals(msg2.senderUuid) && StringHelper.checkSameDay(msg1.time, msg2.time)) {
                hide = true;
            }
            if (!StringHelper.checkSameDay(msg1.time, msg2.time)) {
                showTime = true;
            }
        } else {
            showTime = true;
        }
        //là người nhận tin nhắn
        if (holder.getItemViewType() == 1 || holder.getItemViewType() == 11 || holder.getItemViewType() == 21 || holder.getItemViewType() == 31) {
            ((MessageReceivederViewHolder) holder).bind(messages.get(position), hide);
            if (showTime)
                ((MessageReceivederViewHolder) holder).showLongTime();
        }
        //là người gửi tin nhắn
        else {
            ((MessageSenderViewHolder) holder).bind(messages.get(position), hide);
            if (showTime)
                ((MessageSenderViewHolder) holder).showLongTime();
        }
    }

    @Override
    public int getItemCount() {
        if (messages == null) {
            return 0;
        }
        return messages.size();
    }

    public List<MessagePlaneText> getMessages() {
        return messages;
    }

    //set list massages
    public void setMessages(List<MessagePlaneText> messages) {
        this.messages = messages;
    }

    //add 1 massages lên đầu list messages
    public void addNewMessage(MessagePlaneText msg) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        this.messages.add(0, msg);
        //add image to phone
        if (msg.type == 1)
            EncryptFileLoader.getInstance().storeImage(BuildConfig.HOST + msg.mesage, msg.password);
    }

    public int getposition(String uuid) {
        if (messages != null) {
            for (int i = 0; i < messages.size(); i++) {
                MessagePlaneText msg = messages.get(i);
                if (msg.uuid == uuid) return i;
            }
        }
        return 0;
    }
}
