package cosmetic.com.cosmetictable_server;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    //파이어베이스 관리자 객체를 생성한다. getInstanve는 한번만 생성하는 것.


   /* private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    */

   //혹은 DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();

   EditText EdittextEmail;
   EditText EdittextPassword;
   Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EdittextEmail = (EditText)findViewById(R.id.editText_email);
        EdittextPassword = (EditText)findViewById(R.id.editText_password);
        login = (Button)findViewById(R.id.email_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateUser(EdittextEmail.getText().toString(),EdittextPassword.getText().toString());
            }
        });

    }


        private void CreateUser(final String email, final String password){

            //사용자 계정 생성 메소드
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //계정 생성 성공
                                Toast.makeText(LoginActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                            } else {
                                //만약 이미 있는 아이디인데 비밀번호가 틀렸을 경우엔
                                //계정 생성하지 않고, 로그인시도로 넘어간다.
                                LoginUser(email, password);
                                //inner클래스의 경우 final을 붙혀야 사용가능하다.

                            }

                        }
                    });
        }

        private void LoginUser(String email, String password){

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(LoginActivity.this, "로그인 성공!.", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(i);
                            } else {
                                // If sign in fails, display a message to the user.
                                //아이디가 이미 존재하거나, 비밀번호가 틀렸다.
                                Toast.makeText(LoginActivity.this, "이미 존재하는 아이디 이거나 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }



}






