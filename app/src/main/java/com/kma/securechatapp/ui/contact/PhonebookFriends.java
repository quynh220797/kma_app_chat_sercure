package com.kma.securechatapp.ui.contact;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.viewholder.PhonebookAdapter;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.ApiResponse;
import com.kma.securechatapp.core.api.model.Contact;
import com.kma.securechatapp.core.api.model.PageResponse;
import com.kma.securechatapp.core.api.model.Phonebook;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.utils.misc.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhonebookFriends extends AppCompatActivity {
    ApiInterface api = ApiUtil.getChatApi();
    List<UserInfo> listUser;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    RecyclerView phonebookRecycler;
    PhonebookAdapter phonebookAdapter;
    ArrayList<Phonebook> listPhonebook ;
    RecyclerView.LayoutManager layoutManager;
    Button addButton;
    String appNameSearch="";
    List<Contact> listContact;
    int state =0;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook_friends);

        /*check permission*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                getContacts();
            }
        } else {
            getContacts();
        }
        phonebookRecycler = findViewById(R.id.phonebook_recyclerview);
        listPhonebook = new ArrayList<>();
        // sử dụng câu lệnh này nếu kích thước các hàng luôn
        // bằng nhau. việc này giúp list mượt hơn
        phonebookRecycler.setHasFixedSize(true);
        // sử dụng LayoutManager để quy định kiểu hiển thị cho list là hàng dọc
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        layoutManager = new LinearLayoutManager(this);
        phonebookRecycler.setLayoutManager(layoutManager);
        phonebookAdapter = new PhonebookAdapter(getApplicationContext(), listPhonebook);
        phonebookRecycler.setAdapter(phonebookAdapter);
        phonebookAdapter.notifyDataSetChanged();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    private void getContacts(){
        ContentResolver contentResolver = getContentResolver();//
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if((cursor!=null ? cursor.getCount():0)>0){
            while (cursor !=null && cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String phoneName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))>0){
                    Cursor pCur = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id},null);
                    while (pCur.moveToNext()){
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        //convert phonenumber to 84
                        if(phoneNo.charAt(0)!='8'){
                            phoneNo =  phoneNo.substring(1);
                            phoneNo = "84".concat(phoneNo);
                        }
                        Log.d("PhoneOrigin",phoneNo);
                        checkPhone(phoneNo, phoneName);
                       }
                    }
                }
            }
            if (cursor!=null){
                cursor.close();
            }
        }

    public void checkPhone(String phone, String phoneName){
        appNameSearch = "";
        this.api.getSuggestList().enqueue(new Callback<ApiResponse<List<UserInfo>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<UserInfo>>> call, Response<ApiResponse<List<UserInfo>>> response) {
                listUser = response.body().data;
                //Log.d("listSize",Integer.toString(response.body().data.size()));
                int size = listUser.size() -1;

                while(size>=0){//tim kiem trong danh sach nguoi dung
                    if(listUser.get(size).phone.equals(phone)){ //neu user ton tai
                        appNameSearch  = listUser.get(size).name; //so sanh ten cua user da ton tai voi list contact
                        String id = listUser.get(size).uuid;
                        searchInContact(appNameSearch, id, phoneName);
                        break;
                    }
                    size--;
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<UserInfo>>> call, Throwable t) {
            }
        });
    }
    public void searchInContact(String appName, String id, String phoneName){
        this.api.pageContact(0).enqueue(new Callback<ApiResponse<PageResponse<Contact>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<Contact>>> call, Response<ApiResponse<PageResponse<Contact>>> response) {
                state =0;
                listContact = response.body().data.content;
                int size = listContact.size() -1;
                Log.d("Namecheck", appName);
                while(size >=0){
                    if(listContact.get(size).contactName.equals(appName)){
                        state =1;
                        break;
                    }
                    size--;
                }
                if(state == 1){
                    listPhonebook.add(new Phonebook(phoneName,  appName , id, 1));
                    phonebookAdapter.notifyDataSetChanged();
                }
                else {
                    listPhonebook.add(new Phonebook(phoneName, appName, id, 0));
                    phonebookAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<Contact>>> call, Throwable t) {
        }
        });

    }

}