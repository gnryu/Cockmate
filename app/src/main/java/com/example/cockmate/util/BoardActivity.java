package com.example.cockmate.util;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cockmate.R;
import com.example.cockmate.adapter.MyRecyclerAdapter;
import com.example.cockmate.model.BoardModel;
import com.example.cockmate.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BoardActivity extends AppCompatActivity {

    private static final String TAG = "BoardActivity";

    private ArrayList<BoardModel> mBoardModel;
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter mRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference mDBReference;
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        // 툴바생성
        Toolbar toolbar = findViewById(R.id.board_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView); // 리사이클러뷰 초기화
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mBoardModel = new ArrayList<BoardModel>();
        EventChangeListener();




    }


    // Firestore에서 데이터 불러오기
    private void EventChangeListener() {

        // 날짜 최신순으로 정렬
        db.collection("Main_Board").orderBy("Date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot ds : queryDocumentSnapshots){

                            // BoardModel에 각각 저장
                            BoardModel bmodel = ds.toObject(BoardModel.class);
                            bmodel.boardEmail = (String) ds.get("Email");
                            bmodel.boardCategory = (String) ds.get("Category");
                            bmodel.boardRealDate = (String) ds.get("RealDate");
                            bmodel.boardContent = (String) ds.get("Content");
                            bmodel.boardName = (String) ds.get("Name");
                            bmodel.boardTitle = (String) ds.get("Title");

                            mBoardModel.add(bmodel);
                            //mRecyclerAdapter.updateReceiptsList(mBoardModel);


                            //mRecyclerAdapter.notifyDataSetChanged();
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();



                            //mBoardModel.add((BoardModel) boardData);
                            //Log.e(TAG, b_email + b_category + b_realdate + b_content + b_name + b_title);
                        }
                        //mRecyclerAdapter.updateReceiptsList(mBoardModel);
                        mRecyclerAdapter = new MyRecyclerAdapter(BoardActivity.this, mBoardModel);


                        // 리사이클러뷰에 데이터 전달
                        mRecyclerView.setAdapter(mRecyclerAdapter);
                        //mRecyclerAdapter.notifyDataSetChanged();
                        mRecyclerAdapter.setItemList(mBoardModel);

                        //mRecyclerView.invalidate();
                        //mRecyclerView.getAdapter().notifyDataSetChanged();

                    }
                });

    }


    public void OnStart(){
        super.onStart();
        setContentView(R.layout.activity_board);


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
