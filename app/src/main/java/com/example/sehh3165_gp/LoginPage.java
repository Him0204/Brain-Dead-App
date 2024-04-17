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
        DB.inputData("Michael.Smith@gmail.com", "Michael!123", "Michael");
        DB.inputData("Jessica.Jones@gmail.com", "Jessica!123", "Jessica");
        DB.inputData("Chris.Brown@gmail.com", "Chris!123", "Chris");
        DB.inputData("Sarah.Johnson@gmail.com", "Sarah!123", "Sarah");
        DB.inputData("David.Lee@gmail.com", "David!123", "David");
        DB.inputData("Emma.Wilson@gmail.com", "Emma!123", "Emma");
        DB.inputData("James.Taylor@gmail.com", "James!123", "James");
        DB.inputData("Sophia.Martinez@gmail.com", "Sophia!123", "Sophia");
        DB.inputData("William.Davis@gmail.com", "William!123", "William");
        DB.inputData("Olivia.Lewis@gmail.com", "Olivia!123", "Olivia");
        DB.updateStatus("Michael.Smith@gmail.com", "8","50000");
        DB.updateStatus("Jessica.Jones@gmail.com", "7","122000");
        DB.updateStatus("Chris.Brown@gmail.com.Jones@gmail.com", "7","123000");
        DB.updateStatus("Sarah.Johnson@gmail.com", "2","252000");
        DB.updateStatus("David.Lee@gmail.com", "2","253000");
        DB.updateStatus("Emma.Wilson@gmail.com", "0","0");
        DB.updateStatus("James.Taylor@gmail.com", "6","572000");
        DB.updateStatus("Sophia.Martinez@gmail.com", "4","447000");
        DB.updateStatus("William.Davis@gmail.com", "4","273000");
        DB.updateStatus("Olivia.Lewis@gmail.com", "8","447000");
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
                i.putExtra("email", email.getText().toString());
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

    @Override
    public void onPause() {
        super.onPause();
        stopService(new Intent(this, BackgroundMusic.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        startService(new Intent(this, BackgroundMusic.class));
    }
}
