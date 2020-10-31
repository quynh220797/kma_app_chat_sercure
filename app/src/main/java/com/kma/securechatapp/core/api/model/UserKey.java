package com.kma.securechatapp.core.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserKey implements Serializable {//cơ chế tuần tự trong Java, chuyển 1 đối tượng thành định dạng có thể lưu được, sau đó có thể phục hồi để sử dụng
    @SerializedName("uuid")
    public String UUID;
    @SerializedName("publickey")
    public String publicKey;
    @SerializedName("privatekey")
    public  String privateKey ;

    public UserKey(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}
