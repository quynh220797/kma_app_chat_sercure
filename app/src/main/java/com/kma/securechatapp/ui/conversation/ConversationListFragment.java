package com.kma.securechatapp.ui.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.ConversationAdapter;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.core.service.CacheService;
import com.kma.securechatapp.ui.control.suggestview.OnlineView;
import com.kma.securechatapp.ui.control.suggestview.SuggestView;
import com.kma.securechatapp.ui.conversation.Inbox.ChatFragment;
import com.kma.securechatapp.utils.misc.EndlessRecyclerOnScrollListener;
import com.kma.securechatapp.utils.misc.RecyclerItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class ConversationListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private ConversationListViewModel conversationListViewModel;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    ConversationAdapter conversationAdapter = new ConversationAdapter();
    @BindView(R.id.conversation_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.conversation_swipcontainer)
    SwipeRefreshLayout swipeRefreshLayout;

    EventBus.EvenBusAction evenBus;
    OnlineView onlineView;//danh sách kiến nghị những người bạn có thể biết ở tag dashboard

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //get instance ViewModel of ...
        conversationListViewModel = ViewModelProviders.of(this).get(ConversationListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_conversation, container, false);
        ButterKnife.bind(this, root);

        onlineView = new OnlineView(this.getActivity(), root.findViewById(R.id.online_view));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setAdapter(conversationAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);

        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);//cuộn
        swipeRefreshLayout.setOnRefreshListener(this);//làm mới
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_dark, android.R.color.holo_orange_dark, android.R.color.holo_blue_dark);

        //run luc click vao tab conversation
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                conversationListViewModel.trigerLoadData(0); //lấy Conversation từ server sau đó add vào REALM
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //mở tin nhắn khi click vào 1 hội thoại
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this.getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position == -1) position++;
                        Conversation conversation = conversationAdapter.getConversationList().get(position);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent1 = new Intent(ConversationListFragment.this.getContext(), InboxActivity.class);
                                intent1.putExtra("uuid", conversation.UUID);
                                startActivity(intent1);
                            }
                        },500);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );

        //listen change conversation
        conversationListViewModel.getConversations().observe(this, conversations -> {
            onRefresh();
            conversationAdapter.setConversationList(conversations);
            conversationAdapter.notifyDataSetChanged();
        });

        conversationListViewModel.getListOnline().observe(this, users -> {
            onlineView.upDateList(users);
        });

        //lấy danh sách các liên lạc đang ONLINE
        conversationListViewModel.trigerLoadOnline();
        registerEvent();
        return root;
    }

    //xử lý sự kiện mỗi khi làm mới danh sách Conversation
    void registerEvent() {
        evenBus = new EventBus.EvenBusAction() {
            @Override
            public void onRefreshConversation() {
                conversationListViewModel.trigerLoadData(0);////lấy Conversation từ server sau đó add vào REALM
                conversationListViewModel.trigerLoadOnline();////lấy danh sách các liên lạc đang ONLINE từ SERVER
            }
        };
        EventBus.getInstance().addEvent(evenBus);
        conversationListViewModel.registEvent();//Lấy Conversation ra từ REALMDB
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                conversationListViewModel.trigerLoadOnline();
                conversationListViewModel.trigerLoadData(0);
                swipeRefreshLayout.setRefreshing(false);
            }
        },1000);
    }

    @Override
    public void onDetach() {
        if (evenBus != null) ;
        EventBus.getInstance().removeEvent(evenBus);
        super.onDetach();
    }
}