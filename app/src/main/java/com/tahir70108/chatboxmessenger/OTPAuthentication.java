package com.tahir70108.chatboxmessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OTPAuthentication extends AppCompatActivity {
    TextView tvChangeOrWrongNumber;
    EditText etOTP;
    Button btnVerifyOTP;
    String enteredOTP;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBarOfOTPAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpauthentication);
        initial();

        tvChangeOrWrongNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OTPAuthentication.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredOTP = etOTP.getText().toString().trim();
                if(enteredOTP.isEmpty()){
                    etOTP.setError("Enter Your OTP First");
                }else {
                    progressBarOfOTPAuth.setVisibility(View.VISIBLE);
                    String receivedOTP =getIntent().getStringExtra("otp");

                    PhoneAuthCredential credential  = PhoneAuthProvider.getCredential(receivedOTP,enteredOTP);
                    signInWithPhoneAuthCredential(credential);

                }

            }
        });




    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBarOfOTPAuth.setVisibility(View.INVISIBLE);
                    Toast.makeText(OTPAuthentication.this, "Login success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OTPAuthentication.this,SetProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    if(task.getException() instanceof FirebaseAuthActionCodeException){
                        progressBarOfOTPAuth.setVisibility(View.INVISIBLE);
                        Toast.makeText(OTPAuthentication.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }


    private void initial() {
        tvChangeOrWrongNumber = findViewById(R.id.tvChangeOrWrongNumber);
        etOTP = findViewById(R.id.etUserOTP);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
        progressBarOfOTPAuth = findViewById(R.id.progressBarOFOTPAuth);

        firebaseAuth = FirebaseAuth.getInstance();
    }

}