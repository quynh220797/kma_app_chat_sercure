package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

//1 người dùng tham gia vào Conversation
public class UserConversation {
    @SerializedName("userUuid")
    public String userUuid; //mã user
    @SerializedName("key")
    public String key;
    @SerializedName("lastSeen")
    public Long lastSeen;
}
