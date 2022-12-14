package com.example.cockmate.util;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cockmate.R;
import com.example.cockmate.adapter.MyRecyclerAdapter;
import com.example.cockmate.model.BoardModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainMenuCommunityFragment extends Fragment {

    private static final String TAG = "CommunityFragment";

    private ArrayList<BoardModel> mBoardModel;
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter mRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference mDBReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    ProgressDialog progressDialog;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    GridLayoutManager gridLayoutManager;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu_community, container, false);

        // 툴바생성
        //Toolbar toolbar = v.findViewById(R.id.community_bar);
        //setHasOptionsMenu(true);

        // 리사이클러뷰
        mRecyclerView = (RecyclerView) v.findViewById(R.id.community_recyclerView); // 리사이클러뷰 초기화
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 베이스 카테고리 정리
        mBoardModel = new ArrayList<BoardModel>();

        // 리사이클러뷰 불러오기
        EventChangeListener();

        baseCategoryButtonUp = v.findViewById(R.id.base_categroy_button_up);
        baseCategoryButtonDown = v.findViewById(R.id.base_categroy_button_down);
        baseCategoryList = v.findViewById(R.id.base_category_list);

        baseCategoryButtonDown.setVisibility(View.GONE);

        // 베이스 카테고리 버튼 정리
        mVodka = v.findViewById(R.id.go_vodka);
        mGin = v.findViewById(R.id.go_gin);
        mWhisky = v.findViewById(R.id.go_whisky);
        mBrandy = v.findViewById(R.id.go_brandy);
        mTequila = v.findViewById(R.id.go_tequila);
        mRum = v.findViewById(R.id.go_rum);
        mWine = v.findViewById(R.id.go_wine);
        mLiqueur = v.findViewById(R.id.go_liqueur);
        mVermouth = v.findViewById(R.id.go_vermouth);
        mBeer = v.findViewById(R.id.go_beer);
        mFree = v.findViewById(R.id.go_free);
        mEtc = v.findViewById(R.id.go_etc);

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
                Intent intent = new Intent(getActivity(), VodkaActivity.class);
                startActivity(intent);
            }
        });

        mGin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GinActivity.class);
                startActivity(intent);
            }
        });

        mWhisky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WhiskyActivity.class);
                startActivity(intent);
            }
        });

        mBrandy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BrandyActivity.class);
                startActivity(intent);
            }
        });

        mTequila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TequilaActivity.class);
                startActivity(intent);
            }
        });

        mRum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RumActivity.class);
                startActivity(intent);
            }
        });
        mWine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WineActivity.class);
                startActivity(intent);
            }
        });
        mLiqueur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LiqueurActivity.class);
                startActivity(intent);
            }
        });
        mVermouth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VermouthActivity.class);
                startActivity(intent);
            }
        });
        mBeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BeerActivity.class);
                startActivity(intent);
            }
        });
        mFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FreeActivity.class);
                startActivity(intent);
            }
        });
        mEtc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EtcActivity.class);
                startActivity(intent);
            }
        });


        return v;
    }

    @Override
    public void onResume(){
        super.onResume();

        // 리사이클러뷰
        //mRecyclerView = (RecyclerView) v.findViewById(R.id.community_recyclerView); // 리사이클러뷰 초기화
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                            bmodel.boardAlcol = (String) ds.get("Alcohol");
                            bmodel.boardWrittenUserID = (String) ds.get("WrittenUserID");

                            mBoardModel.add(bmodel);

                        }
                        //mRecyclerAdapter.updateReceiptsList(mBoardModel);
                        mRecyclerAdapter = new MyRecyclerAdapter(getActivity(), mBoardModel);
                        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                        mRecyclerView.setLayoutManager(gridLayoutManager);


                        // 리사이클러뷰에 데이터 전달
                        mRecyclerView.setAdapter(mRecyclerAdapter);
                        //mRecyclerAdapter.notifyDataSetChanged();
                        mRecyclerAdapter.setItemList(mBoardModel);

                    }
                });

    }

    /*
    // 툴바 메뉴 불러오기

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.post_bar,menu);
        //return super.onCreateOptionsMenu(menu);
        //MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.post_bar, menu);
        //return true;
    }

    // 툴바 메뉴 선택시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.post:{
                if (user == null){
                    Toast.makeText(getActivity(), "Log In Please", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "로그인 먼저 해주세요");
                }
                else{
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(), "글쓰기 버튼 클릭됨", Toast.LENGTH_LONG).show();
                    return true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

     */

}