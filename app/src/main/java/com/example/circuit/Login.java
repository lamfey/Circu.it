package com.example.circuit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button submit;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        submit = findViewById(R.id.loginbutton);
        password = findViewById(R.id.loginpassword);
        password.setText("");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuth();
            }
        });
    }
    private void performAuth() {
        email = findViewById(R.id.loginemail);
        password = findViewById(R.id.loginpassword);
        mAuth = FirebaseAuth.getInstance();

        String emailX = email.getText().toString();
        String passwordX = password.getText().toString();


        if(emailX.isEmpty() || passwordX.isEmpty()){
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else{
            mAuth.signInWithEmailAndPassword(emailX, passwordX)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Login.this, "Success", Toast.LENGTH_SHORT).show();

                                Intent myIntent = new Intent(Login.this, HomePage.class);
                                startActivity(myIntent);
                            }
                            else{
                                Toast.makeText(Login.this, "Email and/or Password is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
    public void signup(View view) {
        Intent myIntent = new Intent(this, SignUp.class);
        startActivity(myIntent);
    }
    public void forgotpassword(View view) {
        Intent myIntent = new Intent(this, Password_reset.class);
        startActivity(myIntent);
    }

}