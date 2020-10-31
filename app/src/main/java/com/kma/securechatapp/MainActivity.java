package com.kma.securechatapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.MessagePlaneText;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.api.model.UserKey;
import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.core.service.CacheService;
import com.kma.securechatapp.core.service.DataService;
import com.kma.securechatapp.core.service.RealtimeService;
import com.kma.securechatapp.core.service.RealtimeServiceConnection;
import com.kma.securechatapp.ui.about.AboutActivity;
import com.kma.securechatapp.ui.authentication.KeyPasswordActivity;
import com.kma.securechatapp.ui.authentication.LoginActivity;
import com.kma.securechatapp.ui.contact.ContactAddActivity;
import com.kma.securechatapp.ui.profile.SettingsActivity;
import com.kma.securechatapp.ui.profile.SharedPref;
import com.kma.securechatapp.ui.profile.UserProfileActivity;
import com.kma.securechatapp.utils.common.EncryptFileLoader;
import com.kma.securechatapp.utils.common.ImageLoader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance;
    ApiInterface api = ApiUtil.getChatApi();
    @BindView(R.id.left_nav)
    NavigationView navLeft;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    NavController navController;
    EventBus.EvenBusAction evenBus;
    @BindView(R.id.main_status)
    TextView tvMainStatus;

    SharedPref sharedPref;

    class NavigateHeaderBind {
        @BindView(R.id.h_user_name)
        TextView leftUserName;
        @BindView(R.id.h_user_status)
        TextView leftUserStatus;
        @BindView(R.id.h_avatar)
        ImageView leftUserAvatr;

        @OnClick(R.id.h_avatar)
        void onClickProfile(View view) {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            intent.putExtra("uuid", AppData.getInstance().currentUser.uuid);
            intent.setAction("view_profile");
            startActivity(intent);
        }
    }

    NavigateHeaderBind navHeaderBind = new NavigateHeaderBind(); //header Navigation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if(sharedPref.loadNightModeState() == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ButterKnife.bind(navHeaderBind, navLeft.getHeaderView(0));
        instance = this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ImageLoader.getInstance().bind(this);
        EncryptFileLoader.getInstance().bind(this);

        tvMainStatus.setText("Offline mode");
        tvMainStatus.setVisibility(View.GONE);

        //Run RealtimeSerVice
        Intent intent = new Intent(MainActivity.this, RealtimeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // startForegroundService(intent);
            startService(intent);
        } else {
            startService(intent);
        }

        //Tạo EvenBus
        register();

        AppData.getInstance().deviceId = Settings.Secure.getString(this.getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
        /*  if (BuildConfig.DEBUG){
            AppData.getInstance().setToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwMDAtMDAwLTAwMDAiLCJleHAiOjE1OTM2MzIwNTksImlhdCI6MTU3NTYzMjA1OX0.hdJd6_z9Bw37RXEj8EF_rQAi6OJQeYxl1ewm7iTTEwDi8GBcGjOD5UecuFRY--Xt_EAwglJBFRG3FDbcq56_aA");
            AppData.getInstance().currentUser = new UserInfo("000-000-0000","GN","",null);
        }*/

        //Có ACCESS_TOKEN_KEY
        if (DataService.getInstance(this).getToken() != null) {
            AppData.getInstance().setToken(DataService.getInstance(this).getToken());
            AppData.getInstance().setRefreshToken(DataService.getInstance(this).getRefreshtoken());
            AppData.getInstance().setUserUUID(DataService.getInstance(this).getUserUuid());
            AppData.getInstance().account = DataService.getInstance(this).getUserAccount();
        }

        RealtimeServiceConnection.getInstance().bindService(this);

        if (AppData.getInstance().getToken() == null) {
            Intent intent2 = new Intent(this, LoginActivity.class);
            startActivity(intent2);
        }else{
            DataService.getInstance(null).save();
            EventBus.getInstance().pushOnLogin(AppData.getInstance().currentUser);
        }
       //AUTO LOGIN

        /*Intent intent2 = new Intent(this, LoginActivity.class);
        startActivity(intent2)*/;

        DataService.getInstance(null).save();

        getSupportActionBar().hide();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_conversation, R.id.navigation_notifications)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        bindNavLeft();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            navController.navigate(R.id.navigation_dashboard);
        }
    }

    //xử lý even Bus
    void register() {
        evenBus = new EventBus.EvenBusAction() {
            @Override
            public void onConnectedSocket() { //ẩn text offline mode
                MainActivity.this.tvMainStatus.setVisibility(View.GONE);
            }

            @Override
            public void onDisconnectedSocket() { //hiện text offline mode
                MainActivity.this.tvMainStatus.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNetworkStateChange(int state) { //Check thay đổi trạng thái mạng
                Log.d("GIAYNHAP", "NET WORD STATE " + state);
            }

            @Override
            public void onChangeProfile() {//thay đổi profile
                bindLeftHeader();
            }

            @Override
            public void onLogin(UserInfo u) { // run realtimeservice,
                try {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RealtimeServiceConnection.getInstance().restart();
                        }
                    });
                    CacheService.getInstance().init(MainActivity.this, CacheService.getInstance().accountToDbName(AppData.getInstance().userUUID), AppData.getInstance().password);
                    if (AppData.getInstance().currentUser != null) {//có data về người dùng hiện tại
                        // online
                        //Lấy public key and private key
                        LoginActivity.showInputPass(MainActivity.this, api);
                    } else {
                        tvMainStatus.setVisibility(View.VISIBLE);
                        // offline login
                        AppData.getInstance().currentUser = CacheService.getInstance().getUser(AppData.getInstance().userUUID);
                        //restore user key
                        String privateKey = DataService.getInstance(MainActivity.this.getApplicationContext()).getPrivateKey(AppData.getInstance().userUUID, AppData.getInstance().password);
                        if (privateKey != null) {
                            UserKey userKey = new UserKey(null, null);
                            userKey.publicKey = AppData.getInstance().currentUser.publicKey;
                            userKey.privateKey = privateKey;
                            AppData.getInstance().userKey = userKey;
                            if (AppData.getInstance().getPrivateKey() == null) {
                                EventBus.getInstance().noticShow("Chưa thiết lập private key", "Lỗi đăng nhập");
                            }
                        }
                    }
                    CacheService.getInstance().saveUser(AppData.getInstance().currentUser, AppData.getInstance().account);
                    bindLeftHeader();
                    EventBus.getInstance().pushOnRefreshConversation();
                    EventBus.getInstance().pushOnRefreshContact();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLogout(UserInfo u) {
                AppData.getInstance().clean();
                DataService.getInstance(MainActivity.this).storeToken(null, null);
                DataService.getInstance(MainActivity.this).storeUserUuid(null);
                DataService.getInstance(MainActivity.this).save();
                RealtimeServiceConnection.getInstance().onlyDisconnectSocket();
                Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent2);
            }
        };
        EventBus.getInstance().addEvent(evenBus);
    }

    //menu left Header, avatar, text online
    void bindLeftHeader() {
        navHeaderBind.leftUserName.setText(AppData.getInstance().currentUser.name);
        navHeaderBind.leftUserStatus.setText("Online");
        ImageLoader.getInstance().DisplayImage(ImageLoader.getUserAvatarUrl(AppData.getInstance().currentUser.uuid, 200, 200), navHeaderBind.leftUserAvatr);
    }

    //Menu left
    void bindNavLeft() {
        navLeft.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Intent intent;
                        drawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.menu_item_logout:
                                logout();
                                break;
                            case R.id.menu_item_about:
                                intent = new Intent(MainActivity.this, AboutActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.menu_item_setting:
                                intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });
    }

    void logout() {
        EventBus.getInstance().pushOnLogout(AppData.getInstance().currentUser);
    }
}
