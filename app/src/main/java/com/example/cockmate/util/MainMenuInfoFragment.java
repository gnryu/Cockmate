package com.example.cockmate.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cockmate.R;
import com.example.cockmate.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainMenuInfoFragment extends Fragment {

    private static final String TAG = "InfoFragment";

    Button loginBtn;
    Button logoutBtn;
    TextView joinBtn;

    TextView before_title;
    TextView after_title;
    TextView after_name;
    TextView before_notice;
    TextView after_name_title;
    TextView after_email_title;
    TextView after_email;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDBReference;
    private FirebaseUser user;
    private SharedPreferences preferences;

    private String email;
    private String uid;
    private String name;

    private Fragment fragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View v = inflater.inflate(R.layout.fragment_main_menu_info, container, false);

        loginBtn = v.findViewById(R.id.login_button);
        logoutBtn = v.findViewById(R.id.logout_button);
        joinBtn = v.findViewById(R.id.join_button);
        before_title = v.findViewById(R.id.login_fragment_title);
        before_notice = v.findViewById(R.id.login_notice);
        after_title = v.findViewById(R.id.login_fragment_title2);
        after_name = v.findViewById(R.id.login_fragment_name);
        after_name_title = v.findViewById(R.id.login_fragment_name_title);
        after_email = v.findViewById(R.id.login_fragment_email);
        after_email_title = v.findViewById(R.id.login_fragment_email_title);

        // 로그인한 정보 가져오기
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();



        if (user == null){
            before_title.setVisibility(View.VISIBLE);
            before_notice.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            joinBtn.setVisibility(View.VISIBLE);

            logoutBtn.setVisibility(View.GONE);
            after_title.setVisibility(View.GONE);
            after_name.setVisibility(View.GONE);
            after_name_title.setVisibility(View.GONE);
            after_email.setVisibility(View.GONE);
            after_email_title.setVisibility(View.GONE);
        }
        else {
            email = user.getEmail();
            uid = user.getUid();


            before_title.setVisibility(View.GONE);
            before_notice.setVisibility(View.GONE);
            loginBtn.setVisibility(View.GONE);
            joinBtn.setVisibility(View.GONE);

            logoutBtn.setVisibility(View.VISIBLE);
            after_title.setVisibility(View.VISIBLE);
            after_name.setVisibility(View.VISIBLE);
            after_name_title.setVisibility(View.VISIBLE);
            after_email.setVisibility(View.VISIBLE);
            after_email_title.setVisibility(View.VISIBLE);

            // 파이어베이스에서 같은 uid의 이름 데이터 가져오기
            mDBReference = FirebaseDatabase.getInstance().getReference();
            mDBReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name = dataSnapshot.child("Name").getValue(String.class);
                    if (name != null){
                        after_name.setText(name);
                        after_email.setText(user.getEmail());

                        //Intent intent = new Intent(getActivity(), PostActivity.class);
                        //intent.putExtra("name", name);
                        //startActivity(intent);
                        UserModel userModel = new UserModel();
                        userModel.setName(name);
                        userModel.setEmail(user.getEmail());
                        userModel.setUserId(uid);
                        Log.e(TAG, "로그인화면"+name);
                        Log.e(TAG, "로그인화면"+uid);


                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                }
            });
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), JoinActivity.class);
                startActivity(intent);

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                user = firebaseAuth.getCurrentUser();



                if (user == null){
                    before_title.setVisibility(View.VISIBLE);
                    before_notice.setVisibility(View.VISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                    joinBtn.setVisibility(View.VISIBLE);

                    logoutBtn.setVisibility(View.GONE);
                    after_title.setVisibility(View.GONE);
                    after_name.setVisibility(View.GONE);
                    after_name_title.setVisibility(View.GONE);
                    after_email.setVisibility(View.GONE);
                    after_email_title.setVisibility(View.GONE);
                }
                else{
                    email = user.getEmail();
                    uid = user.getUid();


                    before_title.setVisibility(View.GONE);
                    before_notice.setVisibility(View.GONE);
                    loginBtn.setVisibility(View.GONE);
                    joinBtn.setVisibility(View.GONE);

                    logoutBtn.setVisibility(View.VISIBLE);
                    after_title.setVisibility(View.VISIBLE);
                    after_name.setVisibility(View.VISIBLE);
                    after_name_title.setVisibility(View.VISIBLE);
                    after_email.setVisibility(View.VISIBLE);
                    after_email_title.setVisibility(View.VISIBLE);
                }

                //Intent intent = new Intent(getActivity(), MainActivity.class);
                //Intent intent1 = Intent.getIntent(getActivity());
                //startActivity(intent);
                Toast.makeText(getActivity(), "Log Out Success", Toast.LENGTH_LONG).show();
                Log.e(TAG, "로그아웃 완료");

            }
        });



        return v;
    }

    private void refresh(){
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        //ft.detach(this).attach(this).commit();
        //ft.replace(R.id.menu_frame_layout, this).commitAllowingStateLoss();
    }

    @Override
    public void onResume(){
        super.onResume();
        user = firebaseAuth.getCurrentUser();

        if (user == null){
            before_title.setVisibility(View.VISIBLE);
            before_notice.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            joinBtn.setVisibility(View.VISIBLE);

            logoutBtn.setVisibility(View.GONE);
            after_title.setVisibility(View.GONE);
            after_name.setVisibility(View.GONE);
            after_name_title.setVisibility(View.GONE);
            after_email.setVisibility(View.GONE);
            after_email_title.setVisibility(View.GONE);
        }
        else{
            email = user.getEmail();
            uid = user.getUid();


            before_title.setVisibility(View.GONE);
            before_notice.setVisibility(View.GONE);
            loginBtn.setVisibility(View.GONE);
            joinBtn.setVisibility(View.GONE);

            logoutBtn.setVisibility(View.VISIBLE);
            after_title.setVisibility(View.VISIBLE);
            after_name.setVisibility(View.VISIBLE);
            after_name_title.setVisibility(View.VISIBLE);
            after_email.setVisibility(View.VISIBLE);
            after_email_title.setVisibility(View.VISIBLE);

            // 파이어베이스에서 같은 uid의 이름 데이터 가져오기
            mDBReference = FirebaseDatabase.getInstance().getReference();
            mDBReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name = dataSnapshot.child("Name").getValue(String.class);
                    if (name != null){
                        after_name.setText(name);
                        after_email.setText(user.getEmail());

                        //Intent intent = new Intent(getActivity(), PostActivity.class);
                        //intent.putExtra("name", name);
                        //startActivity(intent);
                        UserModel userModel = new UserModel();
                        userModel.setName(name);
                        userModel.setEmail(user.getEmail());
                        userModel.setUserId(uid);
                        Log.e(TAG, "로그인화면"+name);
                        Log.e(TAG, "로그인화면"+uid);


                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                }
            });
        }
    }




}