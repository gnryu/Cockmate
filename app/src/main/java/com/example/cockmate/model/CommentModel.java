package com.example.cockmate.model;

import java.util.HashMap;
import java.util.Map;

public class CommentModel {

    // 댓글 정보
    public String commentBoardId;
    public String commentUserName;
    public String commentContent;
    public long commentDate;
    public String commentRealDate;

    public CommentModel() {
    }

    public CommentModel(String id, String name, String content, long date, String realDate){
        this.commentBoardId = id;
        this.commentUserName = name;
        this.commentContent = content;
        this.commentDate = date;
        this.commentRealDate = realDate;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("CommentBoardId", commentBoardId);
        result.put("CommentUserName", commentUserName);
        result.put("CommentContent", commentContent);
        result.put("CommentDate", commentDate);
        result.put("CommentRealDate", commentRealDate);

        return result;
    }

    public void setCommentBoardId(String id) {this.commentBoardId = id;}
    public void setCommentUserName(String name) {this.commentUserName = name;}
    public void setCommentContent(String content) {this.commentContent = content;}
    public void setCommentDate(long date) {this.commentDate = date;}
    public void setCommentRealDate(String realDate) {this.commentRealDate = realDate;}

    public String getCommentBoardId() {return commentBoardId;}
    public String getCommentUserName() {return commentUserName;}
    public String getCommentContent() {return commentContent;}
    public long getCommentDate() {return commentDate;}
    public String getCommentRealDate() {return commentRealDate;}
}
