package com.kma.securechatapp.adapter.viewholder;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.utils.common.ImageLoader;
import com.kma.securechatapp.utils.common.StringHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchMessageViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.search_text_message)
    TextView txtMessage;
    @BindView(R.id.search_text_title)
    TextView txtTitle;
    @BindView(R.id.search_text_time)
    TextView txtTime;
    @BindView(R.id.search_img_avatar)
    ImageView imgAvatar;

    public SearchMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setAvatar(String url) {
        if (url != null)
            ImageLoader.getInstance().DisplayImage(url, imgAvatar);
    }

    public void setTitle(String title) {
        this.txtTitle.setText(title);
    }

    public void setMessage(String message) {
        if (message == null) {
            message = "";
        }
        this.txtMessage.setText(message);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setTime(Long time) {
        if (time == null)
            return;
        this.txtTime.setText(StringHelper.getLongTextChat(time));
    }
}
