package com.example.gameescalator.User;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gameescalator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity{

    private EditText emaile;
    private Button reset;
    FirebaseAuth auth;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.reset_password);

        emaile = (EditText) findViewById(R.id.recover_password_email_edit_text);
        reset = (Button) findViewById(R.id.submitreset);
        auth = FirebaseAuth.getInstance();

        reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                resetpassword();

            }
        });

    }
    private void resetpassword() {

        String email = emaile.getText().toString().trim();

        if(email.isEmpty()){
            emaile.setError("Email Is Required!");
            emaile.requestFocus();
            return;
        }
//        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//            emaile.setError("Please provide a valid email!");
//            emaile.requestFocus();
//            return;
//        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgetPassword.this, "Check Your Email!", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    emaile.setError("Please provide a valid email!");
                    emaile.requestFocus();
                }
            }
        });
    }
}
