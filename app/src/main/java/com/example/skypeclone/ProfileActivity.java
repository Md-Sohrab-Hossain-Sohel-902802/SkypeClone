package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {


    String receiverUserID, receiver_profile_name, reciver_profile_image;

    private ImageView profileImage;
    private TextView nameTextview;
    private Button sendFriendRequestButton;
    private Button cancelFriendRequestButton;


    private FirebaseAuth mAuth;
    private String senderUserid;

    private  String currentState="new";





    //<-------------------Database ---------------------->


    private  DatabaseReference friendRequestRef,contactsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        receiverUserID = getIntent().getStringExtra("uid");
        receiver_profile_name = getIntent().getStringExtra("profile_name");
        reciver_profile_image = getIntent().getStringExtra("profile_image");

        mAuth = FirebaseAuth.getInstance();
        senderUserid = mAuth.getCurrentUser().getUid();

        friendRequestRef= FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        contactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts");





        profileImage = findViewById(R.id.background_profile_view);
        nameTextview = findViewById(R.id.profile_usernameTextview);
        sendFriendRequestButton = findViewById(R.id.profile_AddFriendButton);
        cancelFriendRequestButton = findViewById(R.id.profile_declineFriendRequest);


        Picasso.get().load(reciver_profile_image).placeholder(R.drawable.profile_image).into(profileImage);
        nameTextview.setText(receiver_profile_name);


         manageClickEvents();




    }

    private void manageClickEvents() {

        friendRequestRef.child(senderUserid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(receiverUserID)){
                    String requestType=snapshot.child(receiverUserID).child("request_type").getValue().toString();
                    if(requestType.equals("sent")){

                        sendFriendRequestButton.setText("Cancel Friend Request");
                        currentState="request_sent";

                    }else if(requestType.equals("received")){
                            sendFriendRequestButton.setText("Accept Friend Request");
                            currentState="request_received";
                            cancelFriendRequestButton.setVisibility(View.VISIBLE);


                            cancelFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cancelFriendRequest();
                                }
                            });
                    }
                }else{
                    contactsRef.child(senderUserid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(receiverUserID)){
                                sendFriendRequestButton.setText("Remove Friend");
                                cancelFriendRequestButton.setVisibility(View.GONE);
                                currentState="friends";
                            }else{
                                currentState="new";
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        if (senderUserid.equals(receiverUserID)) {
            sendFriendRequestButton.setVisibility(View.GONE);
        } else if (!senderUserid.equals(receiverUserID)) {
           sendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(currentState.equals("new")){
                       sentFriendRequest();
                   }else if(currentState.equals("request_sent")){
                       cancelFriendRequest();

                   }else if(currentState.equals("request_received")){
                       cancelFriendRequestButton.setVisibility(View.VISIBLE);
                       sendFriendRequestButton.setText("Accept Friend Request");
                            acceptFriendRequest();
                   }else if(currentState.equals("friends")){
                        deleteFriend();
                   }
               }
           });
        }
    }

    private void deleteFriend() {
        contactsRef.child(senderUserid).child(receiverUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactsRef.child(receiverUserID).child(senderUserid).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendFriendRequestButton.setText("Add Friend");
                                                currentState="new";
                                                Toast.makeText(ProfileActivity.this, "Friends Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void sentFriendRequest() {

            friendRequestRef.child(senderUserid).child(receiverUserID).child("request_type").setValue("sent")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                friendRequestRef.child(receiverUserID).child(senderUserid).child("request_type").setValue("received")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    sendFriendRequestButton.setText("Cancel Friend Request");
                                                    currentState="request_sent";
                                                    Toast.makeText(ProfileActivity.this, " Friend Request Sent", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    });





    }




    private void cancelFriendRequest() {

        friendRequestRef.child(senderUserid).child(receiverUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestRef.child(receiverUserID).child(senderUserid).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendFriendRequestButton.setText("Add Friend");
                                                currentState="new";
                                                Toast.makeText(ProfileActivity.this, "Canceled Friend Request", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });




    }



    private void acceptFriendRequest() {

        contactsRef.child(senderUserid).child(receiverUserID).child("Contact").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactsRef.child(receiverUserID).child(senderUserid).child("Contact").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                friendRequestRef.child(senderUserid).child(receiverUserID).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    friendRequestRef.child(receiverUserID).child(senderUserid).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        sendFriendRequestButton.setText("Remove Friend");
                                                                                        cancelFriendRequestButton.setVisibility(View.GONE);
                                                                                        currentState="friends";
                                                                                        Toast.makeText(ProfileActivity.this, "Friend Request Accepted", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });
                        }
                    }
                });








    }





}