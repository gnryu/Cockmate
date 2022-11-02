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
    public String boardDate;
    public int resourceId;

    public BoardModel( String title, String category, String content, String date, String name) {
        this.boardTitle = title;
        this.boardCategory = category;
        this.boardContent = content;
        this.boardDate = date;
        this.boardName = name;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("Title", boardTitle);
        result.put("Content", boardContent);
        result.put("Id", boardId);
        result.put("Categoty", boardCategory);
        result.put("Date", boardDate);
        result.put("Name", boardName);

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

    public String getDate() {
        return boardDate;
    }

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

    public void setDate (String date) {
        this.boardDate = date;
    }
}
