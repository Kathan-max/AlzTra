package com.example.finalversion3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    EditText UFullName, UEmail, UPassword;
    Button RegisterBtn;
    Button AdminRegisterBtn;
    Button CaretakerBtn;
    TextView LoginLink;
    FirebaseAuth fAuth;
    DatabaseReference DBref;
    String UName;
    ProgressBar progressBar;


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        UFullName = findViewById(R.id.fullName);
        UEmail = findViewById(R.id.userEmail);
        UPassword = findViewById(R.id.userPassword);
        AdminRegisterBtn = findViewById(R.id.admin);
        CaretakerBtn = findViewById(R.id.caretaker);
        LoginLink = findViewById(R.id.loginLink);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),Dashboard.class));
            finish();
        }

        CaretakerBtn.setOnClickListener(new View.OnClickListener() {
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


                UName="";
                //Get username from email id
                for (int i=0; email.charAt(i)!='@'; i++)
                {
                    UName = UName + email.charAt(i);
                }

                // Register the User in the Firebase

                String finalUsername = UName;

                try {

                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                DBref = FirebaseDatabase.getInstance().getReference().child("Hosts");
                                String Hostname = UFullName.getText().toString();
                                AddHost host = new AddHost(Hostname);
                                DBref.child(finalUsername).setValue(host);
                                Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                                Dashboard.activityResumed();
                                Dashboard.dofirstTime();
                                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                finish();
                            } else {
                                Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(Register.this, "User with that email address already exists please use another email address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AdminRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String email = UEmail.getText().toString().trim();
                    String password = UPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(email)) {
                        UEmail.setError("Email is Required.");
                        return;
                    }

                    if (TextUtils.isEmpty(password)) {
                        UPassword.setError("Password is Required.");
                        return;
                    }

                    if (password.length() < 6) {
                        UPassword.setError("Password Must be greater than 6 characters");
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);

                    String username = "";
                    //Get username from email id
                    for (int i = 0; email.charAt(i) != '@'; i++) {
                        if(email.charAt(i)!='.') {
                            username = username + email.charAt(i);
                        }
                    }

                    // Register the User in the Firebase

                    String finalUsername = username;

                    try {
                        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                DatabaseReference DBRef2;
                                DBRef2 = FirebaseDatabase.getInstance().getReference().child("Admin");
                                String hostname = UFullName.getText().toString();
                                String email = fAuth.getCurrentUser().getEmail().toString();
                                DBRef2.child(finalUsername).child("AdminEmail").setValue(email);
                                //AddHost host = new AddHost(hostname);
                                //DBRef2.child(finalUsername).setValue(host);
                                DBref = FirebaseDatabase.getInstance().getReference().child("Hosts");
                                String Hostname = UFullName.getText().toString();
                                AddHost host = new AddHost(Hostname);
                                DBref.child(finalUsername).setValue(host);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                                    Dashboard.activityResumed();
                                    Dashboard.dofirstTime();
                                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                    finish();
                                } else {
                                    Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }catch (Exception e){
                        Toast.makeText(Register.this, "User with that email address already exists please use another email address", Toast.LENGTH_SHORT).show();
                    }
                }
        });

        LoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });
    }
}