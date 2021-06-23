    package com.tahir70108.chatboxmessenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

    public class UserProfileChange extends AppCompatActivity {

    private EditText etUpdateUserName;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private FirebaseFirestore firebaseFirestore;
    private ImageView ivUpdateNewImageView;
    private StorageReference storageReference;
    private String imageAccessToken;
    private FirebaseStorage firebaseStorage;
    private androidx.appcompat.widget.Toolbar mToolbarOfUpdateProfile;

    private ImageButton backButtonOfUpdateProfile;
    private ProgressBar progressBarOfUpdateProfile;

    private Uri imagePath;

    private Button btnUpdateProfile;

    Intent intent;
    private final static int PICK_IMAGE=123;
    String newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_change);
        initial();

        setSupportActionBar(mToolbarOfUpdateProfile);
        backButtonOfUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //For Updating Only Name

        etUpdateUserName.setText(intent.getStringExtra("name"));

        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newName =etUpdateUserName.getText().toString().trim();
                if(newName.isEmpty()){
                   etUpdateUserName.setError("Name Can't be Empty");
                }else if(imagePath!=null){
                    //if user Select new Image
                     progressBarOfUpdateProfile.setVisibility(View.VISIBLE);
                     UserProfile userProfile = new UserProfile(newName,firebaseAuth.getUid());
                     databaseReference.setValue(userProfile);

                     updateImageToStorage();

                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    progressBarOfUpdateProfile.setVisibility(View.INVISIBLE);
                    Intent intent1 = new Intent(UserProfileChange.this,ChatActivity.class);
                    startActivity(intent1);
                    finish();

                }else {
                    progressBarOfUpdateProfile.setVisibility(View.VISIBLE);
                    UserProfile userProfile = new UserProfile(newName,firebaseAuth.getUid());
                    databaseReference.setValue(userProfile);

                    updateNameOnCloudFireStore();

                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    progressBarOfUpdateProfile.setVisibility(View.INVISIBLE);
                    Intent intent1 = new Intent(UserProfileChange.this,ChatActivity.class);
                    startActivity(intent1);
                    finish();


                }
            }
        });

        ivUpdateNewImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent2, PICK_IMAGE);
            }
        });





    }

    private void updateNameOnCloudFireStore() {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name",newName)    ;
        userData.put("image",imageAccessToken);
        userData.put("uid",firebaseAuth.getUid());
        userData.put("status","Online");

        documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });



        
    }

    private void updateImageToStorage() {

        storageReference = firebaseStorage.getReference();
        storageReference.child("Images")
                .child(Objects.requireNonNull(firebaseAuth.getUid()))
                .child("Profile Pic")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageAccessToken = uri.toString();
                        Picasso.get().load(uri).into(ivUpdateNewImageView);
                    }
                });

        StorageReference imagref =storageReference.child("Images")
                .child(Objects.requireNonNull(firebaseAuth.getUid()))
                .child("Profile Pic");
        //Image Compression
        Bitmap bitmap=null;
        try{
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
        }catch (IOException e){
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] data =byteArrayOutputStream.toByteArray();

        ///putting image to storage

        UploadTask uploadTask = imagref.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageAccessToken = uri.toString();
                        Toast.makeText(getApplicationContext(), "URT get Success", Toast.LENGTH_SHORT).show();
                        updateImageToStorage();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "URT get Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getApplicationContext(), "Image is Updated  ", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Image not Updated", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void initial() {
        mToolbarOfUpdateProfile = findViewById(R.id.toolbarOfUpdateProfileActivity);
        backButtonOfUpdateProfile = findViewById(R.id.backButtonOfUpdateProfileActivity);
        progressBarOfUpdateProfile = findViewById(R.id.progressBarOfUpdateProfileActivity);
        ivUpdateNewImageView = findViewById(R.id.ivGetNewUserProfileImage);
        etUpdateUserName = findViewById(R.id.etGetNewUserName);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        intent = getIntent();

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK){
            imagePath = data.getData();
            ivUpdateNewImageView.setImageURI(imagePath);
        }
    }
}