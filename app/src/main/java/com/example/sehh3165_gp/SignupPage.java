package com.example.sehh3165_gp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupPage extends AppCompatActivity implements View.OnClickListener {

    EditText email, passwd, confirmPw, username;
    Button signUp, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        email = findViewById(R.id.editTextTextEmailAddress);
        passwd = findViewById(R.id.editTextTextPassword);
        confirmPw = findViewById(R.id.editTextTextConfirmPassword);
        username = findViewById(R.id.editTextText_name);

        signUp = findViewById(R.id.button_SignUp);
        back = findViewById(R.id.button_GoToSignIn);
        signUp.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private boolean checkExist(String email) {
        DatabaseHelper DB = new DatabaseHelper(this);
        return DB.checkRecord(email);
    }

    private boolean register(String email, String passwd, String username) {
        DatabaseHelper DB = new DatabaseHelper(this);
        return DB.inputData(email, passwd, username);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_SignUp) {
            if (email.getText().toString().isEmpty() || passwd.getText().toString().isEmpty() || confirmPw.getText().toString().isEmpty() || username.getText().toString().isEmpty())
                Toast.makeText(SignupPage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            else if (checkExist(email.getText().toString()))
                Toast.makeText(SignupPage.this, "Email exist, please login", Toast.LENGTH_SHORT).show();
            else if (!passwd.getText().toString().equals(confirmPw.getText().toString()))
                Toast.makeText(SignupPage.this, "Password not match", Toast.LENGTH_SHORT).show();
            else if (register(email.getText().toString(), passwd.getText().toString(), username.getText().toString())) {
                Toast.makeText(SignupPage.this, "SignUp Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignupPage.this, LoginPage.class));
            }
            else
                Toast.makeText(SignupPage.this, "SignUp Fail", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.button_GoToSignIn) {
            startActivity(new Intent(SignupPage.this, LoginPage.class));
        }
    }
}
