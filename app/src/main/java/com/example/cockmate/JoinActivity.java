package com.example.cockmate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class JoinActivity extends AppCompatActivity {

    private static final String TAG = "JoinActivity";
    EditText mName, mEmailText, mPasswordText, mPasswordCheckText;
    Button mJoinBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼
        actionBar.setDisplayShowHomeEnabled(true); // 홈 아이콘

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

                                // hash map 테이블을 파이어베이스 데이터베이스에 저장
                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("email", email);
                                hashMap.put("name", name);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(hashMap);

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

    public boolean onSupportNavigateUp() {
        onBackPressed(); // 뒤로가기 버튼이 눌렸을 시
        return super.onSupportNavigateUp(); // 뒤로가기 버튼
    }
}