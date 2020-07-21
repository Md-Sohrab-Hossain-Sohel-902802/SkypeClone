package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private Button saveButton;
    private EditText usernameEdittext, userBioEdittext;
    private ImageView userProfileImage;

    private Uri imageUri;
    private String downloadImageUri;
    private ProgressDialog progressDialog;


    private StorageReference userProfileImageref;
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        userProfileImageref = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        progressDialog = new ProgressDialog(this);

        saveButton = findViewById(R.id.save_Settings_Btn);
        usernameEdittext = findViewById(R.id.username_settings);
        userBioEdittext = findViewById(R.id.bio_settings);
        userProfileImage = findViewById(R.id.settings_profile_image);

        RetriveuserInfo();


        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserdata();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data.getData() != null) {
            imageUri = data.getData();
            userProfileImage.setImageURI(imageUri);
        }
    }


    private void saveUserdata() {
        final String getUsername = usernameEdittext.getText().toString();
        final String getUserStatus = userBioEdittext.getText().toString();
        if (getUsername.equals("")) {
            usernameEdittext.setError("User Name Is Required");
            usernameEdittext.requestFocus();
            return;
        } else if (getUserStatus.equals("")) {
            userBioEdittext.setError("User Bio Is Required");
            userBioEdittext.requestFocus();
            return;
        } else if (imageUri == null) {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("image")) {
                        saveInfoOnly();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Please Select Fast", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            progressDialog.setTitle("Account Settings");
            progressDialog.setMessage("Please Wait.....");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            final StorageReference filePath = userProfileImageref.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            final UploadTask uploadTask = filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    downloadImageUri = filePath.getDownloadUrl().toString();


                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        downloadImageUri = task.getResult().toString();
                        final HashMap<String, Object> profileMap = new HashMap<>();
                        profileMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        profileMap.put("name", getUsername);
                        profileMap.put("status", getUserStatus);
                        profileMap.put("image", downloadImageUri);

                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(SettingsActivity.this, ContactsActivity.class);
                                    startActivity(intent);
                                    finish();
                                    progressDialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "Your Profile Status has been updated.", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressDialog.dismiss();
                                }
                            }
                        });


                    }
                }
            });
        }


    }

    private void saveInfoOnly() {
        final String getUsername = usernameEdittext.getText().toString();
        final String getUserStatus = userBioEdittext.getText().toString();

        if (getUsername.equals("")) {
            usernameEdittext.setError("User Name Is Required");
            usernameEdittext.requestFocus();
            return;
        } else if (getUserStatus.equals("")) {
            userBioEdittext.setError("User Bio Is Required");
            userBioEdittext.requestFocus();
            return;
        } else {
            progressDialog.setTitle("Account Settings");
            progressDialog.setMessage("Please Wait.....");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            final HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            profileMap.put("name", getUsername);
            profileMap.put("status", getUserStatus);

            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                    updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(SettingsActivity.this, ContactsActivity.class);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Your Profile Status has been updated.", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                    }
                }
            });


        }
    }

    private void RetriveuserInfo() {
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String image = snapshot.child("image").getValue().toString();
                    String username = snapshot.child("name").getValue().toString();
                    String userStatus = snapshot.child("status").getValue().toString();


                    usernameEdittext.setText(username);
                    userBioEdittext.setText(userStatus);

                    Picasso.get().load(image).placeholder(R.drawable.profile_image).into(userProfileImage);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}