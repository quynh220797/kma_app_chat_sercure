package com.kma.securechatapp.ui.conversation.Inbox.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.MessageAdapter;
import com.kma.securechatapp.adapter.SearchMessageAdapter;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.realm_model.RMessage;
import com.kma.securechatapp.core.security.RSAUtil;
import com.kma.securechatapp.core.security.SecureChatSystem;
import com.kma.securechatapp.core.service.CacheService;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMessageActivity extends AppCompatActivity {
    @BindView(R.id.search_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.editTextTextMultiLine)
    EditText editText;
    @BindView(R.id.imageButton)
    ImageButton imageButton;
    @BindView(R.id.toolbar2)
    Toolbar toolbar;
    SearchMessageAdapter searchMessageAdapter;
    Conversation conversation;
    String uuid;//conversationid;
    public byte[] key;
    List<MessagePlaneText> listmess;//danh sách tin nhắn của cuộc hội thoại
    List<MessagePlaneText> list;//các tin nhắn sau khi tìm kiếm
    RealmResults<RMessage> rMessages;
    ApiInterface api = ApiUtil.getChatApi();
    List<UserInfo> userInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_message);
        ButterKnife.bind(this);
        searchMessageAdapter = new SearchMessageAdapter(this);
        recyclerView.setAdapter(searchMessageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        searchMessageAdapter.setMessagePlaneTextList(list);

        Intent intent = getIntent();
        uuid = intent.getStringExtra("IDconversation");
        if (uuid == null) {
            onBackPressed();
        }

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        SearchMessageActivity.this.getSupportActionBar().setTitle("Search Message");
        //Tìm kiếm tin nhắn
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                fillerMessage(content);
                searchMessageAdapter.notifyDataSetChanged();
            }
        });

        //lấy hội thoại
        try {
            getConversation(uuid);
        } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        //lấy danh sách tin nhắn của hội thoại
        getListMessage(uuid);
    }

    public void getConversation(String uuid) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        conversation = CacheService.getInstance().getConversationInfo(uuid);
        String ukey = conversation.getKey(AppData.getInstance().currentUser.uuid);
        key = RSAUtil.RSADecryptBuffer(RSAUtil.base64Decode(ukey), AppData.getInstance().getPrivateKey());
    }

    public void getListMessage(String uuid) {
        //lấy ra danh sách tin nhắn R
        rMessages = CacheService.getInstance().queryMessageS(uuid);
        //convert sang MessagePlanText
        listmess = new ArrayList<>();
        for (RMessage msg : rMessages) {
            MessagePlaneText plaintText = msg.toModel();
            listmess.add(plaintText);
        }
    }

    public void fillerMessage(String text) {
        list.clear();
        for (MessagePlaneText mess : listmess) {
            if (mess.mesage.contains(text)) {
                list.add(mess);
            }
        }
    }
}