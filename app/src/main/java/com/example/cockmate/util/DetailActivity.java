package com.example.cockmate.util;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cockmate.R;
import com.example.cockmate.model.BoardModel;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    TextView mTitle;
    TextView mContent;
    TextView mCategory;
    TextView mRealDate;
    TextView mName;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);



        /*
        mTitle = (TextView) findViewById(R.id.detailTitleView);
        mContent = (TextView) findViewById(R.id.detailContentView);
        //mCategory = findViewById(R.id.);
        mRealDate = (TextView) findViewById(R.id.detailDateView);
        //mName = (TextView) findViewById(R.id.detailNameView);

        Intent intent = getIntent();
        ArrayList<String> boardInfo = (ArrayList<String>) intent.getSerializableExtra("BoardInfo");
        mTitle.setText(boardInfo.get(0));
        //mName.setText("나와라");
        mContent.setText(boardInfo.get(1));
        mRealDate.setText(boardInfo.get(3));
        //mName.setText(boardInfo.get(4));
        Log.e(TAG, boardInfo.get(4));



         */

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
        }
        return super.onOptionsItemSelected(item);
    }





}
