package com.example.sehh3165_gp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginPage extends AppCompatActivity implements View.OnClickListener {

    DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
    SQLiteDatabase read_db = dbHelper.getReadableDatabase();

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
    }

    private boolean validate(String email, String passwd) {
        if (email.isEmpty() && passwd.isEmpty()) {
            Cursor cursor = read_db.query(dbHelper.TABLE_NAME);
        }
        else
            return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_SignIn) {
            if (validate(email.getText().toString(), passwd.getText().toString()))
                startActivity(new Intent(LoginPage.this, Lobby.class));
            else
                toggle
        }
        else if (v.getId() == R.id.button_ForgotPassword) {
            startActivity(new Intent(LoginPage.this, ResetPage.class));
        }
        else if (v.getId() == R.id.button_DontHaveAccount) {
            startActivity(new Intent(LoginPage.this, SignupPage.class));
        }
    }
}
