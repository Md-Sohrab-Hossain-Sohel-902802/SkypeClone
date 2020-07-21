package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ConcurrentModificationException;

public class FindPeopleActivity extends AppCompatActivity {

    private RecyclerView findFriendsLIst;
    private EditText searchET;
    private  String str="";


    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);


        userRef= FirebaseDatabase.getInstance().getReference().child("Users");

        searchET=findViewById(R.id.search_user_text);
        findFriendsLIst=findViewById(R.id.find_people_List);

        findFriendsLIst.setHasFixedSize(true);
        findFriendsLIst.setLayoutManager(new LinearLayoutManager(this));



        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(searchET.getText().toString().equals("")){
                        Toast.makeText(FindPeopleActivity.this, "Write Name For search", Toast.LENGTH_SHORT).show();
                    }else{
                           str= s.toString();
                           onStart();
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });












    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=null;
        if(str.equals("")){
            options=new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(userRef,Contacts.class)
                    .build();
        }else{
            options=new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(userRef.orderByChild("name")
                            .startAt(str)
                            .endAt(str+"\uf8ff"),Contacts.class)
                            .build();
        }


        FirebaseRecyclerAdapter<Contacts,FindFriendsViewholder> adapter=new FirebaseRecyclerAdapter<Contacts, FindFriendsViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewholder holder, final int position, @NonNull final Contacts dataList) {

                    if(dataList.getImage()==null){
                        Picasso.get().load(R.drawable.profile_image).into(holder.profileImageview);
                    }else{
                        Picasso.get().load(dataList.getImage()).into(holder.profileImageview);

                    }

                    holder.userNameTxt.setText(dataList.getName());


                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(FindPeopleActivity.this,ProfileActivity.class);
                            intent.putExtra("uid",dataList.getUid());
                            intent.putExtra("profile_name",dataList.getName());
                            intent.putExtra("profile_image",dataList.getImage());
                            startActivity(intent);
                        }
                    });



            }

            @NonNull
            @Override
            public FindFriendsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_design,parent,false);
                FindFriendsViewholder holder=new FindFriendsViewholder(view);
                return holder;




            }
        };

        findFriendsLIst.setAdapter(adapter);
        adapter.startListening();








    }

    public static class FindFriendsViewholder extends  RecyclerView.ViewHolder{


        TextView userNameTxt;
        Button videoCallButton;
        ImageView profileImageview;
        RelativeLayout cardview2;




        public FindFriendsViewholder(@NonNull View itemView) {
            super(itemView);
            userNameTxt=itemView.findViewById(R.id.name_contact);
            profileImageview=itemView.findViewById(R.id.image_contact);
            videoCallButton=itemView.findViewById(R.id.call_Button);
            cardview2=itemView.findViewById(R.id.card_View2);


            videoCallButton.setVisibility(View.GONE);


        }
    }


}