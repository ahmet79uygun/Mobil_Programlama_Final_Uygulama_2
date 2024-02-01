package com.example.mobil_programlama_final_uygulama;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobil_programlama_final_uygulama.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    EditText etEmail, etPassword, etName, etlastName;
    Button btnSignup;
    TextView tvLogin;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPass);
        etName = findViewById(R.id.etName);
        etlastName = findViewById(R.id.etlastName);
        btnSignup = findViewById(R.id.bntSignup);
        tvLogin = findViewById(R.id.tvLogin);
        auth = FirebaseAuth.getInstance();

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String name = etName.getText().toString();
                String lastName = etlastName.getText().toString();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference ref = db.collection("UserModel"); // Firestore'da belirlenen koleksiyon adını kullanın

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String uid = task.getResult().getUser().getUid();
                                    Toast.makeText(getApplicationContext(), "Kayıt oldunuz", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Login.class));

                                    // Kullanıcı UID'si ile belirli bir belge oluşturun ve UserModel'i bu belgeye ekleyin
                                    DocumentReference userDocument = ref.document(uid);
                                    UserModel user = new UserModel(email, lastName, name);
                                    userDocument.set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Firestore'a başarıyla kaydedildi
                                                    Toast.makeText(Register.this, "Firestore'a kayıt başarılı", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Firestore'a kaydetme başarısız oldu
                                                    Toast.makeText(Register.this, "Firestore'a kayıt başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    Log.e("FirestoreKayitHata", "Firestore'a kayıt başarısız: " + e.getMessage());
                                                }
                                            });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Kayıt başarısız", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}