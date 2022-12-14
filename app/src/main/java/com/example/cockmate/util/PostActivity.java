package com.example.cockmate.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cockmate.R;
import com.example.cockmate.adapter.MultiImageAdapter;
import com.example.cockmate.adapter.MyRecyclerAdapter;
import com.example.cockmate.model.BoardModel;
import com.example.cockmate.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    private static final String TAG = "PostActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference mDBReference;
    private SharedPreferences preferences;
    private MyRecyclerAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;

    Map<String, Object> board;


    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText Title, Content, Name_alcol;
    String postTitle, postContent, postNameAlcol;
    String postCategory;
    ImageView Image;
    Uri selectedUri;
    private String imageUrl="";
    private Bitmap bm;
    private String email;
    private FirebaseStorage mstorage = FirebaseStorage.getInstance();
    private StorageReference storageRef = mstorage.getReference();
    private DocumentReference documentRef;

    ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체

    RecyclerView recyclerView;  // 이미지를 보여줄 리사이클러뷰
    MultiImageAdapter adapter;  // 리사이클러뷰에 적용시킬 어댑터

    private File tempFile;
    Bitmap originalBm;

    // 베이스 카테고리 버튼
    Button vodka;
    Button gin;
    Button whisky;
    Button brandy;
    Button tequila;
    Button rum;
    Button wine;
    Button liqueur;
    Button vermouth;
    Button beer;
    Button free;
    Button etc;

    Context context;

    private String MyName;
    String prefName;
    private UserModel MyUserModel = new UserModel();

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //pref = PreferenceManager.getDefaultSharedPreferences(this);
        //editor = pref.edit();

        context = getApplicationContext();

        // 로그인한 정보 가져오기
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if (user != null){
            firebaseAuth.updateCurrentUser(user);
            loadName();
        }

        pref = PreferenceManager.getDefaultSharedPreferences(context);
        //pref = context.getSharedPreferences("loadName", MODE_PRIVATE);
        editor = pref.edit();

        //context = MainActivity.

        // 툴바생성
        Toolbar toolbar = findViewById(R.id.post_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Title = findViewById(R.id.post_title);
        Content = findViewById(R.id.post_content);
        Image = findViewById(R.id.post_image);
        Name_alcol = findViewById(R.id.post_alcohol_name);

        vodka = findViewById(R.id.vodka);
        gin = findViewById(R.id.gin);
        whisky = findViewById(R.id.whisky);
        brandy = findViewById(R.id.brandy);
        tequila = findViewById(R.id.tequila);
        rum = findViewById(R.id.rum);
        wine = findViewById(R.id.wine);
        liqueur = findViewById(R.id.liqueur);
        vermouth = findViewById(R.id.vermouth);
        beer = findViewById(R.id.beer);
        free = findViewById(R.id.non);
        etc = findViewById(R.id.etc);


        // 게시글의 고유 Id 생성
        documentRef = db.collection("Main_Board").document();

        if(selectedUri == null){
            Image.setVisibility(View.GONE);
        }
        else {
            Image.setVisibility(View.VISIBLE);
        }


        // 베이스 카테고리 하나만 선택하기
        SelectCategory();


        // 앨범으로 이동하는 버튼
        Button btn_getImage = findViewById(R.id.getImage);
        btn_getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 갤러리 사용 권한 설정
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "권한 설정 완료");
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        //intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2222);
                    }
                    else {
                        Log.d(TAG, "권한 설정 요청");
                        ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }

            }
        });

        recyclerView = findViewById(R.id.recyclerView);



    }



    // 앨범에서 액티비티로 돌아온 후 실행되는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == 2222){
            if (resultCode == RESULT_OK){
                selectedUri = data.getData();

                if(selectedUri == null){
                    Image.setVisibility(View.GONE);
                }
                else {
                    Image.setVisibility(View.VISIBLE);
                }

                storageRef = mstorage.getReference();
                StorageReference imageRef = storageRef.child("BoardImage/"+documentRef.getId()+".jpg");
                UploadTask uploadTask = imageRef.putFile(selectedUri);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Toast.makeText(getApplicationContext(), "사진이 정상적으로 업로드 되지 않았습니다.", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Toast.makeText(getApplicationContext(), "사진이 정상적으로 업로드 되었습니다.", Toast.LENGTH_LONG).show();
                    }
                });

                Glide.with(getApplicationContext())
                        .load(selectedUri)
                        .fitCenter()
                        .into(Image);

                Log.e("원조 uri", String.valueOf(selectedUri));

            }


        }
    }


    // 현재 날짜, 시간 구하기 (Real Date)
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    // 툴바 메뉴 불러오기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.post_complete_bar, menu);
        return true;
    }

    // 툴바 메뉴 선택시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        postTitle = Title.getText().toString();
        postContent = Content.getText().toString();
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                // 액티비티 이동
                finish();
                return true;
            }
            case R.id.post_complete:{
                if (postTitle.isEmpty() || postContent.isEmpty() || selectedUri == null || postCategory==null){
                    Toast.makeText(getApplicationContext(), "Your input is not complete.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "입력 완료 안 됨");
                }
                else {
                    saveData();
                    //Toast.makeText(getApplicationContext(), "등록 버튼 클릭됨", Toast.LENGTH_LONG).show();
                    // 리사이클러뷰 저장 및 새로고침
                    //mRecyclerAdapter.notifyDataSetChanged();
                    //mRecyclerView.invalidate();
                    finish();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadName(){
        //pref = PreferenceManager.getDefaultSharedPreferences(this);

        String uid = user.getUid();

        // 파이어베이스에서 같은 uid의 이름 데이터 가져오기
        mDBReference = FirebaseDatabase.getInstance().getReference();
        mDBReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Name").getValue(String.class);
                if (name != null){
                    Log.e(TAG, "post안에서 이름 불러왔을때 : "+name);

                    // UserModel에 저장하기
                    UserModel userModel = new UserModel(name, user.getEmail(), uid);
                    userModel.Save(context);
                    email = user.getEmail();

                    // 바로 SharedPreferences에 저장하기
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("NAME_post", name);
                    editor.apply();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

    }

    public void saveData(){
        postTitle = Title.getText().toString();
        postContent = Content.getText().toString();
        postNameAlcol = Name_alcol.getText().toString();
        String category = "main";
        long date = System.currentTimeMillis();
        String realDate = getTime();

        // 이름 불러오는 다양한 방법
        UserModel userModel = new UserModel();
        userModel.Load(context);
        String USERNAME = userModel.userName;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = sp.getString("NAME_post", "사용자 이름 없음");
        Log.e(TAG, "UserModel에 저장한 이름 : "+USERNAME);
        Log.e(TAG, "UserModel getter : "+userModel.getName());
        Log.e(TAG, "pref에 저장한 이름 : "+userName);

        board = new HashMap<>();
        board.put("Title", postTitle);
        board.put("Category", postCategory);
        board.put("Content", postContent);
        board.put("Date", date);
        board.put("Name", userName);
        board.put("Email", email);
        board.put("RealDate", realDate);
        board.put("ImageUri", documentRef.getId());
        board.put("BoardId", documentRef.getId());
        board.put("Alcohol", postNameAlcol);
        board.put("WrittenUserID", user.getUid());

        // 파이어스토어에 저장하기
        db.collection("Main_Board")
                .add(board)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "게시글 저장 성공");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "게시글 저장 실패");
                    }
                });
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

    // 권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    // 카테고리 선택
    public void SelectCategory(){
        vodka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vodka.setBackgroundResource(R.drawable.click_after);
                view.setPressed(true);
                postCategory = vodka.getText().toString();

                //vodka.setBackgroundResource(R.drawable.click_before);
                gin.setBackgroundResource(R.drawable.click_before);
                whisky.setBackgroundResource(R.drawable.click_before);
                brandy.setBackgroundResource(R.drawable.click_before);
                tequila.setBackgroundResource(R.drawable.click_before);
                rum.setBackgroundResource(R.drawable.click_before);
                wine.setBackgroundResource(R.drawable.click_before);
                liqueur.setBackgroundResource(R.drawable.click_before);
                vermouth.setBackgroundResource(R.drawable.click_before);
                beer.setBackgroundResource(R.drawable.click_before);
                free.setBackgroundResource(R.drawable.click_before);
                etc.setBackgroundResource(R.drawable.click_before);
            }
        });

        gin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gin.setBackgroundResource(R.drawable.click_after);
                view.setPressed(true);
                postCategory = gin.getText().toString();

                // 버튼색 비활성화
                vodka.setBackgroundResource(R.drawable.click_before);
                //gin.setBackgroundResource(R.drawable.click_before);
                whisky.setBackgroundResource(R.drawable.click_before);
                brandy.setBackgroundResource(R.drawable.click_before);
                tequila.setBackgroundResource(R.drawable.click_before);
                rum.setBackgroundResource(R.drawable.click_before);
                wine.setBackgroundResource(R.drawable.click_before);
                liqueur.setBackgroundResource(R.drawable.click_before);
                vermouth.setBackgroundResource(R.drawable.click_before);
                beer.setBackgroundResource(R.drawable.click_before);
                free.setBackgroundResource(R.drawable.click_before);
                etc.setBackgroundResource(R.drawable.click_before);
            }
        });

        whisky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whisky.setBackgroundResource(R.drawable.click_after);
                view.setPressed(true);
                postCategory = whisky.getText().toString();

                // 버튼색 비활성화
                vodka.setBackgroundResource(R.drawable.click_before);
                gin.setBackgroundResource(R.drawable.click_before);
                //whisky.setBackgroundResource(R.drawable.click_before);
                brandy.setBackgroundResource(R.drawable.click_before);
                tequila.setBackgroundResource(R.drawable.click_before);
                rum.setBackgroundResource(R.drawable.click_before);
                wine.setBackgroundResource(R.drawable.click_before);
                liqueur.setBackgroundResource(R.drawable.click_before);
                vermouth.setBackgroundResource(R.drawable.click_before);
                beer.setBackgroundResource(R.drawable.click_before);
                free.setBackgroundResource(R.drawable.click_before);
                etc.setBackgroundResource(R.drawable.click_before);
            }
        });

        brandy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                brandy.setBackgroundResource(R.drawable.click_after);
                view.setPressed(true);
                postCategory = brandy.getText().toString();

                // 버튼색 비활성화
                vodka.setBackgroundResource(R.drawable.click_before);
                gin.setBackgroundResource(R.drawable.click_before);
                whisky.setBackgroundResource(R.drawable.click_before);
                //brandy.setBackgroundResource(R.drawable.click_before);
                tequila.setBackgroundResource(R.drawable.click_before);
                rum.setBackgroundResource(R.drawable.click_before);
                wine.setBackgroundResource(R.drawable.click_before);
                liqueur.setBackgroundResource(R.drawable.click_before);
                vermouth.setBackgroundResource(R.drawable.click_before);
                beer.setBackgroundResource(R.drawable.click_before);
                free.setBackgroundResource(R.drawable.click_before);
                etc.setBackgroundResource(R.drawable.click_before);
            }
        });
        tequila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tequila.setBackgroundResource(R.drawable.click_after);
                view.setPressed(true);
                postCategory = tequila.getText().toString();

                // 버튼색 비활성화
                vodka.setBackgroundResource(R.drawable.click_before);
                gin.setBackgroundResource(R.drawable.click_before);
                whisky.setBackgroundResource(R.drawable.click_before);
                brandy.setBackgroundResource(R.drawable.click_before);
                //tequila.setBackgroundResource(R.drawable.click_before);
                rum.setBackgroundResource(R.drawable.click_before);
                wine.setBackgroundResource(R.drawable.click_before);
                liqueur.setBackgroundResource(R.drawable.click_before);
                vermouth.setBackgroundResource(R.drawable.click_before);
                beer.setBackgroundResource(R.drawable.click_before);
                free.setBackgroundResource(R.drawable.click_before);
                etc.setBackgroundResource(R.drawable.click_before);
            }
        });
        rum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rum.setBackgroundResource(R.drawable.click_after);
                view.setPressed(true);
                postCategory = rum.getText().toString();

                // 버튼색 비활성화
                vodka.setBackgroundResource(R.drawable.click_before);
                gin.setBackgroundResource(R.drawable.click_before);
                whisky.setBackgroundResource(R.drawable.click_before);
                brandy.setBackgroundResource(R.drawable.click_before);
                tequila.setBackgroundResource(R.drawable.click_before);
                //rum.setBackgroundResource(R.drawable.click_before);
                wine.setBackgroundResource(R.drawable.click_before);
                liqueur.setBackgroundResource(R.drawable.click_before);
                vermouth.setBackgroundResource(R.drawable.click_before);
                beer.setBackgroundResource(R.drawable.click_before);
                free.setBackgroundResource(R.drawable.click_before);
                etc.setBackgroundResource(R.drawable.click_before);
            }
        });
        wine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wine.setBackgroundResource(R.drawable.click_after);
                view.setPressed(true);
                postCategory = wine.getText().toString();

                // 버튼색 비활성화
                vodka.setBackgroundResource(R.drawable.click_before);
                gin.setBackgroundResource(R.drawable.click_before);
                whisky.setBackgroundResource(R.drawable.click_before);
                brandy.setBackgroundResource(R.drawable.click_before);
                tequila.setBackgroundResource(R.drawable.click_before);
                rum.setBackgroundResource(R.drawable.click_before);
                //wine.setBackgroundResource(R.drawable.click_before);
                liqueur.setBackgroundResource(R.drawable.click_before);
                vermouth.setBackgroundResource(R.drawable.click_before);
                beer.setBackgroundResource(R.drawable.click_before);
                free.setBackgroundResource(R.drawable.click_before);
                etc.setBackgroundResource(R.drawable.click_before);
            }
        });
        liqueur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liqueur.setBackgroundResource(R.drawable.click_after);
                view.setPressed(true);
                postCategory = liqueur.getText().toString();

                // 버튼색 비활성화
                vodka.setBackgroundResource(R.drawable.click_before);
                gin.setBackgroundResource(R.drawable.click_before);
                whisky.setBackgroundResource(R.drawable.click_before);
                brandy.setBackgroundResource(R.drawable.click_before);
                tequila.setBackgroundResource(R.drawable.click_before);
                rum.setBackgroundResource(R.drawable.click_before);
                wine.setBackgroundResource(R.drawable.click_before);
                //liqueur.setBackgroundResource(R.drawable.click_before);
                vermouth.setBackgroundResource(R.drawable.click_before);
                beer.setBackgroundResource(R.drawable.click_before);
                free.setBackgroundResource(R.drawable.click_before);
                etc.setBackgroundResource(R.drawable.click_before);
            }
        });
        vermouth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vermouth.setBackgroundResource(R.drawable.click_after);
                view.setPressed(true);
                postCategory = vermouth.getText().toString();

                // 버튼색 비활성화
                vodka.setBackgroundResource(R.drawable.click_before);
                gin.setBackgroundResource(R.drawable.click_before);
                whisky.setBackgroundResource(R.drawable.click_before);
                brandy.setBackgroundResource(R.drawable.click_before);
                tequila.setBackgroundResource(R.drawable.click_before);
                rum.setBackgroundResource(R.drawable.click_before);
                wine.setBackgroundResource(R.drawable.click_before);
                liqueur.setBackgroundResource(R.drawable.click_before);
                //vermouth.setBackgroundResource(R.drawable.click_before);
                beer.setBackgroundResource(R.drawable.click_before);
                free.setBackgroundResource(R.drawable.click_before);
                etc.setBackgroundResource(R.drawable.click_before);
            }
        });
        beer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beer.setBackgroundResource(R.drawable.click_after);
                view.setPressed(true);
                postCategory = beer.getText().toString();

                // 버튼색 비활성화
                vodka.setBackgroundResource(R.drawable.click_before);
                gin.setBackgroundResource(R.drawable.click_before);
                whisky.setBackgroundResource(R.drawable.click_before);
                brandy.setBackgroundResource(R.drawable.click_before);
                tequila.setBackgroundResource(R.drawable.click_before);
                rum.setBackgroundResource(R.drawable.click_before);
                wine.setBackgroundResource(R.drawable.click_before);
                liqueur.setBackgroundResource(R.drawable.click_before);
                vermouth.setBackgroundResource(R.drawable.click_before);
                //beer.setBackgroundResource(R.drawable.click_before);
                free.setBackgroundResource(R.drawable.click_before);
                etc.setBackgroundResource(R.drawable.click_before);
            }
        });
        free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                free.setBackgroundResource(R.drawable.click_after);
                view.setPressed(true);
                postCategory = free.getText().toString();

                // 버튼색 비활성화
                vodka.setBackgroundResource(R.drawable.click_before);
                gin.setBackgroundResource(R.drawable.click_before);
                whisky.setBackgroundResource(R.drawable.click_before);
                brandy.setBackgroundResource(R.drawable.click_before);
                tequila.setBackgroundResource(R.drawable.click_before);
                rum.setBackgroundResource(R.drawable.click_before);
                wine.setBackgroundResource(R.drawable.click_before);
                liqueur.setBackgroundResource(R.drawable.click_before);
                vermouth.setBackgroundResource(R.drawable.click_before);
                beer.setBackgroundResource(R.drawable.click_before);
                // free.setBackgroundResource(R.drawable.click_before);
                etc.setBackgroundResource(R.drawable.click_before);
            }
        });
        etc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etc.setBackgroundResource(R.drawable.click_after);
                view.setPressed(true);
                postCategory = etc.getText().toString();

                // 버튼색 비활성화
                vodka.setBackgroundResource(R.drawable.click_before);
                gin.setBackgroundResource(R.drawable.click_before);
                whisky.setBackgroundResource(R.drawable.click_before);
                brandy.setBackgroundResource(R.drawable.click_before);
                tequila.setBackgroundResource(R.drawable.click_before);
                rum.setBackgroundResource(R.drawable.click_before);
                wine.setBackgroundResource(R.drawable.click_before);
                liqueur.setBackgroundResource(R.drawable.click_before);
                vermouth.setBackgroundResource(R.drawable.click_before);
                beer.setBackgroundResource(R.drawable.click_before);
                free.setBackgroundResource(R.drawable.click_before);
                //etc.setBackgroundResource(R.drawable.click_before);
            }
        });
    }


}


