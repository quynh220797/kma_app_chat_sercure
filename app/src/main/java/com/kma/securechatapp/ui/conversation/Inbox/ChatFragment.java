package com.kma.securechatapp.ui.conversation.Inbox;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.MessageAdapter;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.ui.conversation.InboxActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class ChatFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public interface ChatUiEvent {
        //load lại danh sách các tin nhắn (kéo từ trên xuống)
        public void loadedMessages(List<MessagePlaneText> messages);

        //nhận tin nhắn mới hoặc gửi đi tin nhắn
        public void revcNewMessage(MessagePlaneText message);
    }

    protected ChatUiEvent event;
    @BindView(R.id.load_more)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.reyclerview_message_list)
    public RecyclerView recyclerView;
    MessageAdapter messageAdapter = new MessageAdapter();
    String uuid;

    @Override
    public void onRefresh() {
        ((InboxActivity) this.getActivity()).onRefresh();
    }

    public ChatFragment.ChatUiEvent uiEvent = new ChatFragment.ChatUiEvent() {
        @Override
        public void loadedMessages(List<MessagePlaneText> messages) {
            refreshLayout.setRefreshing(false);
            messageAdapter.setMessages(messages);
            messageAdapter.notifyDataSetChanged();
        }

        @Override
        public void revcNewMessage(MessagePlaneText message) {
            messageAdapter.addNewMessage(message);
            messageAdapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, root);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        refreshLayout.setOnRefreshListener(this);

        /*Bundle bundle = getArguments();
        uuid=bundle.getString("uuid");
        if(uuid!=null){
            recyclerView.scrollToPosition(10);
        }*/
        return root;
    }

    //when Activity create, ping uiEvent.
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((InboxActivity) this.getActivity()).uiEvent = this.uiEvent;
    }

    //click vào list tin nhắn, hủy bỏ target
    @OnTouch(R.id.reyclerview_message_list)
    boolean onClickReclerView(View cornerPoint, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                try {
                    this.getActivity().getCurrentFocus().clearFocus();
                } catch (Exception e) {
                }
                break;
        }
        return false;
    }
    public void getidmess(){
        Bundle bundle = getArguments();
        if (bundle != null) {
            uuid = bundle.getString("uuid");
        }
        if (uuid == null) {
            //Log.d("ABCD", "null");
        } else {
            //Log.d("ABCD", uuid);
        }
        if (uuid != "abcdef") {
            //this.recyclerView.scrollToPosition(10);
            //recyclerView.scrollToPosition(10);
        }
    }
}
