package com.example.cockmate.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class FreeActivity extends AppCompatActivity {

    private static final String TAG = "FreeActivity";

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
    Context context;

    private FirebaseStorage mstorage = FirebaseStorage.getInstance();
    private StorageReference storageRef = mstorage.getReference();
    private StorageReference pathRef = storageRef.child("BoardImage");

    private TextView notice;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free);
        context = getApplicationContext();

        notice = findViewById(R.id.noticefree);
        // ???????????? ?????? ????????????
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        // ????????????
        Toolbar toolbar = findViewById(R.id.free_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //????????????




        // ??????????????????
        mRecyclerView = (RecyclerView) findViewById(R.id.free_recyclerView); // ?????????????????? ?????????
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBoardModel = new ArrayList<BoardModel>();

        LoadRecyclerview();
    }

    @Override
    public void onResume(){
        super.onResume();
        // ??????????????????
        mRecyclerView = (RecyclerView) findViewById(R.id.free_recyclerView); // ?????????????????? ?????????
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBoardModel = new ArrayList<BoardModel>();

        LoadRecyclerview();

    }

    // Firestore?????? ????????? ????????????
    private void LoadRecyclerview() {

        // ?????? ??????????????? ?????? (????????? ?????? ?????????)
        db.collection("Main_Board")
                .whereEqualTo("Category", "Free")
                .orderBy("Date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot ds : queryDocumentSnapshots){

                            // ?????? ??????????????? ????????? ????????? ?????????????????? ?????????
                            if (!ds.exists()){
                                notice.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.GONE);
                            }
                            else {
                                notice.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.VISIBLE);

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

                                mBoardModel.add(bmodel);
                                Log.e(TAG, String.valueOf(bmodel));


                            }
                            //mRecyclerAdapter.updateReceiptsList(mBoardModel);
                            mRecyclerAdapter = new MyRecyclerAdapter(FreeActivity.this, mBoardModel);


                            // ????????????????????? ????????? ??????
                            mRecyclerView.setAdapter(mRecyclerAdapter);
                            //mRecyclerAdapter.notifyDataSetChanged();
                            mRecyclerAdapter.setItemList(mBoardModel);
                        }

                    }
                });

    }

    // ?????? ?????? ????????????

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.post_bar, menu);
        return true;
    }


    // ?????? ?????? ?????????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar??? back??? ????????? ??? ??????
                // ???????????? ??????
                finish();
                return true;
            }
            case R.id.post:{
                if (user == null){
                    Toast.makeText(getApplicationContext(), "Log In Please", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "????????? ?????? ????????????");
                }
                else{
                    Intent intent = new Intent(FreeActivity.this, PostActivity.class);
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(), "????????? ?????? ?????????", Toast.LENGTH_LONG).show();
                    return true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
