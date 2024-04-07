package com.example.sehh3165_gp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPage extends AppCompatActivity implements View.OnClickListener {

    EditText email, passwd, confirmPw;
    Button reset, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset);

        email = findViewById(R.id.editTextTextEmailAddress);
        passwd = findViewById(R.id.editTextTextPassword);
        confirmPw = findViewById(R.id.editTextTextConfirmPassword);

        reset = findViewById(R.id.button_SignUp);
        back = findViewById(R.id.button_GoToSignIn);
        reset.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private boolean checkExist(String email) {
        DatabaseHelper DB = new DatabaseHelper(this);
        return DB.checkRecord(email);
    }

    private boolean reset(String email, String passwd) {
        DatabaseHelper DB = new DatabaseHelper(this);
        return DB.updatePw(email, passwd);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_SignUp) {
            if (email.getText().toString().isEmpty() || passwd.getText().toString().isEmpty() || confirmPw.getText().toString().isEmpty())
                Toast.makeText(ResetPage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            else if (!checkExist(email.getText().toString()))
                Toast.makeText(ResetPage.this, "Email not exist, please signup", Toast.LENGTH_SHORT).show();
            else if (!passwd.getText().toString().equals(confirmPw.getText().toString()))
                Toast.makeText(ResetPage.this, "Password not match", Toast.LENGTH_SHORT).show();
            else if (reset(email.getText().toString(), passwd.getText().toString())) {
                Toast.makeText(ResetPage.this, "Reset Successfully", Toast.LENGTH_SHORT).show();
                email.setText("");
                passwd.setText("");
                confirmPw.setText("");
                startActivity(new Intent(ResetPage.this, LoginPage.class));
            }
            else
                Toast.makeText(ResetPage.this, "Reset Fail", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.button_GoToSignIn) {
            startActivity(new Intent(ResetPage.this, LoginPage.class));
        }
    }
}
