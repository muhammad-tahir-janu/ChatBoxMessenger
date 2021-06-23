package com.tahir70108.chatboxmessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    EditText etUserName;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    TextView tvMoveToUpdateProfile;

    FirebaseFirestore firebaseFirestore;
    ImageView userImageView;
    StorageReference storageReference;
    private  String imageAccessToken;
    FirebaseStorage firebaseStorage;
    androidx.appcompat.widget.Toolbar mToolbarOfViewProfile;

    ImageButton backButtonOfViewProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initial();
        setSupportActionBar(mToolbarOfViewProfile);
        backButtonOfViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        storageReference =FirebaseStorage.getInstance().getReference();
        storageReference.child("Images")
                .child(Objects.requireNonNull(firebaseAuth.getUid()))
                .child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageAccessToken =uri.toString();
                Picasso.get().load(uri).into(userImageView);
            }
        });

        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile userProfile =snapshot.getValue(UserProfile.class);
                etUserName.setText(userProfile.getUserName());
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed To Fetch", Toast.LENGTH_SHORT).show();
            }
        });

        tvMoveToUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,UserProfileChange.class);
                intent.putExtra("name",etUserName.getText().toString());
                startActivity(intent);
            }
        });




    }

    private void initial() {
        etUserName = findViewById(R.id.etChangeUserName);
        userImageView = findViewById(R.id.ivChangeUserProfileImage);
        tvMoveToUpdateProfile = findViewById(R.id.tvMoveToUpdateProfile);
        mToolbarOfViewProfile = findViewById(R.id.toolbarOfViewProfileActivity);
        firebaseFirestore =FirebaseFirestore.getInstance();
        backButtonOfViewProfile = findViewById(R.id.backButtonOfProfileActivity);
        firebaseDatabase =FirebaseDatabase.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseStorage =FirebaseStorage.getInstance();

    }
}