package com.kma.securechatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.viewholder.SearchMessageViewHolder;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.ui.conversation.InboxActivity;

import java.util.List;

public class SearchMessageAdapter extends RecyclerView.Adapter {
    List<MessagePlaneText> messagePlaneTextList;
    Context context;

    public SearchMessageAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_message, parent, false);
        return new SearchMessageViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchMessageViewHolder searchMessageViewHolder = (SearchMessageViewHolder) holder;
        MessagePlaneText messagePlaneText = this.messagePlaneTextList.get(position);
        searchMessageViewHolder.setAvatar(BuildConfig.HOST + "users/avatar/" + messagePlaneText.sender.uuid + "?width=" + 80 + "&height=" + 80);
        searchMessageViewHolder.setMessage(messagePlaneText.mesage);
        searchMessageViewHolder.setTitle(messagePlaneText.sender.name);
        searchMessageViewHolder.setTime(messagePlaneText.time);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mở lại InboxActivity với tham số uuid của tin nhắn truyeefn vào
                //phía activity sẽ scroll to đến tin nhắn đó
                Intent intent = new Intent(context, InboxActivity.class);
                intent.putExtra("uuid",messagePlaneText.threadUuid);
                intent.putExtra("idmess",messagePlaneText.uuid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (messagePlaneTextList != null) {
            return messagePlaneTextList.size();
        }
        return 0;
    }

    public List<MessagePlaneText> getMessageList() {
        return messagePlaneTextList;
    }

    public void setMessagePlaneTextList(List<MessagePlaneText> messageList) {
        messagePlaneTextList = messageList;
    }

}

