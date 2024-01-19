package com.example.circuit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class Password_reset extends AppCompatActivity {
    private FirebaseAuth mAuth;

    Button submit;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
    }

    public void resetpassword(View view) {
        email = findViewById(R.id.resetemail);
        mAuth = FirebaseAuth.getInstance();
        String Email = email.getText().toString();

        mAuth.sendPasswordResetEmail(Email)
                .addOnFailureListener(Password_reset.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Password_reset.this, "User does not exist", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(Password_reset.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Password_reset.this, "change sucess", Toast.LENGTH_SHORT).show();

                        Intent myIntent = new Intent(Password_reset.this, Login.class);
                        startActivity(myIntent);
                    }
                });
    }
}