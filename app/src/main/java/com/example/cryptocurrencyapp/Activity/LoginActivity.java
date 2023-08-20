package com.example.cryptocurrencyapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final String FILE_EMAIL = "EmailPassSave";
    EditText Email, Password;
    ProgressBar progressBar;
    String uid, etName;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //init_screen();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);

        //e-postayı ve şifreyi kaydet onay kutusunu tıklayın
        final CheckBox mCheckBoxRemember = (CheckBox) findViewById(R.id.remember_me);
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_EMAIL, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String etEmail = sharedPreferences.getString("etEmail", "");
        String etPassword = sharedPreferences.getString("etPassword", "");
        if (sharedPreferences.contains("checked") && sharedPreferences.getBoolean("checked", false) == true) {
            mCheckBoxRemember.setChecked(true);
        } else {
            mCheckBoxRemember.setChecked(false);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        //metni göster
        Email.setText(etEmail);
        Password.setText(etPassword);

        TextView nextToRegister = (TextView) findViewById(R.id.nextToRegister);
        nextToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button LoginBtn = (Button) findViewById(R.id.LoginBtn);
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String etEmail = Email.getText().toString().trim();
                String etPassword = Password.getText().toString().trim();
                String checkPassword = "^(?=\\S+$).{6,20}$";

                if (mCheckBoxRemember.isChecked()) {
                    editor.putBoolean("checked", true);
                    editor.apply();
                    StoreDataUsingSharedPref(etEmail, etPassword);

                    if (!Patterns.EMAIL_ADDRESS.matcher(etEmail).matches()) {
                        Email.setError("Lütfen e-posta adresinizi eksiksiz giriniz.");
                        Email.requestFocus();
                    } else if (TextUtils.isEmpty(etPassword)) {
                        Password.setError("Lütfen şifrenizi giriniz.");
                        Password.requestFocus();
                    } else if (!etPassword.matches(checkPassword)) {
                        Password.setError("Lütfen 6 karakter veya daha uzun bir şifre giriniz.");
                        Password.requestFocus();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        loginUser(etName, etEmail, etPassword);
                    }
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(etEmail).matches()) {
                        Email.setError("Lütfen e-posta adresinizi eksiksiz giriniz.");
                        Email.requestFocus();
                    } else if (TextUtils.isEmpty(etPassword)) {
                        Password.setError("Lütfen şifrenizi giriniz.");
                        Password.requestFocus();
                    } else if (!etPassword.matches(checkPassword)) {
                        Password.setError("Lütfen 6 karakter veya daha uzun bir şifre giriniz.");
                        Password.requestFocus();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        getSharedPreferences(FILE_EMAIL, MODE_PRIVATE).edit().clear().commit();
                        loginUser(etName, etEmail, etPassword);
                    }
                }
            }
        });

        TextView ForgotPassword = (TextView) findViewById(R.id.ForgotPassword);
        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editText = new EditText(v.getContext());
                AlertDialog.Builder forgotPasswordDialog = new AlertDialog.Builder(v.getContext());
                forgotPasswordDialog.setTitle("Şifreyi yenile ?");
                forgotPasswordDialog.setMessage("Alınan sıfırlama bağlantısı için e-postanızı girin.");
                forgotPasswordDialog.setView(editText);

                forgotPasswordDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String Email = editText.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(Email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LoginActivity.this, "E-postanıza gönderilen bağlantıyı sıfırlayın.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Hata! Sıfırlama bağlantısı gönderilmedi. Bu tanımlayıcıya karşılık gelen kullanıcı kaydı yok. Kullanıcı silinmiş olabilir", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                forgotPasswordDialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                forgotPasswordDialog.create().show();
            }
        });

    }


    private void loginUser(String etName, String etEmail, String etPassword) {
        firebaseAuth.signInWithEmailAndPassword(etEmail, etPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                String etEmail = user.getEmail();
                                uid = user.getUid();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("email", etEmail);
                                hashMap.put("name", etName);
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("User");

                                reference.child(uid).setValue(hashMap);
                            }
                            if (user.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Giriş başarılı", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                progressBar.setVisibility(View.GONE);
                                finish();
                            } else {
                                user.sendEmailVerification();
                                firebaseAuth.signOut();
                                progressBar.setVisibility(View.GONE);
                                showAlertDialog();
                            }
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                Email.setError("Kullanıcı adı sistemde mevcut değil.Lütfen tekrar kayıt olunuz.");
                                Email.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Email.setError("Bazı bilgiler doğru değil lütfen kontrol edin ve tekrar deneyin.");
                                Email.requestFocus();
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("E-posta doğrulanmadı, lütfen kontrol edin");
        builder.setMessage("Lütfen yeni e-postanızı doğrulayın, e-postanızı doğrulamadan giriş yapamazsınız");

        builder.setPositiveButton("E-mail'i doğrula", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void StoreDataUsingSharedPref(String etEmail, String etPassword) {
        SharedPreferences.Editor editor = getSharedPreferences(FILE_EMAIL, MODE_PRIVATE).edit();
        editor.putString("etEmail", etEmail);
        editor.putString("etPassword", etPassword);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            Toast.makeText(this, "Giriş yapıldı!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Şimdi giriş yapabilirsin!", Toast.LENGTH_SHORT).show();
        }
    }

    int backPressed = 0;

    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed == 1) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }

    private void init_screen() {
        final int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        final View view = getWindow().getDecorView();
        view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    view.setSystemUiVisibility(flags);
                }
            }
        });

    }
}