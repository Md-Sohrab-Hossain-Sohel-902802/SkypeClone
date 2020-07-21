package com.example.skypeclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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











    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

         navView = findViewById(R.id.nav_view);

         findPeopleBtn=findViewById(R.id.find_people_button);
         myContactLIst=findViewById(R.id.contact_list);


        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);




        myContactLIst.setHasFixedSize(true);
        myContactLIst.setLayoutManager(new LinearLayoutManager(this));



        findPeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent=new Intent(ContactsActivity.this,FindPeopleActivity.class);
                    startActivity(intent);
            }
        });



   }



   private  BottomNavigationView.OnNavigationItemSelectedListener  navigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
       @Override
       public boolean onNavigationItemSelected(@NonNull MenuItem item) {

           if(item.getItemId()==R.id.navigation_notifications){
               Intent notiIntent=new Intent(ContactsActivity.this,NotificationActivity.class);
               startActivity(notiIntent);

           } else if(item.getItemId()==R.id.navigation_home){
               Intent intent=new Intent(ContactsActivity.this,ContactsActivity.class);
               startActivity(intent);

           }else if(item.getItemId()==R.id.navigation_settings){
               Intent settingIntent=new Intent(ContactsActivity.this,SettingsActivity.class);
               startActivity(settingIntent);

           }else if(item.getItemId()==R.id.navigation_logout){
               FirebaseAuth.getInstance().signOut();
               FirebaseAuth.getInstance().signInWithEmailAndPassword("sohrab@gmail.com","244739").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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

}