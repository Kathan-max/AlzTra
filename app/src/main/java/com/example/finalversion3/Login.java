package com.example.finalversion3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {
    EditText UEmail, UPassword;
    Button LogBtn;
    TextView RegLink;
    FirebaseAuth fAuth;
    boolean passwordVisible;
    ProgressBar progressBar;


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UEmail = findViewById(R.id.userEmail);
        UPassword = findViewById(R.id.userPassword);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        LogBtn = findViewById(R.id.LoginButton);
        RegLink = findViewById(R.id.registerLink);

        UPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    if(event.getRawX()>=UPassword.getRight()-UPassword.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = UPassword.getSelectionEnd();
                        if(passwordVisible){
                            UPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off_24,0);
                            UPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }else{
                            UPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_24,0);
                            UPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        UPassword.setSelection(selection);
                    }
                }

                return false;
            }
        });

        LogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = UEmail.getText().toString().trim();
                String password = UPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    UEmail.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    UPassword.setError("Password is Required.");
                    return;
                }

                if(password.length()<6){
                    UPassword.setError("Password Must be greater than 6 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Authenticate the User

                try {
                    fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                Dashboard.dofirstTime();
                                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                finish();
                            } else {
                                Toast.makeText(Login.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(Login.this, "Wrong Password or Email address.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        RegLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
                finish();
            }
        });
    }
}