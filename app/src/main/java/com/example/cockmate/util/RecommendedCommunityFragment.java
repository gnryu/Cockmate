package com.example.cockmate.util;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

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
import java.util.Collections;

public class RecommendedCommunityFragment extends Fragment {

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
        View v = inflater.inflate(R.layout.fragment_recommend, container, false);

        // ????????????
        //Toolbar toolbar = v.findViewById(R.id.community_bar);
        //setHasOptionsMenu(true);

        // ??????????????????
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recommend_recyclerView); // ?????????????????? ?????????
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // ????????? ???????????? ??????
        mBoardModel = new ArrayList<BoardModel>();

        // ?????????????????? ????????????
        EventChangeListener();

        baseCategoryButtonUp = v.findViewById(R.id.recommend_base_categroy_button_up);
        baseCategoryButtonDown = v.findViewById(R.id.recommend_base_categroy_button_down);
        baseCategoryList = v.findViewById(R.id.recommend_base_category_list);

        baseCategoryButtonDown.setVisibility(View.GONE);

        // ????????? ???????????? ?????? ??????
        mVodka = v.findViewById(R.id.recommend_go_vodka);
        mGin = v.findViewById(R.id.recommend_go_gin);
        mWhisky = v.findViewById(R.id.recommend_go_whisky);
        mBrandy = v.findViewById(R.id.recommend_go_brandy);
        mTequila = v.findViewById(R.id.recommend_go_tequila);
        mRum = v.findViewById(R.id.recommend_go_rum);
        mWine = v.findViewById(R.id.recommend_go_wine);
        mLiqueur = v.findViewById(R.id.recommend_go_liqueur);
        mVermouth = v.findViewById(R.id.recommend_go_vermouth);
        mBeer = v.findViewById(R.id.recommend_go_beer);
        mFree = v.findViewById(R.id.recommend_go_free);
        mEtc = v.findViewById(R.id.recommend_go_etc);

        // ???????????? ?????? ????????????
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

        // ??????????????????
        //mRecyclerView = (RecyclerView) v.findViewById(R.id.community_recyclerView); // ?????????????????? ?????????
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // ????????? ???????????? ??????
        mBoardModel = new ArrayList<BoardModel>();

        // ?????????????????? ????????????
        EventChangeListener();
    }

    // Firestore?????? ????????? ????????????
    private void EventChangeListener() {

        // ?????? ??????????????? ?????? (????????? ?????? ?????????)
        String pred = this.getArguments().getString("pred");
        db.collection("Main_Board").whereEqualTo("Alcohol", pred).orderBy("Date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot ds : queryDocumentSnapshots){

                            String  imaUri = (String) ds.get("ImageUri");
                            //Bitmap imaUri = (Bitmap) ds.get("ImageUri");
                            if (imaUri == null){
                                Log.e(TAG, "uri ????????????");
                            }
                            else {
                                Log.e(TAG, String.valueOf(imaUri));
                            }

                            // BoardModel??? ?????? ??????
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


                        // ????????????????????? ????????? ??????
                        mRecyclerView.setAdapter(mRecyclerAdapter);
                        //mRecyclerAdapter.notifyDataSetChanged();
                        mRecyclerAdapter.setItemList(mBoardModel);

                    }
                });

    }

    /*
    // ?????? ?????? ????????????

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.post_bar,menu);
        //return super.onCreateOptionsMenu(menu);
        //MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.post_bar, menu);
        //return true;
    }

    // ?????? ?????? ?????????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.post:{
                if (user == null){
                    Toast.makeText(getActivity(), "Log In Please", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "????????? ?????? ????????????");
                }
                else{
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(), "????????? ?????? ?????????", Toast.LENGTH_LONG).show();
                    return true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

     */

}