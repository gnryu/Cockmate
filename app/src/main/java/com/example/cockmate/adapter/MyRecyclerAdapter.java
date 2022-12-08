package com.example.cockmate.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cockmate.R;
import com.example.cockmate.model.BoardModel;
import com.example.cockmate.util.DetailActivity;

import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private static final String TAG = "BoardAdapter";
    private ArrayList<BoardModel> mBoardModel;
    private Context context;


    public MyRecyclerAdapter(Context context, ArrayList<BoardModel> mBoardModel) {
        this.context = context;
        this.mBoardModel = mBoardModel;

    }



    // 어떤 레이아웃과 연결해야되는지 설정하고 view를 만듦
    @NonNull
    @Override
    public MyRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        //Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    // 받아온 데이터를 item_layout에 set해주기
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerAdapter.ViewHolder holder, int position){
        BoardModel boardModel = mBoardModel.get(position);

        //holder.onBind(boardModel);

        holder.itemTitle.setText(boardModel.boardTitle);
        holder.itemCategory.setText(boardModel.boardCategory);
        holder.itemDate.setText(boardModel.boardRealDate);
        Glide.with(holder.itemView).load(boardModel.getResourceId()).into(holder.itemImage);
        //Log.d(TAG, "onBindViewHolder: "+position);
        //holder.onBind(mBoardModel.get(position));

        // item 클릭할 때 마다 해당 item의 정보 가져오기
        String myBoardTitle = boardModel.boardTitle;
        String myBoardContent = boardModel.boardContent;
        String myBoardCategory = boardModel.boardCategory;
        String myBoardRealDate = boardModel.boardRealDate;
        String myBoardName = boardModel.boardName;

        // 위 정보를 순서대로 array에 저장하기
        ArrayList<String> boardInfo = new ArrayList<>();
        boardInfo.add(myBoardTitle);
        boardInfo.add(myBoardContent);
        boardInfo.add(myBoardCategory);
        boardInfo.add(myBoardRealDate);
        boardInfo.add(myBoardName);


        // Detail Activity에 넘겨주기
        //BoardModel data = mBoardModel.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("BoardInfo", boardInfo);
                Log.e(TAG, String.valueOf(boardInfo));
                ContextCompat.startActivity(context, intent, null);

            }
        });

    }

    public void setItemList(ArrayList<BoardModel> list){
        this.mBoardModel = list;
        this.notifyDataSetChanged();
    }

    // 아이템 카운트
    @Override
    public int getItemCount(){
        Log.d(TAG, "getItemCount: ");
        return mBoardModel.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateReceiptsList(ArrayList<BoardModel> newlist){
        //mBoardModel.clear();
        //mBoardModel.addAll(newlist);
        //mBoardModel = newlist;
        this.notifyDataSetChanged();
    }

    // item_layout의 데이터를 초기화해주는 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView itemImage;
        TextView itemTitle;
        TextView itemCategory;
        TextView itemDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = (ImageView) itemView.findViewById(R.id.item_image);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemCategory = (TextView) itemView.findViewById(R.id.item_category);
            itemDate = (TextView) itemView.findViewById(R.id.item_date);
        }



    }


}
