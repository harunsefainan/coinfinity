package com.example.cryptocurrencyapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cryptocurrencyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText name, email, password, confirmPassword;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //init_screen();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth=FirebaseAuth.getInstance();

        CheckBox mcheckbox=(CheckBox) findViewById(R.id.checkbox);

        progressBar=findViewById(R.id.progressBar);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        confirmPassword=findViewById(R.id.confirmPassword);

        TextView nextToLogin=(TextView) findViewById(R.id.nextToLogin);
        nextToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button RegisterBtn=(Button) findViewById(R.id.RegisterBtn);
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String etEmail=email.getText().toString().trim();
                String etName=name.getText().toString().trim();
                String etPassword=password.getText().toString().trim();
                String etConfirmPassword=confirmPassword.getText().toString().trim();
                String checkPassword="^(?=\\S+$).{6,20}$";

                if (mcheckbox.isChecked()){
                    if (!Patterns.EMAIL_ADDRESS.matcher(etEmail).matches()){
                        email.setError("Lütfen e-posta adresinizi eksiksiz giriniz.");
                        email.requestFocus();
                    }else if (TextUtils.isEmpty(etName)){
                        name.setError("Lütfen ad soyad giriniz.");
                        email.requestFocus();
                    }else if (TextUtils.isEmpty(etPassword)){
                        password.setError("Lütfen şifrenizi giriniz.");
                        password.requestFocus();
                    }else if (!etPassword.matches(checkPassword)){
                        password.setError("Lütfen 6 karakter veya daha uzun bir şifre giriniz.");
                        password.requestFocus();
                    }else if(TextUtils.isEmpty(etConfirmPassword)){
                        confirmPassword.setError("Lütfen şifrenizi doğrulayınız.");
                        confirmPassword.requestFocus();
                    }else if (!etConfirmPassword.matches(checkPassword)) {
                        password.setError("Lütfen aynı şifreyi giriniz.");
                        password.requestFocus();
                    }else if (!etPassword.matches(etConfirmPassword)) {
                        password.setError("Parolalar uyuşmuyor!");
                        password.requestFocus();
                    }else{
                        registerCreateUser(etEmail,etName,etPassword);
                    }
                }else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(etEmail).matches()){
                        email.setError("Lütfen e-posta adresinizi eksiksiz giriniz.");
                        email.requestFocus();
                    }else if (TextUtils.isEmpty(etName)){
                        name.setError("Lütfen ad soyad giriniz.");
                        email.requestFocus();
                    }else if (TextUtils.isEmpty(etPassword)){
                        password.setError("Lütfen şifrenizi giriniz.");
                        password.requestFocus();
                    }else if (!etPassword.matches(checkPassword)){
                        password.setError("Lütfen 6 karakter veya daha uzun bir şifre giriniz.");
                        password.requestFocus();
                    }else if(TextUtils.isEmpty(etConfirmPassword)){
                        confirmPassword.setError("Lütfen şifrenizi doğrulayınız.");
                        confirmPassword.requestFocus();
                    }else if (!etConfirmPassword.matches(checkPassword)) {
                        password.setError("Lütfen aynı şifreyi giriniz.");
                        password.requestFocus();
                    }else if (!etPassword.matches(etConfirmPassword)) {
                        password.setError("Parolalar uyuşmuyor!");
                        password.requestFocus();
                    }else{
                        Toast.makeText(RegisterActivity.this, "Lütfen kayıt olmadan önce koşulları okuyunuz.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void registerCreateUser(String etEmail, String etName, String etPassword) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(etEmail,etPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user=firebaseAuth.getCurrentUser();
                            uid=user.getUid();
                            String etEmail=user.getEmail();

                            HashMap<String, Object>hashMap=new HashMap<>();
                            hashMap.put("uid",uid);
                            hashMap.put("email",etEmail);
                            hashMap.put("name",etName);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("User");

                            reference.child(uid).setValue(hashMap);
                            Toast.makeText(RegisterActivity.this, "Başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(RegisterActivity.this, UserProfileActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                password.setError("Şifren çok zayıf.");
                                password.requestFocus();
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                email.setError("E-postanız geçersiz veya zaten etkin.");
                                email.requestFocus();
                            }catch (FirebaseAuthUserCollisionException e){
                                email.setError("Kullanıcı zaten bu e-posta ile kaydoldu.Lütfen başka bir e-posta adresi ile kayıt olun.");
                                email.requestFocus();
                            }catch (Exception e){
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }



    int backPressed=0;
    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed==1){
            Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }

    private void init_screen() {
        final int flags =View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        final View view=getWindow().getDecorView();
        view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN)==0){
                    view.setSystemUiVisibility(flags);
                }
            }
        });

    }
}