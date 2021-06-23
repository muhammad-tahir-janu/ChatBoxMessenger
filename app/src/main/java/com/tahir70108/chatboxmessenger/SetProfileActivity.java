package com.tahir70108.chatboxmessenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SetProfileActivity extends AppCompatActivity {

    private CardView cvGetUserImageCardView;
    private ImageView ivGetUserImageview;

    private final int PICK_IMG =123;
    private Uri imagePath;
    private EditText etGetUserName;
    private Button btnSaveProfile;
    private FirebaseAuth firebaseAuth;
    private String name;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private  String imageUriAccessToken;
    private FirebaseFirestore firebaseFirestore;
    ProgressBar progressBarOfSetProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        inital();
        cvGetUserImageCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMG);
            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etGetUserName.getText().toString().trim();
                if(name.isEmpty()){
                    etGetUserName.setError("Please Enter a Name");
                }else {
//                    progressBarOfSetProfile.setVisibility(View.VISIBLE);
                    sendDataForNewUser();
                   // progressBarOfSetProfile.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(SetProfileActivity.this,ChatActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });



    }

    private void sendDataForNewUser() {
        sendDataToRealTimeDataBase();

    }

    private void sendDataToRealTimeDataBase() {
        name = etGetUserName.getText().toString().trim();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        UserProfile userProfile = new UserProfile(name,firebaseAuth.getUid());
        databaseReference.setValue(userProfile);
        Toast.makeText(getApplicationContext(), "User Profile Added SuccessFully", Toast.LENGTH_SHORT).show();
        sendImagetoStorage();

    }

    private void sendImagetoStorage() {

    }

    private void sendDataToCloudFireStore() {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name",name);
        userData.put("image",imageUriAccessToken);
        userData.put("uid",firebaseAuth.getUid());
        userData.put("status","Online");

        documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Data On Cloud Fire Store send SuccessFull", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMG && resultCode==RESULT_OK){
            imagePath = data.getData();
            ivGetUserImageview.setImageURI(imagePath);
        }
    }

    private void inital() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage =FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore =FirebaseFirestore.getInstance();
        etGetUserName = findViewById(R.id.etGetUserName);
        ivGetUserImageview = findViewById(R.id.ivGetUserProfileImage);
        cvGetUserImageCardView = findViewById(R.id.cvGetUserImage);
        btnSaveProfile =findViewById(R.id.btnSetProfile);


    }
}