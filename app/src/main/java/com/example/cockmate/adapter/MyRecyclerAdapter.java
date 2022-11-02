package com.example.cockmate.adapter;

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

import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private static final String TAG = "BoardAdapter";
    private ArrayList<BoardModel> mBoardModel;

    /*
    public MyRecyclerAdapter(ArrayList<BoardModel> mBoardModel) {
        this.mBoardModel = mBoardModel;
    }

     */

    // 어떤 레이아웃과 연결해야되는지 설정하고 view를 만듦
    @NonNull
    @Override
    public MyRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    // 받아온 데이터를 item_layout에 set해주기
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerAdapter.ViewHolder holder, int position){
        Log.d(TAG, "onBindViewHolder: "+position);
        holder.onBind(mBoardModel.get(position));
    }

    public void setItemList(ArrayList<BoardModel> list){
        this.mBoardModel = list;
        notifyDataSetChanged();
    }

    // 아이템 카운트
    @Override
    public int getItemCount(){
        Log.d(TAG, "getItemCount: ");
        return mBoardModel.size();
    }

    // item_layout의 데이터를 초기화해주는 클래스
    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView itemImage;
        TextView itemTitle;
        TextView itemCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = (ImageView) itemView.findViewById(R.id.item_image);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemCategory = (TextView) itemView.findViewById(R.id.item_category);
        }

        void onBind(BoardModel item){
            Log.d(TAG, "MyViewHolder: ");
            itemImage.setImageResource(item.getResourceId());
            itemTitle.setText(item.getTitle());
            itemCategory.setText(item.getCategory());
        }


    }


}
