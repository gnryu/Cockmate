package com.example.cockmate.model;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
    // 사용자 기본정보
    public String userName;
    public String userEmail;
    public String userId;

    public UserModel(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }

    public UserModel(String name, String email, String id){
        this.userName = name;
        this.userEmail = email;
        this.userId = id;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("Name", userName);
        result.put("Email", userEmail);
        result.put("Id", userId);

        return result;
    }
}
