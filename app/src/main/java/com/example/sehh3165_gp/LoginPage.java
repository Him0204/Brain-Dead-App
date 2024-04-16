package com.example.sehh3165_gp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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
        DB.inputData("Test1", "abc", "Test1");
        DB.inputData("Test3", "abc", "Test3");
        DB.inputData("Test5", "abc", "Test5");
        DB.inputData("Test2", "abc", "Test2");
        DB.inputData("Test4", "abc", "Test4");
        DB.updateStatus("Test1", "1","253");
        DB.updateStatus("Test5", "6","572");
        DB.updateStatus("Test3", "4","447");
        DB.updateStatus("Test2", "4","273");
        DB.updateStatus("Test4", "8","495");
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
                Intent i = new Intent(LoginPage.this, LobbyPage.class);
                i.putExtra("Email", email.getText().toString());
                email.setText("");
                passwd.setText("");
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
