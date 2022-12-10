package com.example.cockmate.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cockmate.R;
import com.example.cockmate.adapter.CommentAdapter;
import com.example.cockmate.adapter.MyRecyclerAdapter;
import com.example.cockmate.model.BoardModel;
import com.example.cockmate.model.CommentModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.Reference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    TextView mTitle;
    TextView mContent;
    TextView mCategory;
    TextView mRealDate;
    TextView mName;
    ImageView mImage;

    EditText mComment;
    Button mCommentButton;

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDBReference;
    private SharedPreferences preferences;

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<CommentModel> mCommentModel;
    private CommentAdapter mCommentAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseStorage mstorage = FirebaseStorage.getInstance();
    private StorageReference storageRef = mstorage.getReference();
    private StorageReference pathRef = storageRef.child("BoardImage");

    String mboardId;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 툴바생성
        Toolbar toolbar = findViewById(R.id.detail_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 리사이클러뷰
        mRecyclerView = (RecyclerView) findViewById(R.id.detailRecyclerView); // 리사이클러뷰 초기화
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mBoardModel = new ArrayList<BoardModel>();
        mCommentModel = new ArrayList<CommentModel>();




        mTitle = (TextView) findViewById(R.id.detailTitle);
        mContent = (TextView) findViewById(R.id.detailContent);
        //mCategory = findViewById(R.id.);
        mRealDate = (TextView) findViewById(R.id.detailDate);
        mName = (TextView) findViewById(R.id.detailName);
        mImage = (ImageView) findViewById(R.id.detailImage);

        mImage.setClipToOutline(true);

        Intent intent = getIntent();
        ArrayList<String> boardInfo = (ArrayList<String>) intent.getSerializableExtra("BoardInfo");
        mTitle.setText(boardInfo.get(0));
        mContent.setText(boardInfo.get(1));
        mRealDate.setText(boardInfo.get(3));
        mName.setText(boardInfo.get(4));
        String mimageuri = boardInfo.get(5);
        //Log.e(TAG, boardInfo.get(5));
        mboardId = boardInfo.get(6);

        if (pathRef == null) {
            Log.e(TAG, "저장소에 사진이 없습니다.");
        }
        else{
            StorageReference myImage = storageRef.child("BoardImage/"+mimageuri+".jpg");
            myImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext())
                            .load(uri)
                            .into(mImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "이미지 불러오기 실패");
                }
            });
        }

        /*
        Glide.with(getApplicationContext())
                .load(mimageuri)
                .fitCenter()
                .fallback(R.drawable.ic_launcher_foreground) // load 실패 시 보여줄 사진
                .into(mImage);

         */

        // 댓글 관련
        mComment = (EditText) findViewById(R.id.detailComment);
        mCommentButton = (Button) findViewById(R.id.detailCommentButton);
        mCommentButton.setVisibility(View.INVISIBLE);



        // 댓글 불러오기
        makeCommentRecyclerview();

        // 댓글 저장하기
        saveCommentData();




    }

    // 댓글 파이어베이스에서 불러오기
    public void makeCommentRecyclerview(){

        //Log.e(TAG, mboardId);
        // 날짜 최신순으로 정렬 (최신이 아래로 오게끔)
        db.collection("Comment")
                .whereEqualTo("CommentBoardId", mboardId)
                //.orderBy("CommentDate", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot ds : queryDocumentSnapshots){

                            CommentModel commentModel = ds.toObject(CommentModel.class);

                            commentModel.commentBoardId = (String) ds.get("CommentBoardId");
                            commentModel.commentUserName = (String) ds.get("CommentUserName");
                            commentModel.commentContent = (String) ds.get("CommentContent");
                            commentModel.commentDate = (long) ds.get("CommentDate");
                            commentModel.commentRealDate = (String) ds.get("CommentRealDate");

                            // 오름차순으로 정렬하기
                            mCommentModel.add(0,commentModel);
                        }

                        mCommentAdapter = new CommentAdapter(DetailActivity.this, mCommentModel);
                        // Comment 리사이클러뷰에 데이터 전달하기
                        mRecyclerView.setAdapter(mCommentAdapter);
                        mCommentAdapter.setItemList(mCommentModel);
                    }
                });
    }


    // 댓글 파이어베이스에 저장하기
    public void saveCommentData(){

        // EditText에 글 있을 때만 CommentButton 활성화
        mComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    mCommentButton.setVisibility(View.VISIBLE);


                mCommentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        preferences = getSharedPreferences("UserName", MODE_PRIVATE);

                        // 로그인한 정보 가져오기
                        firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        String uid = user.getUid();

                        // 파이어베이스에서 같은 uid의 이름 데이터 가져오기
                        mDBReference = FirebaseDatabase.getInstance().getReference();
                        mDBReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child("Name").getValue(String.class);
                                if (name != null){
                                    //Log.d(TAG, name);

                                    // SharedPreference 사용해서 name 임시 저장하기
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("userName", name);
                                    editor.commit();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                            }
                        });

                        // BoardModel에서 BoardId 가져오기
                        Intent intent = getIntent();
                        ArrayList<String> boardInfo = (ArrayList<String>) intent.getSerializableExtra("BoardInfo");

                        // CommentModel에 저장할 데이터 불러오기
                        String myCommentName = preferences.getString("userName", "없음"); // SharedPreference 이용해서 저장된 값 불러오기
                        //Log.e(TAG, myCommentName);
                        String myCommentBoardId = boardInfo.get(6);
                        String myCommentContent = mComment.getText().toString();
                        long myCommentDate = System.currentTimeMillis();
                        String myCommentRealDate = getTime();

                        // 파이어베이스에 저장할 HashMap 데이터 만들기
                        Map<String, Object> comment = new HashMap<>();
                        comment.put("CommentBoardId", myCommentBoardId);
                        comment.put("CommentUserName", myCommentName);
                        comment.put("CommentContent", myCommentContent);
                        comment.put("CommentDate", myCommentDate);
                        comment.put("CommentRealDate", myCommentRealDate);

                        db.collection("Comment")
                                .add(comment)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "댓글 저장 성공");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "댓글 저장 실패");
                                    }
                                });



                    }
                });


                    
                } else {
                    mCommentButton.setVisibility(View.INVISIBLE);
                }
            }
        });


    }


    // 현재 날짜, 시간 구하기 (Real Date)
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
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

    // edit text 외 다른 영역 터치시 키보드 숨기기
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

}
