package com.example.cockmate.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cockmate.R;
import com.example.cockmate.adapter.CommentAdapter;
import com.example.cockmate.adapter.MyRecyclerAdapter;
import com.example.cockmate.model.BoardModel;
import com.example.cockmate.model.CommentModel;
import com.example.cockmate.model.UserModel;
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
import java.nio.DoubleBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OnnxValue;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.io.ByteStreamsKt;
import kotlin.jvm.internal.Intrinsics;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;

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

    TextView comment_num;
    GridLayoutManager gridLayoutManager;

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDBReference;
    private FirebaseUser user;
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

    Intent intent;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private UserModel MyUserModel = new UserModel();
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        context = getApplicationContext();

        // 로그인한 정보 가져오기
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if (user != null){
            firebaseAuth.updateCurrentUser(user);
            loadName();
        }

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
        mCategory = findViewById(R.id.detailCategoryReal);
        mRealDate = (TextView) findViewById(R.id.detailDate);
        mName = (TextView) findViewById(R.id.detailName);
        mImage = (ImageView) findViewById(R.id.detailImage);
        comment_num = (TextView) findViewById(R.id.comment_num);

        mImage.setClipToOutline(true);




        intent = getIntent();
        ArrayList<String> boardInfo = (ArrayList<String>) intent.getSerializableExtra("BoardInfo");
        //boardInfo = new ArrayList<>();
        mTitle.setText(boardInfo.get(0));
        mContent.setText(boardInfo.get(1));
        mCategory.setText(boardInfo.get(2));
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
                .orderBy("CommentDate", Query.Direction.DESCENDING)
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
                            commentModel.commentFavorCategory = (String) ds.get("CommentFavorCategory");

                            // 오름차순으로 정렬하기
                            mCommentModel.add(0,commentModel);
                        }

                        mCommentAdapter = new CommentAdapter(DetailActivity.this, mCommentModel);
                        // Comment 리사이클러뷰에 데이터 전달하기
                        mRecyclerView.setAdapter(mCommentAdapter);
                        mCommentAdapter.setItemList(mCommentModel);

                        // 댓글 수 저장
                        int commentNum = mCommentAdapter.getItemCount();
                        //Log.e(TAG, "받아온 댓글 수"+String.valueOf(commentNum));
                        comment_num.setText(String.valueOf(commentNum));
                    }
                });
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
                    Log.e(TAG, "detail안에서 이름 불러왔을때 : "+name);

                    // UserModel에 저장하기
                    UserModel userModel = new UserModel(name, user.getEmail(), uid);
                    userModel.Save(context);

                    // 바로 SharedPreferences에 저장하기
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("NAME_detail", name);
                    editor.apply();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
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

                        //preferences = getSharedPreferences("UserName", MODE_PRIVATE);


                        if (user == null){
                            Toast.makeText(getApplicationContext(), "Log In Please", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "로그인 먼저 해주세요");
                        }
                        else{


                            // BoardModel에서 BoardId 가져오기
                            intent = getIntent();
                            ArrayList<String> boardInfo = (ArrayList<String>) intent.getSerializableExtra("BoardInfo");


                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                            String myCommentName = sp.getString("NAME_detail", "사용자 이름 없음");
                            Log.e(TAG, "pref에 저장한 detail 이름 : "+myCommentName);


                            // CommentModel에 저장할 데이터 불러오기
                            //Log.e(TAG, myCommentName);
                            String myCommentBoardId = boardInfo.get(6);
                            String myCommentContent = mComment.getText().toString();
                            long myCommentDate = System.currentTimeMillis();
                            String myCommentRealDate = getTime();
                            String mCommentCategory = "";

                            // 맛 카테고리 분석 관련
                            String[] feature_set = {"쌉쌀/MAG", "달콤/XR", "달달/MAG", "잘/MAG", "더/MAG", "상큼/XR", "이/MDT", "살짝/MAG", "그/MDT", "약간/MAG", "시원/XR", "조금/MAG", "많이/MAG", "깔끔/XR", "시피/MAG", "함께/MAG", "은은/XR", "좀/MAG", "거의/MAG", "자몽/XR", "또/MAG", "가장/MAG", "너무/MAG", "다/MAG", "및/MAG", "바로/MAG", "아주/MAG", "적당/XR", "다른/MDT", "매우/MAG", "새콤달콤/XR", "제이/MAG", "새콤/MAG", "풍부/XR", "그냥/MAG", "상큼/MAG", "가득/MAG", "산뜻/XR", "여러/MDT", "특히/MAG", "없이/MAG", "적당히/MAG", "안/MAG", "향긋/MAG", "같이/MAG", "비슷/XR", "간단/XR", "딱/MAG", "구수/XR", "별로/MAG", "정말/MAG", "첫/MDT", "상쾌/XR", "묵직/XR", "역시/MAG", "일단/MAG", "다시/MAG", "오래/MAG", "꽤/MAG", "그대로/MAG", "이런/MDT", "그리/MAG", "못/MAG", "따뜻/XR", "먼저/MAG", "모두/MAG", "상당히/MAG", "신선/XR", "엄청/MAG", "익숙/XR", "제법/MAG", "주로/MAG", "하여/MAG", "개운/XR", "굉장히/MAG", "그래서/MAG", "그리고/MAG", "아마/MAG", "다소/MAG", "아직/MAG", "어떤/MDT", "톡/MAG", "또는/MAG", "마치/MAG", "몇/MDT", "더욱/MAG", "데/MAG", "모/MDT", "씁쓸/XR", "어느/MDT", "또한/MAG", "모든/MDT", "무난/XR", "시큼/MAG", "텁텁/XR", "혹은/MAG", "훌륭/XR", "그런/MDT", "적절/XR", "전혀/MAG", "참/MAG", "물론/MAG", "훨씬/MAG", "삭/MAG", "이제/MAG", "저렴/XR", "톡톡/MAG", "풍성/XR", "꽤나/MAG", "뭔/MDT", "보다/MAG", "즉/MAG", "항상/MAG", "강렬/XR", "그윽/XR", "몬/MAG", "서로/MAG", "우아/XR", "제일/MAG", "짜릿/MAG", "가끔/MAG", "꿀떡/MAG", "덜/MAG", "밍밍/XR", "비교적/MAG", "오히려/MAG", "왜/MAG", "잠시/MAG", "제대로/MAG", "중후/XR", "진짜/MAG", "확실히/MAG", "계속/MAG", "꼭/MAG", "달리/MAG", "둥글둥글/MAG", "따로/MAG", "막/MAG", "아마도/MAG", "오묘/XR", "점점/MAG", "한층/MAG", "화려/XR", "대충/MAG", "바짝/MAG", "벌써/MAG", "새로/MAG", "이미/MAG", "잔잔/XR", "적절히/MAG", "종종/MAG", "직접/MAG", "카/MAG", "흔히/MAG", "각/MDT", "간단히/MAG", "곧/MAG", "과연/MAG", "금방/MAG", "너무나/MAG", "늘/MAG", "달짝지근/XR", "대체로/MAG", "따분/XR", "발랄/XR", "비롯/XR", "비릿/XR", "아무래도/MAG", "알싸/XR", "워낙/MAG", "원래/MAG", "이내/MAG", "이렇게/MAG", "이에/MAG", "자세히/MAG", "좀더/MAG", "천천히/MAG", "청량/XR", "충분/XR", "탕/MAG", "갑자기/MAG", "결코/MAG", "경쾌/XR", "깔끔/MAG", "꽉/MAG", "내내/MAG", "다만/MAG", "다채/XR", "당연/XR", "마구/MAG", "매번/MAG", "매콤/XR", "물씬/MAG", "불쾌/XR", "상세/XR", "술술/MAG", "실제로/MAG", "쓱/MAG", "씩/MAG", "아무튼/MAG", "언제/MAG", "엎치락뒤치락/MAG", "영롱/XR", "예민/XR", "오랜/MDT", "우/MAG", "우선/MAG", "의외로/MAG", "잉/MAG", "저/MDT", "즐/MAG", "탄탄/XR", "풋풋/XR", "한데/MAG", "활발히/MAG", "간편/XR", "갈수록/MAG", "결국/MAG", "곤드레/MAG", "굳이/MAG", "궁금/XR", "그다지/MAG", "그런데/MAG", "근데/MAG", "농후/XR", "느끼/XR", "단단/XR", "담백/XR", "뚜/MAG", "무슨/MDT", "바이/MAG", "보통/MAG", "본/MDT", "비록/MAG", "빙그레/MAG", "빨리/MAG", "소중/XR", "솔직히/MAG", "쉬/MAG", "쌉싸래/MAG", "아예/MAG", "알록달록/MAG", "앞서/MAG", "오직/MAG", "온전히/MAG", "완전히/MAG", "왠지/MAG", "자글자글/MAG", "자꾸/MAG", "자주/MAG", "잘못/MAG", "저릿/MAG", "점차/MAG", "정말로/MAG", "조금씩/MAG", "지금/MAG", "쫌/MAG", "쭉/MAG", "총/MDT", "쿵/MAG", "퀴퀴/XR", "평범/XR", "퐁당/MAG", "픽/MAG", "훅/MAG", "흡사/MAG", "희미/XR", "각각/MAG", "걸쭉/XR", "골고루/MAG", "과감히/MAG", "그냥저냥/MAG", "그럭저럭/MAG", "그럼/MAG", "그야말로/MAG", "글로/MAG", "깨끗/XR", "껄끔/MAG", "꿀꺽/MAG", "끈끈/XR", "끈적/MAG", "난생/MAG", "널리/MAG", "넘/MAG", "농밀/XR", "단연코/MAG", "단지/MAG", "달래/MAG", "답답/XR", "대강/MAG", "도저히/MAG", "도톰/XR", "동글동글/MAG", "두기/XR", "두툼/XR", "딱딱/XR", "마/MAG", "마냥/MAG", "말끔/XR", "매일/MAG", "맨/MDT", "맨날/MAG", "머/MAG", "명확히/MAG", "모락모락/MAG", "몽글몽글/MAG", "무방/XR", "무심코/MAG", "미묘/XR", "바삭바삭/MAG", "벌컥벌컥/MAG", "별달리/MAG", "분명/MAG", "불구/XR", "빵빵/MAG", "생생/XR", "선선/XR", "섬세/XR", "손수/MAG", "솔직/XR", "스르륵/MAG", "스믈/MDT", "스스로/MAG", "신기/XR", "심심/XR", "싱글/MAG", "싱싱/MAG", "쌀쌀/XR", "쏙쏙/MAG", "아늑/XR", "아릿/XR", "알딸딸/XR", "암튼/MAG", "양대/MDT", "어렴풋/XR", "어리/MAG", "어쨌거나/MAG", "어찌/MAG", "얼른/MAG", "얼마나/MAG", "얼추/MAG", "여전히/MAG", "여태껏/MAG", "연미/XR", "열심히/MAG", "유쾌/XR", "은근히/MAG", "이무/MAG", "이제야/MAG", "잠깐/MAG", "절대/MAG", "지만/XR", "진득/XR", "짜릿짜릿/MAG", "짭짤/XR", "쫄깃/XR", "쭈욱/MAG", "찌릿/XR", "차마/MAG", "착착/MAG", "처음/MAG", "충분히/MAG", "통째로/MAG", "특별히/MAG", "팍/MAG", "포롱/MAG", "푸릇/XR", "한결/MAG", "허겁지겁/MAG", "현재/MAG", "혹시/MAG", "휘/MAG", "흐/MAG", "가까이/MAG", "가다가/MAG", "가령/MAG", "가만/MAG", "각기/MAG", "각자/MAG", "감사히/MAG", "걍/MAG", "거꾸로/MAG", "게다가/MAG", "겨우/MAG", "고로/MAG", "곧바로/MAG", "굉장/XR", "그렇게/MAG", "그만/MAG", "그저/MAG", "근소/XR", "기껏/MAG", "기발/XR", "깊이/MAG", "깜짝/MAG", "깜찍/XR", "꾸덕꾸덕/MAG", "꾸준히/MAG", "꾼들/MAG", "끊임없이/MAG", "남김없이/MAG", "누릿/XR", "눅진/XR", "다닥다닥/MAG", "단단히/MAG", "단연/MAG", "달착지근/XR", "달큼/XR", "담담/XR", "당연히/MAG", "더구나/MAG", "더더욱/MAG", "더듬더듬/MAG", "더리/MAG", "도도/XR", "도로/MAG", "도무지/MAG", "두근두근/MAG", "두로/MAG", "두루/MAG", "두루두루/MAG", "드디어/MAG", "드리/MAG", "든든히/MAG", "따끈/XR", "따끈따끈/MAG", "딱히/MAG", "딴딴/XR", "때때로/MAG", "때로는/MAG", "떨떠름/XR", "또렷/XR", "뚜렷/XR", "띵/MAG", "마음대로/MAG", "마주/MAG", "마침/MAG", "마카/MAG", "맘대로/MAG", "모처럼/MAG", "몰래/MAG", "무려/MAG", "무미/XR", "무척/MAG", "미리/MAG", "미미/XR", "미약/XR", "민감/XR", "바삭/MAG", "복잡/XR", "부득이/MAG", "부디/MAG", "불과/MAG", "비로소/MAG", "비비/MAG", "뿌듯/XR", "사르르/MAG", "사실/MAG", "살포시/MAG", "살풋/MAG", "새/MDT", "생소/XR", "서늘/XR", "서머/XR", "섭섭/XR", "성큼/MAG", "솔솔/MAG", "수월/XR", "슬쩍/MAG", "심지어/MAG", "쓸쓸/XR", "아까/MAG", "아무/MDT", "아무리/MAG", "아적/MAG", "애매/XR", "어느새/MAG", "어마어마/XR", "어지간/XR", "어지간히/MAG", "어째서/MAG", "어쨋든/MAG", "어쨌든/MAG", "어쩐지/MAG", "어차피/MAG", "언뜻/MAG", "얼얼/XR", "얼핏/MAG", "여러모로/MAG", "옛/MDT", "오늘/MAG", "오래도록/MAG", "온갖/MDT", "왜냐면/MAG", "외로/MAG", "요런/MDT", "용이/XR", "으슬으슬/MAG", "으하하/MAG", "은은히/MAG", "이래저래/MAG", "이른바/MAG", "이만/MAG", "이만/XR", "이어서/MAG", "이윽고/MAG", "이처럼/MAG", "이케/MAG", "이토록/MAG", "일로/MAG", "일부러/MAG", "일약/MAG", "일일이/MAG", "일찌감치/MAG", "있다/MAG", "자꾸만/MAG", "자세/XR", "자연스레/MAG", "자잘/XR", "자칫하면/MAG", "잔뜩/MAG", "저절로/MAG", "적어도/MAG", "적이/MAG", "전/MDT", "절묘/XR", "절실히/MAG", "정성껏/MAG", "정확히/MAG", "조만간/MAG", "조심스레/MAG", "조이/MAG", "주/MDT", "지극히/MAG", "지독/XR", "지루/XR", "진정/MAG", "짝/MAG", "쩌릿/MAG", "찌르르/MAG", "찜찜/XR", "찡/MAG", "차랑/MAG", "착/MAG", "찬찬히/MAG", "철두철미/MAG", "출출/MAG", "칵/MAG", "탁/MAG", "특히나/MAG", "팔팔/MAG", "퍽/MAG", "편히/MAG", "평이/XR", "평평/XR", "포근/XR", "폭신폭신/MAG", "하나같이/MAG", "한번/MAG", "함부로/MAG", "헤/MAG", "혹시나/MAG", "확/MAG", "확실/XR", "확연/XR", "확연히/MAG", "훌쩍/MAG", "흑흑/MAG"};
                            double[] IDF_table = {0.7258020881816168, 0.8055299604586157, 0.9389376157733125, 1.1324788606852316, 1.189476619174033, 1.351242023738311, 1.3749366895365212, 1.4452790749006372, 1.485242374845657, 1.469822015978163, 1.495835448343081, 1.5122258645312503, 1.5235068749409393, 1.5292592038300308, 1.5914071105788752, 1.5469879707904624, 1.5654713764844754, 1.5718115545154945, 1.5914071105788752, 1.7322226092860133, 1.6408924736587933, 1.6484456115492394, 1.6561324402155304, 1.705350462885712, 1.705350462885712, 1.7230792298461435, 1.7415626355401566, 1.7511079534463871, 1.7415626355401566, 1.7810711768238303, 1.7915366105019952, 1.7608677907355432, 1.8022604758937684, 1.7915366105019952, 1.7915366105019952, 1.8022604758937684, 1.860252422871455, 1.8245368706049205, 1.8361187431547357, 1.8361187431547357, 1.8480179664544434, 1.9419224693227746, 1.8728415501794757, 1.8991704889018248, 1.9419224693227746, 1.912958773387458, 1.9271992125020683, 1.9271992125020683, 1.9571624358795114, 1.9571624358795114, 1.9571624358795114, 1.9893471192509127, 1.9893471192509127, 2.006380458549693, 2.0241092255101245, 2.006380458549693, 2.0241092255101245, 2.042592631204138, 2.042592631204138, 2.0618977863995243, 2.1032904715577496, 2.0821011724878113, 2.0821011724878113, 2.149047962118425, 2.1032904715577496, 2.1032904715577496, 2.1738715458434568, 2.149047962118425, 2.1738715458434568, 2.1032904715577496, 2.1032904715577496, 2.1255668662689016, 2.1255668662689016, 2.149047962118425, 2.149047962118425, 2.1255668662689016, 2.149047962118425, 2.1738715458434568, 2.149047962118425, 2.149047962118425, 2.149047962118425, 2.149047962118425, 2.1738715458434568, 2.1738715458434568, 2.1738715458434568, 2.200200484565806, 2.290377114914894, 2.290377114914894, 2.200200484565806, 2.200200484565806, 2.2282292081660495, 2.2282292081660495, 2.2282292081660495, 2.2282292081660495, 2.2282292081660495, 2.2282292081660495, 2.2282292081660495, 2.2581924315434927, 2.2581924315434927, 2.2581924315434927, 2.2581924315434927, 2.290377114914894, 2.290377114914894, 2.3251392211741058, 2.3251392211741058, 2.3251392211741058, 2.3251392211741058, 2.3251392211741058, 2.3629277820635055, 2.3629277820635055, 2.3629277820635055, 2.3629277820635055, 2.3629277820635055, 2.404320467221731, 2.404320467221731, 2.4500779577824057, 2.404320467221731, 2.4500779577824057, 2.404320467221731, 2.4500779577824057, 2.4500779577824057, 2.8022604758937684, 2.4500779577824057, 2.4500779577824057, 2.4500779577824057, 2.4500779577824057, 2.4500779577824057, 2.4500779577824057, 2.4500779577824057, 2.4500779577824057, 2.4500779577824057, 2.501230480229787, 2.501230480229787, 2.501230480229787, 2.559222427207474, 2.501230480229787, 2.501230480229787, 2.501230480229787, 2.501230480229787, 2.501230480229787, 2.501230480229787, 2.501230480229787, 2.501230480229787, 2.7053504628857117, 2.559222427207474, 2.559222427207474, 2.559222427207474, 2.559222427207474, 2.559222427207474, 2.559222427207474, 2.559222427207474, 2.559222427207474, 2.559222427207474, 2.559222427207474, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.8022604758937684, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.626169216838087, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.8022604758937684, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.8022604758937684, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.7053504628857117, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.8022604758937684, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 3.1032904715577496, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 3.1032904715577496, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 3.1032904715577496, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 2.9271992125020683, 3.1032904715577496, 2.9271992125020683, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496, 3.1032904715577496};

                            double[] TFIDF = convert_review_to_TFIDF(myCommentContent, feature_set, IDF_table);

                            if(TFIDF != null) {
                                OrtEnvironment ortEnvironment = OrtEnvironment.getEnvironment();
                                DetailActivity detail = DetailActivity.this;
                                Intrinsics.checkNotNullExpressionValue(ortEnvironment, "ortEnvironment");
                                OrtSession ortSession = null;
                                try {
                                    ortSession = detail.createORTSession(ortEnvironment);
                                } catch (OrtException e) {
                                    e.printStackTrace();
                                }
                                int output = 0;
                                try {
                                    output = DetailActivity.this.runPrediction(TFIDF, ortSession, ortEnvironment);
                                } catch (OrtException e) {
                                    e.printStackTrace();
                                }
                                if(output == 0)
                                    mCommentCategory = "Bitter";
                                if(output == 1)
                                    mCommentCategory = "Sour";
                                if(output == 2)
                                    mCommentCategory = "Sweet";
                            } else {
                                Toast.makeText((Context) DetailActivity.this, (CharSequence) "Please check the inputs", Toast.LENGTH_LONG).show();
                            }


                            // 파이어베이스에 저장할 HashMap 데이터 만들기
                            Map<String, Object> comment = new HashMap<>();
                            comment.put("CommentBoardId", myCommentBoardId);
                            comment.put("CommentUserName", myCommentName);
                            comment.put("CommentContent", myCommentContent);
                            comment.put("CommentDate", myCommentDate);
                            comment.put("CommentRealDate", myCommentRealDate);
                            comment.put("CommentFavorCategory", mCommentCategory);

                            db.collection("Comment")
                                    .add(comment)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "댓글 저장 성공");

                                            // 화면 새로고침하기
                                            overridePendingTransition(0, 0);//인텐트 효과 없애기
                                            Intent intent = getIntent(); //인텐트
                                            finish();
                                            startActivity(intent); //액티비티 열기
                                            overridePendingTransition(0, 0);//인텐트 효과 없애기
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "댓글 저장 실패");
                                        }
                                    });

                        }

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

    /*
    '댓글로 맛 카테고리 분석' 관련 함수들
     */
    public static String morph(String review) {
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

        KomoranResult analyzeResultList = komoran.analyze(review);
        String morphLine = analyzeResultList.getPlainText();
        System.out.println(morphLine);
        System.out.println();

        return morphLine;
    }

    public static double[] cal_TF(String review, String[] features) {
        double[] tf = new double[features.length];
        Arrays.fill(tf, 0.0);
        String[] morph = review.split(" ");
        for(int i=0; i<morph.length; i++) {
            for(int j=0; j<features.length; j++) {
                if(features[j].equals(morph[i])) {
                    tf[j] += 1;
                }
            }
        }
        for(int i=0; i<tf.length; i++) {
            tf[i] = Math.log10(tf[i] + 1);
        }

        return tf;
    }

    public static double[] TFIDF_norm(String[] features, double[] TF, double[] IDF) {
        double[] TFIDF = new double[features.length];
        Arrays.fill(TFIDF, 0.0);
        double sum = 0.0;
        for(int i=0; i<features.length; i++) {
            sum += Math.pow((TF[i]*IDF[i]), 2);
        }
        for(int i=0; i< features.length; i++) {
            if(sum == 0) {
                sum = 0.00000000000000000000000001;
            }
            TFIDF[i] = (TF[i]*IDF[i]) / Math.sqrt(sum);
        }

        return TFIDF;
    }

    public static double[] convert_review_to_TFIDF(String review, String[] features, double[] IDF) {
        String pre = morph(review);

        double[] TF = cal_TF(pre, features);
        double[] TFIDF = TFIDF_norm(features, TF, IDF);

        return TFIDF;
    }

    private OrtSession createORTSession(OrtEnvironment ortEnvironment) throws OrtException {
        byte[] modelBytes = ByteStreamsKt.readBytes(getResources().openRawResource(R.raw.sklearn_model));
        return ortEnvironment.createSession(modelBytes);
    }

    private int runPrediction(double[] input, OrtSession ortSession, OrtEnvironment ortEnvironment) throws OrtException {
        String TFIDF_str = Arrays.toString(input);
        String inputName = ortSession.getInputNames().iterator().next();

//        // unused ?
//        double[] inputs = new double[TFIDF_str.length()];
//        for(int i=0; i<TFIDF_str.length(); i++) {
//            inputs[i] = Double.valueOf(inputs[i]);
//        }
//        String newstr = Arrays.toString(inputs);

        DoubleBuffer doubleBufferInputs = DoubleBuffer.wrap(input);
        OnnxTensor inputTensor = OnnxTensor.createTensor(ortEnvironment, doubleBufferInputs, new long[]{1L, 1L});
        OrtSession.Result results = ortSession.run(MapsKt.mapOf(TuplesKt.to(inputName, inputTensor)));

        OnnxValue var11 = results.get(0);
        Intrinsics.checkNotNullExpressionValue(var11, "results[0");
        Object var12 = var11.getValue();
        if(var12 == null) {
            throw new NullPointerException("null cannot be cast to non-null type kotlin.Array<kotlin.FloatArray>");
        } else {
            int[][] output = (int[][])var12;
            return output[0][0];
        }
    }

}
