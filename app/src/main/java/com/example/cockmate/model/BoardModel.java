package com.example.cockmate.model;

import java.util.HashMap;
import java.util.Map;

public class BoardModel {
    // 게시글 정보
    public String boardTitle;
    public String boardName;
    public String boardId;
    public String boardContent;
    public String boardImageUrl;
    public String boardCategory;
    public long boardDate;
    public int resourceId;
    public String boardEmail;
    public String boardRealDate;

    public BoardModel() {
    }



    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("Title", boardTitle);
        result.put("Content", boardContent);
        result.put("Id", boardId);
        result.put("Categoty", boardCategory);
        result.put("Date", boardDate);
        result.put("Name", boardName);
        result.put("Email", boardEmail);
        result.put("RealDate", boardRealDate);

        return result;
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getCategory() {
        return boardCategory;
    }

    public String getTitle() {
        return boardTitle;
    }

    public String getContent(){
        return boardContent;
    }

    public long getDate() {
        return boardDate;
    }

    public String getEmail() {return boardEmail;}

    public String getRealDate() {return boardRealDate;}

    public void setCategory(String category) {
        this.boardCategory = category;
    }

    public void setTitle(String title) {
        this.boardTitle = title;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public void setContent(String content) {
        this.boardContent = content;
    }

    public void setDate (long date) {
        this.boardDate = date;
    }

    public void setEmail (String email) {this.boardEmail = email;}

    public void setRealDate (String realDate) {this.boardRealDate = realDate;}
}
