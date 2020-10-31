package com.kma.securechatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.viewholder.ManagerMediaViewHolder;
import com.kma.securechatapp.adapter.viewholder.MessageSenderViewHolder;
import com.kma.securechatapp.core.api.model.MessagePlaneText;

import java.util.List;

public class ManagerMediaAdapter extends RecyclerView.Adapter {
    List<MessagePlaneText> listmessage;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_image, parent, false);
        return new ManagerMediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ManagerMediaViewHolder managerMediaViewHolder = (ManagerMediaViewHolder) holder;
        MessagePlaneText messagePlaneText = this.listmessage.get(position);
        managerMediaViewHolder.bind(messagePlaneText);
    }

    @Override
    public int getItemCount() {
        if(listmessage==null)
            return 0;
        return  listmessage.size();
    }
    public void setListmessage(List<MessagePlaneText> list){
        this.listmessage=list;
    }
}

