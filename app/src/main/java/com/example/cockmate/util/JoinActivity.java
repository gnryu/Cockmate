package com.example.cockmate.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cockmate.R;
import com.example.cockmate.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class JoinActivity extends AppCompatActivity {

    private static final String TAG = "JoinActivity";
    EditText mName, mEmailText, mPasswordText, mPasswordCheckText;
    Button mJoinBtn;
    private FirebaseAuth firebaseAuth;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        // 툴바생성
        Toolbar toolbar = findViewById(R.id.join_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        mEmailText = findViewById(R.id.user_email);
        mPasswordText = findViewById(R.id.user_password);
        mPasswordCheckText = findViewById(R.id.check_password);
        mName = findViewById(R.id.user_name);
        mJoinBtn = findViewById(R.id.join_btn);


        mJoinBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 가입 정보 가져오기
                final String email = mEmailText.getText().toString().trim();
                String pwd = mPasswordText.getText().toString().trim();
                String pwd_check = mPasswordCheckText.getText().toString().trim();

                if(pwd.equals(pwd_check)) {
                    Log.d(TAG, "회원가입 버튼 " + email + ", " + pwd);
                    final ProgressDialog dialog = new ProgressDialog(JoinActivity.this);
                    dialog.setMessage("가입중입니다...");
                    dialog.show();

                    // 파이어베이스에 신규 계정 등록하기
                    firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // 가입 성공 시
                            if(task.isSuccessful()) {
                                dialog.dismiss();
                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                String email = user.getEmail();
                                String uid = user.getUid();
                                String name = mName.getText().toString().trim();


                                // UserModel에 값 저장하기
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("Email", email);
                                userData.put("Uid", uid);
                                userData.put("Name", name);

                                // 파이어베이스 firestore에 저장하기
                                db.collection("Auth")
                                        .add(userData)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "저장 성공");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "저장 실패");
                                            }
                                        });



                                // 파이어베이스 실시간 데이터베이스에 저장하기
                                DatabaseReference mDBReference = FirebaseDatabase.getInstance().getReference();
                                HashMap<String, Object> userUpdates = new HashMap<>();
                                UserModel userModel = new UserModel(name, email, uid);
                                Map<String, Object> userValue = userModel.toMap();

                                userUpdates.put("/Users/" + uid, userValue);
                                mDBReference.updateChildren(userUpdates);




                                // 가입이 이루어졌을 시 가입 화면을 빠져나감 -> 로그인 화면으로 이동
                                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(JoinActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(JoinActivity.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                } else { // 비밀번호 오류 시
                    Toast.makeText(JoinActivity.this, "비밀번호가 틀렸습니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
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