package com.example.cockmate.util;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private FirebaseUser user;
    ProgressDialog progressDialog;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseStorage mstorage = FirebaseStorage.getInstance();
    private StorageReference storageRef = mstorage.getReference();
    private StorageReference pathRef = storageRef.child("BoardImage");



    Button baseCategoryButtonUp;
    Button baseCategoryButtonDown;
    View baseCategoryList;

    ImageButton mVodka;
    ImageButton mGin;
    ImageButton mWhisky;
    ImageButton mBrandy;
    ImageButton mTequila;
    ImageButton mRum;
    ImageButton mWine;
    ImageButton mLiqueur;
    ImageButton mVermouth;
    ImageButton mBeer;
    ImageButton mFree;
    ImageButton mEtc;

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

        // 리사이클러뷰
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView); // 리사이클러뷰 초기화
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // 베이스 카테고리 정리
        mBoardModel = new ArrayList<BoardModel>();

        // 리사이클러뷰 불러오기
        EventChangeListener();

        baseCategoryButtonUp = findViewById(R.id.base_categroy_button_up);
        baseCategoryButtonDown = findViewById(R.id.base_categroy_button_down);
        baseCategoryList = findViewById(R.id.base_category_list);

        baseCategoryButtonDown.setVisibility(View.GONE);

        // 베이스 카테고리 버튼 정리
        mVodka = findViewById(R.id.go_vodka);
        mGin = findViewById(R.id.go_gin);
        mWhisky = findViewById(R.id.go_whisky);
        mBrandy = findViewById(R.id.go_brandy);
        mTequila = findViewById(R.id.go_tequila);
        mRum = findViewById(R.id.go_rum);
        mWine = findViewById(R.id.go_wine);
        mLiqueur = findViewById(R.id.go_liqueur);
        mVermouth = findViewById(R.id.go_vermouth);
        mBeer = findViewById(R.id.go_beer);
        mFree = findViewById(R.id.go_free);
        mEtc = findViewById(R.id.go_etc);

        // 로그인한 정보 가져오기
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        baseCategoryButtonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseCategoryList.setVisibility(View.GONE);
                baseCategoryButtonUp.setVisibility(View.GONE);
                baseCategoryButtonDown.setVisibility(View.VISIBLE);
            }
        });

        baseCategoryButtonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseCategoryList.setVisibility(View.VISIBLE);
                baseCategoryButtonUp.setVisibility(View.VISIBLE);
                baseCategoryButtonDown.setVisibility(View.GONE);
            }
        });

        mVodka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, VodkaActivity.class);
                startActivity(intent);
            }
        });

        mGin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, GinActivity.class);
                startActivity(intent);
            }
        });

        mWhisky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, WhiskyActivity.class);
                startActivity(intent);
            }
        });

        mBrandy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, BrandyActivity.class);
                startActivity(intent);
            }
        });

        mTequila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, TequilaActivity.class);
                startActivity(intent);
            }
        });

        mRum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, RumActivity.class);
                startActivity(intent);
            }
        });
        mWine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, WineActivity.class);
                startActivity(intent);
            }
        });
        mLiqueur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, LiqueurActivity.class);
                startActivity(intent);
            }
        });
        mVermouth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, VermouthActivity.class);
                startActivity(intent);
            }
        });
        mBeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, BeerActivity.class);
                startActivity(intent);
            }
        });
        mFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, FreeActivity.class);
                startActivity(intent);
            }
        });
        mEtc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, EtcActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();

        // 리사이클러뷰
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView); // 리사이클러뷰 초기화
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 베이스 카테고리 정리
        mBoardModel = new ArrayList<BoardModel>();


        // 리사이클러뷰 불러오기
        EventChangeListener();
    }



    // Firestore에서 데이터 불러오기
    private void EventChangeListener() {

        // 날짜 최신순으로 정렬 (최신이 위로 오게끔)
        db.collection("Main_Board").orderBy("Date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot ds : queryDocumentSnapshots){

                            String  imaUri = (String) ds.get("ImageUri");
                            //Bitmap imaUri = (Bitmap) ds.get("ImageUri");
                            if (imaUri == null){
                                Log.e(TAG, "uri 비어있음");
                            }
                            else {
                                Log.e(TAG, String.valueOf(imaUri));
                            }

                            // BoardModel에 각각 저장
                            BoardModel bmodel = ds.toObject(BoardModel.class);
                            bmodel.boardEmail = (String) ds.get("Email");
                            bmodel.boardCategory = (String) ds.get("Category");
                            bmodel.boardRealDate = (String) ds.get("RealDate");
                            bmodel.boardContent = (String) ds.get("Content");
                            bmodel.boardName = (String) ds.get("Name");
                            bmodel.boardTitle = (String) ds.get("Title");
                            bmodel.boardImageUrl = imaUri;
                            bmodel.boardId = (String) ds.get("BoardId");

                            mBoardModel.add(bmodel);

                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        //mRecyclerAdapter.updateReceiptsList(mBoardModel);
                        mRecyclerAdapter = new MyRecyclerAdapter(BoardActivity.this, mBoardModel);


                        // 리사이클러뷰에 데이터 전달
                        mRecyclerView.setAdapter(mRecyclerAdapter);
                        //mRecyclerAdapter.notifyDataSetChanged();
                        mRecyclerAdapter.setItemList(mBoardModel);

                    }
                });

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
                if (user == null){
                    Toast.makeText(getApplicationContext(), "Log In Please", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "로그인 먼저 해주세요");
                }
                else{
                    Intent intent = new Intent(BoardActivity.this, PostActivity.class);
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(), "글쓰기 버튼 클릭됨", Toast.LENGTH_LONG).show();
                    return true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
