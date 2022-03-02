package com.example.gameescalator.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.gameescalator.Classes.CommentUser;
import com.example.gameescalator.Classes.User;
import com.example.gameescalator.Home.GameDetails;
import com.example.gameescalator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText regemail, regpassword, regname;
    private Button regregisteruser;
    private ProgressBar progressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_user);


        regregisteruser = (Button) findViewById(R.id.registeruser);
        regregisteruser.setOnClickListener(this);

        regemail = (EditText) findViewById(R.id.email);
        regpassword = (EditText) findViewById(R.id.password);
        regname = (EditText) findViewById(R.id.name);
        progressBar = (progressBar) = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {

            if (v.getId() == R.id.registeruser) {
                registerUser();
                startActivity(new Intent(this, MainActivity.class));
            }

    }

    private void registerUser() {
        String email = regemail.getText().toString().trim();
        String name = regname.getText().toString().trim();
        String password = regpassword.getText().toString().trim();

        if (name.isEmpty()) {
            regname.setError("Name Required");
            return;
        }
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

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(name,password,email);
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth
                                    .getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                    }else{
                                        Toast.makeText(RegisterUser.this,"Failed to Register",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.VISIBLE);
                        }
                   }});
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();
                            mAuth.getCurrentUser()
                                .updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                    }
                                }
                            });
                }
             }
         });
      }

 }