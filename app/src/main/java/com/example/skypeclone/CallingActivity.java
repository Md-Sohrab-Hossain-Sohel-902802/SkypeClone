package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CallingActivity extends AppCompatActivity {

    private TextView nameContact;
    private ImageView profileImage;
    private ImageView cancelCallButton, makeCallButton;

    String reciverUserid, reciverUsername, reciverUserImage;
    String senderUserid, senderUsername, senderUserImage;


    private DatabaseReference userRef;
    private String checker = "";
    private  String callingID,ringIngID;

    private  MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        reciverUserid = getIntent().getStringExtra("uid");
        senderUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        nameContact = findViewById(R.id.name_calling);
        profileImage = findViewById(R.id.profile_Image_calling);
        cancelCallButton = findViewById(R.id.cancel_call);
        makeCallButton = findViewById(R.id.make_call);
    mediaPlayer=MediaPlayer.create(this,R.raw.a);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");


        cancelCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                checker = "clicked";
                cancelCallingUser();
            }
        });


        makeCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.stop();



                final HashMap<String, Object> callingPickerMap=new HashMap<>();

                callingPickerMap.put("picked","picked");

                userRef.child(senderUserid).child("Ringing").updateChildren(callingPickerMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                        Intent intent=new Intent(CallingActivity.this,VideoChatActivity.class);
                                        startActivity(intent);

                                }
                            }
                        });






            }
        });




        retriveData();


    }


    private void retriveData() {


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(reciverUserid).exists()) {
                    if (snapshot.child(reciverUserid).hasChild("image")) {
                        String image = snapshot.child(reciverUserid).child("image").getValue().toString();

                        Picasso.get().load(image).placeholder(R.drawable.profile_image).into(profileImage);
                    }

                    String name = snapshot.child(reciverUserid).child("name").getValue().toString();
                    nameContact.setText(name);


                }
                if (snapshot.child(senderUserid).exists()) {
                    if (snapshot.child(senderUserid).hasChild("image")) {
                        String image = snapshot.child(senderUserid).child("image").getValue().toString();
                    }

                    String name = snapshot.child(senderUserid).child("name").getValue().toString();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.start();
        userRef.child(reciverUserid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!checker.equals("clicked") && !snapshot.hasChild("Calling") && !snapshot.hasChild("Ringing")) {




                    final HashMap<String, Object> callingInfo = new HashMap<>();
                    callingInfo.put("uid", senderUserid);
                    callingInfo.put("name", senderUsername);
                    callingInfo.put("image", senderUserImage);
                    callingInfo.put("calling", reciverUserid);

                    userRef.child(senderUserid).child("Calling")
                            .updateChildren(callingInfo)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        final HashMap<String, Object> ringingInfo = new HashMap<>();
                                        ringingInfo.put("ringing", senderUserid);

                                        userRef.child(reciverUserid)
                                                .child("Ringing")
                                                .updateChildren(ringingInfo);


                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(senderUserid).hasChild("Ringing") && !snapshot.child(senderUserid).hasChild("Calling")) {
                    makeCallButton.setVisibility(View.VISIBLE);

                }


                if(snapshot.child(reciverUserid).child("Ringing").hasChild("picked")){
                    mediaPlayer.stop();
                    Intent intent=new Intent(CallingActivity.this,VideoChatActivity.class);
                    startActivity(intent);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void cancelCallingUser() {
//from sender side
        userRef.child(senderUserid).child("Calling")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists() && snapshot.hasChild("calling")){

                                callingID=snapshot.child("calling").getValue().toString();
                                userRef.child(callingID)
                                        .child("Ringing").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                userRef.child(senderUserid).child("Calling")
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Intent intent=new Intent(CallingActivity.this,RegistrationActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });
                                            }
                                    }
                                });

                        }else{
                            Intent intent=new Intent(CallingActivity.this,RegistrationActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            //from reciver side



        userRef.child(senderUserid).child("Ringing")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists() && snapshot.hasChild("ringing")){

                            ringIngID=snapshot.child("ringing").getValue().toString();
                            userRef.child(ringIngID)
                                    .child("Calling").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        userRef.child(senderUserid).child("Ringing")
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Intent intent=new Intent(CallingActivity.this,RegistrationActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                        }else{
                            Intent intent=new Intent(CallingActivity.this,RegistrationActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


}