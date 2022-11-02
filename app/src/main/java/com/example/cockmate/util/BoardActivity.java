package com.example.cockmate.util;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cockmate.R;
import com.example.cockmate.adapter.MyRecyclerAdapter;
import com.example.cockmate.model.BoardModel;

import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity {

    private ArrayList<BoardModel> mBoardModel;
    private ArrayList<BoardModel> mfriendItems;
    private MyRecyclerAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        // 툴바생성
        Toolbar toolbar = findViewById(R.id.board_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView); // 리사이클러뷰 초기화

        //linearLayoutManager = new LinearLayoutManager(this); // 레이아웃 매니저
        //mRecyclerView.setLayoutManager(linearLayoutManager); // 리사이클러뷰에 set해줌

        mRecyclerAdapter =  new MyRecyclerAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //mRecyclerView.setAdapter((RecyclerView.Adapter) mRecyclerAdapter);


        /*
        mBoardModel = new ArrayList<>(); // 어댑터 선언
        mRecyclerAdapter = new MyRecyclerAdapter(mBoardModel);

        mRecyclerAdapter =  new MyRecyclerAdapter();

        mRecyclerView.setAdapter((RecyclerView.Adapter) mRecyclerAdapter);


         */


        load();
    }

    void load(){
        mfriendItems = new ArrayList<>();

        for(int i=0;i<10;i++){
            if(i%2==0)
                mfriendItems.add(new BoardModel("f",i+"번째 사람",i+"번째 카테고리", "1", "1"));
            else
                mfriendItems.add(new BoardModel("f",i+"번째 사람",i+"번째 카테고리", "1", "1"));

        }
        mRecyclerAdapter.setItemList(mfriendItems);
    }

    // 툴바 메뉴 불러오기

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.post_bar, menu);
        return true;
    }


    // 툴바 메뉴 선택시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                // 액티비티 이동
                finish();
                return true;
            }
            case R.id.post:{
                Intent intent = new Intent(BoardActivity.this, PostActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "글쓰기 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
