package com.example.sehh3165_gp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class LoginPage extends AppCompatActivity implements View.OnClickListener {

    EditText email, passwd;
    Button signIn, forgetPw, signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        email = findViewById(R.id.editTextTextEmailAddress);
        passwd = findViewById(R.id.editTextTextPassword);

        signIn = findViewById(R.id.button_SignIn);
        forgetPw = findViewById(R.id.button_ForgotPassword);
        signUp = findViewById(R.id.button_DontHaveAccount);
        signIn.setOnClickListener(this);
        forgetPw.setOnClickListener(this);
        signUp.setOnClickListener(this);

        DatabaseHelper DB = new DatabaseHelper(this);
        String arr[][] = DB.getAllInfo();
        for (String[] strings : arr) {
            Log.wtf("hi2342", Arrays.toString(strings));
        }

    }

    private boolean validate(String email, String passwd) {
        DatabaseHelper DB = new DatabaseHelper(this);
        return DB.validate(email, passwd);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_SignIn) {
            if (email.getText().toString().isEmpty() || passwd.getText().toString().isEmpty())
                Toast.makeText(LoginPage.this, "Please fill in email/password", Toast.LENGTH_SHORT).show();
            else if (validate(email.getText().toString(), passwd.getText().toString())) {
                email.setText("");
                passwd.setText("");
                Intent i = new Intent(LoginPage.this, LobbyPage.class);
                i.putExtra("Email", email.getText().toString());
                startActivity(i);
            }
            else {
                Toast.makeText(LoginPage.this, "Email/Password invalid", Toast.LENGTH_SHORT).show();
                email.setText("");
                passwd.setText("");
            }
        }
        else if (v.getId() == R.id.button_ForgotPassword) {
            startActivity(new Intent(LoginPage.this, ResetPage.class));
        }
        else if (v.getId() == R.id.button_DontHaveAccount) {
            startActivity(new Intent(LoginPage.this, SignupPage.class));
        }
    }
}
