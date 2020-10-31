package com.kma.securechatapp.ui.conversation.Inbox.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.ManagerMediaAdapter;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.realm_model.RMessage;
import com.kma.securechatapp.core.service.CacheService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

public class MangerMediaActivity extends AppCompatActivity {
    @BindView(R.id.rv_manager_media)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar3)
    Toolbar toolbar;
    List<MessagePlaneText> messagePlaneTextList;
    RealmResults<RMessage> rMessages;
    String uuid;// conversation id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manger_media);
        ButterKnife.bind(this);
        ManagerMediaAdapter managerMediaAdapter = new ManagerMediaAdapter();
        recyclerView.setAdapter(managerMediaAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        messagePlaneTextList = new ArrayList<>();
        Intent intent = getIntent();
        uuid= intent.getStringExtra("conid");
        getListmessage(uuid);
        managerMediaAdapter.setListmessage(messagePlaneTextList);
        //managerMediaAdapter.notifyDataSetChanged();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        this.getSupportActionBar().setTitle("Đã chia sẻ");
    }
    public void getListmessage (String uuid){
        rMessages = CacheService.getInstance().queryMessImage(uuid);
        //convert sang MessagePlanText
        for (RMessage msg : rMessages) {
            MessagePlaneText plaintText = msg.toModel();
            messagePlaneTextList.add(plaintText);
        }
    }
}

