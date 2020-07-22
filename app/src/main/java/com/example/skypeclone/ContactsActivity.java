package com.example.skypeclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsActivity extends AppCompatActivity {

    BottomNavigationView navView;


    RecyclerView myContactLIst;
    ImageView findPeopleBtn;

    private DatabaseReference userRef;
    private DatabaseReference contactsRef;
    private FirebaseAuth mAuth;
    private String currentUserid;


    private  String calldedBy="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        navView = findViewById(R.id.nav_view);

        findPeopleBtn = findViewById(R.id.find_people_button);
        myContactLIst = findViewById(R.id.contact_list);


        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();


        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


        myContactLIst.setHasFixedSize(true);
        myContactLIst.setLayoutManager(new LinearLayoutManager(this));


        findPeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsActivity.this, FindPeopleActivity.class);
                startActivity(intent);
            }
        });


    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if (item.getItemId() == R.id.navigation_notifications) {
                Intent notiIntent = new Intent(ContactsActivity.this, NotificationActivity.class);
                startActivity(notiIntent);

            } else if (item.getItemId() == R.id.navigation_home) {
                Intent intent = new Intent(ContactsActivity.this, ContactsActivity.class);
                startActivity(intent);

            } else if (item.getItemId() == R.id.navigation_settings) {
                Intent settingIntent = new Intent(ContactsActivity.this, SettingsActivity.class);
                startActivity(settingIntent);

            } else if (item.getItemId() == R.id.navigation_logout) {
                FirebaseAuth.getInstance().signOut();
                FirebaseAuth.getInstance().signInWithEmailAndPassword("sohrab@gmail.com", "244739").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(ContactsActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                    }
                });
               /*Intent intent1=new Intent(ContactsActivity.this,RegistrationActivity.class);
               startActivity(intent1);*/

            }


            return false;

        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        checkForReceavingCall();

        validateUser();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()), Contacts.class)
                .build();



        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder > adapter=new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {

                holder.videoCallButton.setVisibility(View.VISIBLE);
                    final String list_userId=getRef(position).getKey();

                    userRef.child(list_userId).addValueEventListener(new ValueEventListener() {
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
                holder.videoCallButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ContactsActivity.this,CallingActivity.class);
                        intent.putExtra("uid",list_userId);
                        startActivity(intent);
                    }
                });



            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_design, parent, false);

                return new  ContactsViewHolder(view);
            }
        };


        myContactLIst.setAdapter(adapter);
        adapter.startListening();



    }




    public static class ContactsViewHolder extends RecyclerView.ViewHolder {


        TextView userNameTxt;
        Button videoCallButton;
        ImageView profileImageview;
        RelativeLayout cardview2;


        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTxt = itemView.findViewById(R.id.name_contact);
            profileImageview = itemView.findViewById(R.id.image_contact);
            videoCallButton = itemView.findViewById(R.id.call_Button);
            cardview2 = itemView.findViewById(R.id.card_View2);


            videoCallButton.setVisibility(View.GONE);


        }
    }
    private void validateUser() {

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users");

        reference.child(currentUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(!snapshot.exists()){
                            Intent intent=new Intent(ContactsActivity.this,SettingsActivity.class);
                            startActivity(intent);
                            finish();
                        }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }


    private void checkForReceavingCall() {
        userRef.child(currentUserid)
                .child("Ringing")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("ringing")){
                            calldedBy=snapshot.child("ringing").getValue().toString();
                            Intent intent=new Intent(ContactsActivity.this,CallingActivity.class);
                            intent.putExtra("uid",calldedBy);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }




}