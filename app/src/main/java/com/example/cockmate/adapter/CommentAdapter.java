package com.example.cockmate.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cockmate.R;
import com.example.cockmate.model.BoardModel;
import com.example.cockmate.model.CommentModel;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private static final String TAG = "CommentAdapter";
    private ArrayList<CommentModel> mCommentModel;
    private Context context;

    public CommentAdapter(Context context, ArrayList<CommentModel> mCommentModel) {
        this.context = context;
        this.mCommentModel = mCommentModel;

    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        CommentModel commentModel = mCommentModel.get(position);

        holder.commentName.setText(commentModel.commentUserName);
        holder.commentContent.setText(commentModel.commentContent);
        holder.commentRealDate.setText(commentModel.commentRealDate);


    }

    public void setItemList(ArrayList<CommentModel> list){
        this.mCommentModel = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "댓글 수 " + mCommentModel.size());
        return mCommentModel.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView commentName;
        TextView commentContent;
        TextView commentRealDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            commentName = (TextView) itemView.findViewById(R.id.comment_name);
            commentContent = (TextView) itemView.findViewById(R.id.comment_content);
            commentRealDate = (TextView) itemView.findViewById(R.id.comment_date);
        }

    }
}
