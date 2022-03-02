package com.example.gameescalator.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.gameescalator.Home.Home;
import com.example.gameescalator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
        private TextView register, forgetpass;
        private EditText regemail,regpassword;
        private Button regsignin;
        private FirebaseAuth mAuth;
        private ProgressBar progressBar;
        private FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        statusBarColor();

        register = (TextView)findViewById(R.id.register);
        register.setOnClickListener(this);

        regsignin = (Button) findViewById(R.id.signin);
        regsignin.setOnClickListener(this);
        regemail = (EditText) findViewById(R.id.email);
        regpassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        forgetpass = findViewById(R.id.forgetpass);

        forgetpass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                startActivity(new Intent(this,RegisterUser.class));
                break;
            case R.id.signin:
                userLogin();
                break;
            case R.id.forgetpass:
                startActivity(new Intent(this,ForgetPassword.class));
                break;
        }
    }

    private void userLogin() {
        String email = regemail.getText().toString();
        String password = regpassword.getText().toString();

        if (email.isEmpty()) {
            regemail.setError("Email Is Required");
            regemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            regemail.setError("Please provide valid email!");
            regemail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            regpassword.setError("Password Is Required");
            regpassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            regpassword.setError("Min Password length be 6 Chars!");
            return;
        }
        progressBar.setVisibility(View.GONE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.VISIBLE);
                    startActivity(new Intent(MainActivity.this, Home.class));
                    finish();
                }else{
                    Toast.makeText(MainActivity.this, "Failed to Login!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void statusBarColor(){
        if (Build.VERSION.SDK_INT >= 21) {
            Log.i("ColorApp","NiceTrue");
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.appColor));
        }
    }

    private void saveUsersData() {



    }

}