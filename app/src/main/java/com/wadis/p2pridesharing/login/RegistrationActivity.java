package com.wadis.p2pridesharing.login;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wadis.p2pridesharing.MainActivity;
import  com.wadis.p2pridesharing.R;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;


import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;

public class RegistrationActivity extends AppCompatActivity {

    private com.google.android.material.textfield.TextInputLayout mEmail, mPassword;
    private FirebaseAuth mAuth;
    private Button  mRegistration;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mEmail =   findViewById(R.id.textInputEmail);
        mPassword =  findViewById(R.id.textInputPassword);
        mAuth = FirebaseAuth.getInstance();

        mRegistration = (Button) findViewById(R.id.cirRegisterButton);


        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getEditText().getText().toString();
                final String password = mPassword.getEditText().getText().toString();
                Log.d("email", email);
                Log.d("password", password);

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(RegistrationActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
                        }else{
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("name");
                            current_user_db.setValue(email);
                            Intent intent=new Intent(RegistrationActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();


                        }
                    }
                });
            }
        });

    }
    public void onLoginClick(View View){
        Intent intent=new Intent(RegistrationActivity.this, DriverLoginActivity.class);
        startActivity(intent);
        finish();


    }

}