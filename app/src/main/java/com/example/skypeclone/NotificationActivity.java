package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class NotificationActivity extends AppCompatActivity {


    private RecyclerView notificationList;

    private DatabaseReference userRef, friendRequestRef, contactRef;
    private FirebaseAuth mAuth;
    private String currentUserid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend_Request");

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();


        notificationList = findViewById(R.id.notification_List);
        notificationList.setHasFixedSize(true);

        notificationList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(friendRequestRef.child(currentUserid), Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts, NotificationsViewholder> adapter = new FirebaseRecyclerAdapter<Contacts, NotificationsViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotificationsViewholder holder, int position, @NonNull Contacts model) {

                holder.acceptButton.setVisibility(View.VISIBLE);
                holder.canelButton.setVisibility(View.VISIBLE);


               final String listUserid = getRef(position).getKey();

               DatabaseReference requestTypeRef = getRef(position).child("request_type").getRef();
         requestTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String type = snapshot.getValue().toString();
                            if (type.equals("received")) {

                                userRef.child(listUserid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("image")) {
                                            final String image = snapshot.child("image").getValue().toString();
                                            Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.profileImageview);
                                       }
                                        final String name = snapshot.child("name").getValue().toString();
                                            holder.userNameTxt.setText(name);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                holder.cardView.setVisibility(View.GONE);
                            }



                            holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        acceptFriendRequest(listUserid);
                                }
                            });
                            holder.canelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        cancelFriendRequest(listUserid);
                                }
                            });



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @NonNull
            @Override
            public NotificationsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friends_item_layout, parent, false);

                return new NotificationsViewholder(view);


            }
        };
        notificationList.setAdapter(adapter);
        adapter.startListening();


    }

    public static class NotificationsViewholder extends RecyclerView.ViewHolder {


        TextView userNameTxt;
        Button acceptButton, canelButton;
        ImageView profileImageview;
        RelativeLayout cardView;

        public NotificationsViewholder(@NonNull View itemView) {
            super(itemView);

            userNameTxt = itemView.findViewById(R.id.name_notificaitons);
            acceptButton = itemView.findViewById(R.id.request_Accept_Button);
            canelButton = itemView.findViewById(R.id.request_Decline_Button);
            profileImageview = itemView.findViewById(R.id.image_notificaiton);
            cardView = itemView.findViewById(R.id.card_View);


        }
    }








    private void acceptFriendRequest(final String listUserid) {

        contactRef.child(currentUserid).child(listUserid).child("Contact").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactRef.child(currentUserid).child(currentUserid).child("Contact").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                friendRequestRef.child(currentUserid).child(listUserid).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    friendRequestRef.child(listUserid).child(currentUserid).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                             Toast.makeText(NotificationActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
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



    private void cancelFriendRequest(final String listUserid) {

        friendRequestRef.child(currentUserid).child(listUserid).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestRef.child(listUserid).child(currentUserid).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(NotificationActivity.this, "Friend Request Canceled", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });




    }



}