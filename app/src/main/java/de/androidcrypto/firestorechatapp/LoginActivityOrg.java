package de.androidcrypto.firestorechatapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivityOrg extends AppCompatActivity implements View.OnClickListener {
    Button btnLogin;
    TextView txtSignup;
    EditText edtEmail;
    EditText edtPassword;
    ActionBar actionBar;
    FirebaseAuth auth;
    FirebaseUser user;

    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        txtSignup = findViewById(R.id.txtSignUp);
        txtSignup.setOnClickListener(this);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                login();
                break;
            case R.id.txtSignUp:
                startActivity(new Intent(this, SignupActivity.class));
                break;
        }
    }

    private void login() {
        email = edtEmail.getText().toString().trim();
        password = edtPassword.getText().toString().trim();

        /*
        Check for empty fields to avoid exception
         */
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Enter email", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter password", Toast.LENGTH_LONG).show();
            return;
        }
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            user = auth.getCurrentUser();
                            Toast.makeText(LoginActivityOrg.this, "Login successful", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivityOrg.this, MainActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(LoginActivityOrg.this, "Login unsuccessful. Check details", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
