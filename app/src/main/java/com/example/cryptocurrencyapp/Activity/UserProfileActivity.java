package com.example.cryptocurrencyapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cryptocurrencyapp.Model.ModelUser;
import com.example.cryptocurrencyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    TextView tvName, tvEmail;
    ProgressBar progressBar;
    Dialog dialog;
    TextInputEditText etName;
    LinearLayout show_edit_email, Show_Change_password, Show_Delete_User;

    private TextInputEditText textViewAuthenticated;
    private String userOldEmail, userNewEmail, userPassword;
    private Button buttonUpdateEmail;
    private CardView buttonUpdateEmailCv;
    private TextInputEditText editTextNewEmail, editTextPassword;
    private TextView tvAuthenticated, tvAuthenticatedTt;


    private TextInputEditText editTextPasswordCurrent, editTextPasswordNew, editTextPasswordConfirm;
    private TextView textViewAuthenticatedCP;
    private Button buttonChangePassword, buttonReAuthenticate,map;
    private CardView buttonChangePasswordCardView;
    private String userPasswordCurrent;


    private TextInputEditText editTextUserPassword;
    private TextView textViewAuthenticateDu;
    private String userPasswordDu;
    private Button buttonReAuthenticateDu, buttonDeleteUser,button_ExitUser;
    private CardView buttonDeleteCardView;

    private BottomNavigationView nav;

    public UserProfileActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        createNotificationChannel();
        //init_screen();

        dialog = new Dialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        tvName = findViewById(R.id.name);
        tvEmail = findViewById(R.id.email);
        progressBar = findViewById(R.id.progressBar);

        showUserProfile(firebaseUser);

        show_edit_email = findViewById(R.id.Show_edit_email);
        Show_Change_password = findViewById(R.id.Show_Change_password);
        Show_Delete_User = findViewById(R.id.Show_Delete_Account);

        //profil düzenleme butonu
        Button editProfile = (Button) findViewById(R.id.button_editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditProfileDialog();
                show_edit_email.setVisibility(View.GONE);
                Show_Change_password.setVisibility(View.GONE);
                Show_Delete_User.setVisibility(View.GONE);
            }
        });

        //mail düzenleme butonu
        Button SheditEmail = (Button) findViewById(R.id.button_editEmail);
        SheditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_edit_email.setVisibility(View.VISIBLE);
                Show_Change_password.setVisibility(View.GONE);
                Show_Delete_User.setVisibility(View.GONE);
                editEmail();
            }
        });

        //şifreyi değiştirme butonu
        Button changePasswordBtn = (Button) findViewById(R.id.button_changePassword);
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_edit_email.setVisibility(View.GONE);
                Show_Change_password.setVisibility(View.VISIBLE);
                Show_Delete_User.setVisibility(View.GONE);
                changePassword();
            }
        });

        //hesabı silme butonu
        Button DeleteAccountBtn = (Button) findViewById(R.id.button_DeleteUser);
        DeleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_edit_email.setVisibility(View.GONE);
                Show_Change_password.setVisibility(View.GONE);
                Show_Delete_User.setVisibility(View.VISIBLE);
                deleteAccount();
            }
        });

        Button ExitAccountBtn=(Button) findViewById(R.id.button_ExitUser);
        ExitAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button map=(Button) findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });




        nav = findViewById(R.id.bottomNavigationView);
        nav.setSelectedItemId(R.id.nav_user);

        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    finish();
                    return true;
                case R.id.nav_market:
                    startActivity(new Intent(getApplicationContext(),CryptoActivity.class));
                    finish();
                    return true;
                case R.id.nav_user:
                    return true;
            }
            return false;
        });

    }


    //profil bölümünü göster
    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser modelUser = snapshot.getValue(ModelUser.class);
                if (modelUser != null) {
                    String name = "" + snapshot.child("name").getValue();
                    String email = "" + snapshot.child("email").getValue();

                    tvName.setText("İsim : " + name);
                    tvEmail.setText("E-posta : " + email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Bir şey yanlış!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openEditProfileDialog() {
        dialog.setContentView(R.layout.edit_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        firebaseAuth = firebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        TextInputLayout textInputLayout = dialog.findViewById(R.id.text_input_name);
        textInputLayout.setHintEnabled(false);

        etName = dialog.findViewById(R.id.name);
        String userID = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser modelUser = snapshot.getValue(ModelUser.class);
                if (modelUser != null) {
                    String name = "" + snapshot.child("name").getValue();
                    etName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Bir şey yanlış!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        Button buttonUpdateProfile = dialog.findViewById(R.id.ButtonUpdateProfile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(firebaseUser);
            }
        });

        Button cancelBtn = dialog.findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        showUserProfile(firebaseUser);
        dialog.show();
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        String Name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(Name)) {
            etName.setError("Lütfen adınızı giriniz.");
            etName.requestFocus();
        } else {
            HashMap<String, Object> result = new HashMap<>();
            result.put("name", Name);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
            String userID = firebaseUser.getUid();
            databaseReference.child(userID).updateChildren(result)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(Name).build();
                            firebaseUser.updateProfile(profileChangeRequest);
                            Toast.makeText(UserProfileActivity.this, "Profil düzenlendi.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            restartApp();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //email bölümünü göster
    private void editEmail() {
        editTextPassword = findViewById(R.id.editText_update_password_v);
        editTextNewEmail = findViewById(R.id.editText_update_email_new);
        textViewAuthenticated = findViewById(R.id.textView_update_email_old);
        buttonUpdateEmail = findViewById(R.id.ButtonUpdateEmail);
        textViewAuthenticated.setOnTouchListener(otl);
        buttonUpdateEmailCv = findViewById(R.id.buttonUpdateEmailCardView);
        tvAuthenticated = findViewById(R.id.textView_update_email_new_header);
        tvAuthenticatedTt = findViewById(R.id.textView_update_email_authenticated_a);

        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        userOldEmail = firebaseUser.getEmail();
        textViewAuthenticated.setText(userOldEmail);

        if (firebaseUser.equals("")) {
            Toast.makeText(this, "Bir sorun var! Kullanıcı ayrıntıları yok.", Toast.LENGTH_SHORT).show();
        } else {
            reAuthenticate(firebaseUser);
        }
    }

    private View.OnTouchListener otl = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    };

    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser = findViewById(R.id.Button_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userPassword = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(userPassword)) {
                    Toast.makeText(UserProfileActivity.this, "Devam etmek için şifre gereklidir.",
                            Toast.LENGTH_SHORT).show();
                    editTextPassword.setError("Kimlik doğrulama için lütfen şifrenizi girin.");
                    editTextPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPassword);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                //kullanıcının kimliğinin doğrulandığını gösteren metni ayarla
                                tvAuthenticated.setText("Kontrol edildi.");
                                tvAuthenticatedTt.setText("Şifre onaylandı.");
                                Toast.makeText(UserProfileActivity.this, "Şifre onaylandı."
                                        + "E-posta adresini değiştirebilirsin.", Toast.LENGTH_SHORT).show();

                                editTextNewEmail.setEnabled(true);
                                editTextPassword.setEnabled(false);
                                buttonVerifyUser.setEnabled(false);
                                buttonUpdateEmail.setEnabled(true);

                                buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UserProfileActivity.this,
                                        R.color.purple_200));

                                buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        userNewEmail = editTextNewEmail.getText().toString().trim();
                                        if (TextUtils.isEmpty(userNewEmail)) {
                                            editTextNewEmail.setError("Lütfen değiştirmek istediğiniz e-postayı girin.");
                                            editTextNewEmail.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                            editTextPassword.setError("Lütfen e-postayı girin.");
                                            editTextPassword.requestFocus();
                                        } else if (userOldEmail.matches(userNewEmail)) {
                                            editTextPassword.setError("Lütfen yeni e-posta adresinizi girin.");
                                            editTextPassword.requestFocus();
                                        } else {
                                            progressBar.setVisibility(View.VISIBLE);
                                            updateValueEmail();

                                        }
                                    }
                                });

                                buttonUpdateEmailCv.setCardBackgroundColor(ContextCompat.getColorStateList(
                                        UserProfileActivity.this, R.color.purple_200));
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(UserProfileActivity.this, "Lütfen şifrenizi doğru girdiğinizden emin olun.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateValueEmail() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", userNewEmail);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateEmail(firebaseUser);
                Toast.makeText(UserProfileActivity.this, "E-posta bilgileri güncellendi."
                                + "Eski e-postanıza geri dönmek istiyorsanız lütfen e-posta mesajınızı kontrol edin."
                        , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfileActivity.this, "E-posta bilgileriniz düzenlenemiyor. Lütfen tekrar deneyin."
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UserProfileActivity.this, "E-posta güncellendi lütfen yeni e-posta adresinizi doğrulayın."
                            + "E-postanızı doğrulamak için lütfen tekrar giriş yapın.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    //şifre bölümünü göster
    private void changePassword(){
        editTextPasswordNew = findViewById(R.id.editText_change_password_new);
        editTextPasswordCurrent = findViewById(R.id.editText_change_password_current);
        editTextPasswordConfirm = findViewById(R.id.editText_change_password_new_confirm) ;
        textViewAuthenticatedCP = findViewById(R.id.textView_change_password_authenticated) ;
        buttonReAuthenticate = findViewById(R.id.Button_change_password_authenticate_user);
        buttonChangePassword = findViewById(R.id.ButtonChangePassword);
        buttonChangePasswordCardView = findViewById(R.id.ButtonChangePasswordCardView);

        editTextPasswordNew.setEnabled(false);
        editTextPasswordConfirm.setEnabled(false);

        if (firebaseUser.equals("")){
            Toast.makeText(this, "Bir sorun var! Kullanıcı ayrıntıları yok.", Toast.LENGTH_SHORT).show();
        }else {
            reAuthenticatePassword(firebaseUser);
        }
    }

    private void reAuthenticatePassword(FirebaseUser firebaseUser) {

        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPasswordCurrent=editTextPasswordCurrent.getText().toString().trim();
                String checkPassword = "^(?=\\S+$).{6,20}$";

                if (TextUtils.isEmpty(userPasswordCurrent)){
                    Toast.makeText(UserProfileActivity.this, "Şifre Gerekli", Toast.LENGTH_SHORT).show();
                    editTextPasswordCurrent.setError("Lütfen geçerli parolanızı girin kimlik doğrulamasını yapın.");
                    editTextPasswordCurrent.requestFocus();
                } else if (!userPasswordCurrent.matches(checkPassword)) {
                    editTextPasswordCurrent.setError("Lütfen 6 karakter veya daha uzun bir şifre giriniz.");
                    editTextPasswordCurrent.requestFocus();
                }
                progressBar.setVisibility(View.VISIBLE);
                AuthCredential credential=EmailAuthProvider.getCredential(firebaseUser.getEmail(),userPasswordCurrent);
                firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            //mevcut şifre için düzenleme metnini devre dışı bırak / yeni şifre için edittext'i etkinleştirin ve yeni şifreyi onaylayın
                            editTextPasswordCurrent.setEnabled(false);
                            editTextPasswordNew.setEnabled(true);
                            editTextPasswordConfirm.setEnabled(true);
                            //şifre değiştirme düğmesini etkinleştir /kimlik doğrulama düğmesini devre dışı bırak
                            buttonReAuthenticate.setEnabled(false);
                            buttonChangePassword.setEnabled(true);
                            //kullanıcının kimliğinin doğrulandığını göster / doğrulama
                            textViewAuthenticatedCP.setText("Kontrol edildiniz/Onaylandınız"+ "Şifrenizi anında değiştirebilirsiniz!");
                            Toast.makeText(UserProfileActivity.this, "Şifre onaylandı."+
                                    "Yeni şifreyi değiştir.", Toast.LENGTH_SHORT).show();
                            //şifre butonu renk değişimi
                            buttonChangePassword.setBackgroundTintList(ContextCompat.getColorStateList(
                                    UserProfileActivity.this,R.color.purple_200));

                            buttonChangePassword.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    changePasswordConfirm(firebaseUser);
                                }
                            });
                        }else {
                            try {
                                throw task.getException();
                            }catch (Exception e){
                                Toast.makeText(UserProfileActivity.this, "Yanlış şifre.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

    }

    private void changePasswordConfirm(FirebaseUser firebaseUser) {
        String userPasswordNew = editTextPasswordNew.getText().toString().trim();
        String userPasswordConfirmNew = editTextPasswordConfirm.getText().toString().trim();
        String checkPassword = "^(?=\\S+$).{6,20}$";
        if (TextUtils.isEmpty(userPasswordNew)) {
            Toast.makeText(this, "Şifre gerekli.", Toast.LENGTH_SHORT).show();
            editTextPasswordNew.setError("Lütfen yeni şifreyi giriniz.");
            editTextPasswordNew.requestFocus();
        } else if (!userPasswordNew.matches(checkPassword)) {
            editTextPasswordConfirm.setError("Lütfen 6 karakter veya daha uzun bir şifre giriniz.");
            editTextPasswordConfirm.requestFocus();
        } else if (TextUtils.isEmpty(userPasswordConfirmNew)) {
            Toast.makeText(this, "Lütfen yeni şifreyi onaylayınız.", Toast.LENGTH_SHORT).show();
            editTextPasswordConfirm.setError("Lütfen yeni şifreyi tekrar giriniz.");
            editTextPasswordConfirm.requestFocus();
        } else if (!userPasswordConfirmNew.matches(checkPassword)) {
            editTextPasswordConfirm.setError("Lütfen eşleştirmeyin");
            editTextPasswordConfirm.requestFocus();
        } else if (!userPasswordNew.matches(userPasswordConfirmNew)) {
            Toast.makeText(this, "Lütfen eşleştirmeyin", Toast.LENGTH_SHORT).show();
            editTextPasswordConfirm.setError("Lütfen aynı şifreyi tekrar girin.");
            editTextPasswordConfirm.requestFocus();
        } else if (!userPasswordCurrent.matches(userPasswordCurrent)) {
            Toast.makeText(this, "Yeni şifre eski şifre ile aynı olmamalıdır.",
                    Toast.LENGTH_SHORT).show();
            editTextPasswordNew.setError("Lütfen yeni şifreyi giriniz.");
            editTextPassword.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            firebaseUser.updatePassword(userPasswordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserProfileActivity.this, "Şifre değiştirildi.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(UserProfileActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(UserProfileActivity.this, "CHANNEL_ID")
                                .setContentTitle("Coinfinity")
                                .setSmallIcon(R.drawable.bitcoin)
                                .setContentText("Şifreniz güncellendi!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(UserProfileActivity.this);

                        if (ActivityCompat.checkSelfPermission(UserProfileActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        notificationManager.notify(1, builder.build());

                        startActivity(intent);
                        finish();



                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", "name", importance);
            channel.setDescription("description");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //hesap silme bölümünü göster
    private void deleteAccount() {
        editTextUserPassword = findViewById(R.id.editText_Delete_User);
        textViewAuthenticateDu = findViewById(R.id.textView_Delete_User_Authenticated);
        buttonDeleteUser = findViewById(R.id.ButtonDeleteUser);
        buttonReAuthenticateDu = findViewById(R.id.Button_delete_user_authenticate);
        buttonDeleteCardView = findViewById(R.id.ButtonDeleteCardView);


        buttonDeleteUser.setEnabled(false);
        if (firebaseUser.equals("")) {
            Toast.makeText(this, "Bir sorun var! Kullanıcı ayrıntıları yok.", Toast.LENGTH_SHORT).show();
        }
        reAuthenticateDelete(firebaseUser);
    }

    private void reAuthenticateDelete(FirebaseUser firebaseUser) {
        buttonReAuthenticateDu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPasswordDu = editTextUserPassword.getText().toString();
                if (TextUtils.isEmpty(userPasswordDu)) {
                    Toast.makeText(UserProfileActivity.this, "Şifre gerekli.", Toast.LENGTH_SHORT).show();
                    editTextUserPassword.setError("Kimlik doğrulaması için lütfen mevcut şifrenizi girin.");
                    editTextUserPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    //yeni kullanıcının kimliğini doğrula
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPasswordDu);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                //şifre kısmı devre dışı
                                editTextUserPassword.setEnabled(false);

                                //hesap sil butonu aktif/doğrulama butonu pasif
                                buttonReAuthenticateDu.setEnabled(false);
                                buttonDeleteUser.setEnabled(true);

                                //kullanıcının kimliğinin doğrulandığını göster/doğrulandı
                                textViewAuthenticateDu.setText("Kontrol edildi." +
                                        "Hesabınızı hemen iptal edebilirsiniz. Lütfen dikkatli olun," +
                                        "hesabınızı bir daha kurtaramayacaksınız.");
                                Toast.makeText(UserProfileActivity.this, "Şifre onaylandı.", Toast.LENGTH_SHORT).show();

                                //hesap silme butonu renk değişimi
                                buttonDeleteCardView.setBackgroundTintList(ContextCompat.getColorStateList(
                                        UserProfileActivity.this, R.color.purple_200));

                                buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showAlertDialog();
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(UserProfileActivity.this, "Lütfen şifrenizi doğru giriniz.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });
    }

    private void showAlertDialog() {
        //Alert builder setup
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Kullanıcı Hesabını Sil");
        builder.setMessage("Profilinizi ve ilgili bilgileri silmekistiyor musunuz?" +
                "Verilerinizi bir daha asla kurtaramayacaksınız!");

        //kullanıcı tıklarsa e-posta uygulamalarını aç/yukarı devam düğmesi
        builder.setPositiveButton("Onayla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteUserData();
            }
        });

        //create alertdialog
        final AlertDialog alertDialog = builder.create();

        //kullanıcı profili etkinliğine geri dön kullanıcı iptal düğmesine basar
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        //alertdialog aç
        alertDialog.show();
    }

    private void deleteUserData() {
        //gerçek zamanlı veritabanından verileri sil
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                deleteUser(firebaseUser);
                Toast.makeText(UserProfileActivity.this, "Kullanıcı hesabı başarıyla silindi.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser(FirebaseUser firebaseUser) {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseAuth.signOut();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(UserProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //restart activity
    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        startActivity(intent);
        finish();
    }

    //initscreen
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



    //geri basıldığında
    int backPressed = 0;

    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed == 1) {
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }

}