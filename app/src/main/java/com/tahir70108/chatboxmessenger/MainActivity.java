package com.tahir70108.chatboxmessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

     EditText etUserPhoneNumber;
     Button btnSendOTP;
     CountryCodePicker mCountryCodePicker;
     String countryCode;
     String phoneNumberWithCountryCode;

     FirebaseAuth firebaseAuth;
     ProgressBar progressBarOfMain;

     PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
     String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initial();

        countryCode = mCountryCodePicker.getDefaultCountryCodeWithPlus();

        mCountryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode = mCountryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });

        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number;
                number = etUserPhoneNumber.getText().toString().trim();
                if (number.isEmpty()){
                    etUserPhoneNumber.setError("Please Enter A Number");
               /* }else if(number.length()<10){
                    etUserPhoneNumber.setError("Invalid Number");
                }else if(number.length()>10){
                    etUserPhoneNumber.setError("Invalid Number");
                    Toast.makeText(MainActivity.this, "Enter a valid Number Without Country Code", Toast.LENGTH_SHORT).show();
                */}else  {
                    Toast.makeText(MainActivity.this, number, Toast.LENGTH_SHORT).show();
                    progressBarOfMain.setVisibility(View.VISIBLE);
                    phoneNumberWithCountryCode = countryCode+number;

                    PhoneAuthOptions authOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(number)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(MainActivity.this)
                            .setCallbacks(callbacks)
                            .build();

                    PhoneAuthProvider.verifyPhoneNumber(authOptions);
                }
            }

        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull  PhoneAuthCredential phoneAuthCredential) {
                //  how to automatically fetch code
            }

            @Override
            public void onVerificationFailed(@NonNull  FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String verifyCode,  PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verifyCode, forceResendingToken);
                Toast.makeText(MainActivity.this, "OTP is Sent", Toast.LENGTH_SHORT).show();
                progressBarOfMain.setVisibility(View.INVISIBLE);
                verificationCode = verifyCode;

                Intent intent = new Intent(MainActivity.this,OTPAuthentication.class);
                intent.putExtra("otp",verificationCode);
                startActivity(intent);

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent = new Intent(MainActivity.this,ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
    }

    private void initial() {
        etUserPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSendOTP = findViewById(R.id.btnSendOTP);
        mCountryCodePicker = findViewById(R.id.countryPicker);
        progressBarOfMain = findViewById(R.id.progressBarOFOTP);
        firebaseAuth = FirebaseAuth.getInstance();

    }
}