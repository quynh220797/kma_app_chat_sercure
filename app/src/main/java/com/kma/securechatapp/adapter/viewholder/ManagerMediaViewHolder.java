package com.kma.securechatapp.adapter.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.ui.control.ImagePreview;
import com.kma.securechatapp.utils.common.EncryptFileLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagerMediaViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.imageView2)
    ImageView imageView;

    public ManagerMediaViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
    public void bind(MessagePlaneText msg){
        EncryptFileLoader.getInstance().loadEncryptImage(BuildConfig.HOST +msg.mesage,msg.password, imageView,null);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity.instance.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.HOST +msg.mesage)));
                Context context = ManagerMediaViewHolder.this.itemView.getContext();
                Intent intent = new Intent( context, ImagePreview.class);
                ImagePreview.bitmap  = ((BitmapDrawable)((ImageView)imageView).getDrawable()).getBitmap();
                intent.putExtra("url",BuildConfig.HOST +msg.mesage);
                intent.putExtra("key",msg.password);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity)context, (View)imageView, "imageView");

                context.startActivity(intent,options.toBundle());
            }
        });
    }
}
