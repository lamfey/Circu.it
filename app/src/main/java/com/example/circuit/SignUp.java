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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    Button submit;
    FirebaseDatabase rootNode;
    FirebaseFirestore       db;
    private String  fname, lname, email, password, confirmpassword;
    EditText fnames, lnames, emails, passwords, confirmpasswords;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        db = FirebaseFirestore.getInstance();
        fnames = findViewById(R.id.firstnamesignup);
        lnames = findViewById(R.id.lastnamesignup);
        emails = findViewById(R.id.emailsignup);
        passwords = findViewById(R.id.passwordsignup);
        confirmpasswords = findViewById(R.id.confirmpasswordsignup);
        submit = findViewById(R.id.signupbutton);
        mAuth = FirebaseAuth.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstname = fnames.getText().toString();
                String lastname = lnames.getText().toString();
                String emaili = emails.getText().toString();
                String passw = passwords.getText().toString();
                String confpassw = confirmpasswords.getText().toString();
                String exp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

                if(firstname.isEmpty() && lastname.isEmpty() && passw.isEmpty() && emaili.isEmpty() && confpassw.isEmpty()){
                    Toast.makeText(SignUp.this, "Fields can not be empty", Toast.LENGTH_SHORT).show();
                }
                else if(passw.equals(confpassw)){
                    if(emaili.matches(exp)){

                        mAuth.createUserWithEmailAndPassword(emaili, passw).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(SignUp.this, "Success", Toast.LENGTH_SHORT).show();
                                    String userID  = mAuth.getCurrentUser().getUid();
                                    DocumentReference doc = db.collection("Users").document(userID);
                                    Map<String, Object> user  = new HashMap<>();
                                    user.put("FirstName", firstname);
                                    user.put("lastname", lastname);
                                    user.put("Email", emaili);
                                    doc.set(user);

                                    Intent myIntent = new Intent(SignUp.this, HomePage.class);
                                    startActivity(myIntent);
                                }
                                else{
                                    Toast.makeText(SignUp.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(SignUp.this, "Email is not valid", Toast.LENGTH_SHORT).show();
                    }
                }  else{
                    Toast.makeText(SignUp.this, "Passwords must match", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void logindirect(View view) {
        Intent myIntent = new Intent(this, Login.class);
        startActivity(myIntent);
    }
}