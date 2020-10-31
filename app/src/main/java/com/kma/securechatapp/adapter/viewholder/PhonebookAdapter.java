package com.kma.securechatapp.adapter.viewholder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.Contact;
import com.kma.securechatapp.core.api.model.Phonebook;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.utils.common.ImageLoader;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhonebookAdapter extends RecyclerView.Adapter<PhonebookAdapter.ViewHolder>  {
    private ArrayList<Phonebook> listPhonebook;
    ApiInterface api = ApiUtil.getChatApi();
    Context context;
    private MutableLiveData<Boolean> hasContact = new MutableLiveData<Boolean>();
    public PhonebookAdapter(Context context, ArrayList<Phonebook> listPhonebook){
        this.context = context;
        this.listPhonebook = listPhonebook;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //gan view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phonebook_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //gan du lieu
        int state ;
        Phonebook phonebook = listPhonebook.get(position);
        holder.name.setText(phonebook.getName());
        holder.appName.setText(phonebook.getAppName());
        state = phonebook.getState();
        ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl(phonebook.getUrlAvatar(),50,50), holder.phonebookAvatar);
        if (state==1){
            holder.addButton.setVisibility(View.GONE);
            holder.added.setVisibility(View.VISIBLE);
        }
        else{
           holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Click","btn");
                //holder.addButton.setBackgroundColor(Integer.parseInt("#FFFFFF"));
                addContact(phonebook.getUrlAvatar());
                holder.addButton.setVisibility(View.GONE);
                holder.added.setVisibility(View.VISIBLE);
            }
        });
        }
    }
    void addContact(String uuid) {
        api.addContact(new Contact(uuid, 0, null)).enqueue(new Callback<ApiResponse<UserInfo>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserInfo>> call, Response<ApiResponse<UserInfo>> response) {
                trigerCheckHasContact(uuid);
            }
            @Override
            public void onFailure(Call<ApiResponse<UserInfo>> call, Throwable t) {
            }
        });
    }
    public void trigerCheckHasContact(String uuid) {
        api.existContact(uuid).enqueue(new Callback<ApiResponse<Contact>>() {
            @Override
            public void onResponse(Call<ApiResponse<Contact>> call, Response<ApiResponse<Contact>> response) {
                if (response.body() == null || response.body().error != 0 || response.body().data == null) {
                    hasContact.setValue(false);
                } else {
                    hasContact.setValue(true);
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Contact>> call, Throwable t) {
                hasContact.setValue(false);
            }
        });
    }
    @Override
    public int getItemCount() {
        return listPhonebook.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder { // khai bao cac thanh phan giao dien
        TextView name;
        TextView appName;
        ImageView phonebookAvatar;
        Button addButton;
        TextView added;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //anh xa view
            name = itemView.findViewById(R.id.phonebook_item_name);
            appName = itemView.findViewById(R.id.phonebook_app_name);
            phonebookAvatar = itemView.findViewById(R.id.phonebook_item_avatar);
            addButton = itemView.findViewById(R.id.phonebook_state);
            added = itemView.findViewById(R.id.tvAdded);
        }
    }
}
