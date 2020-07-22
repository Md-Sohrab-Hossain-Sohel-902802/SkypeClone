package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;


import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoChatActivity extends AppCompatActivity  implements  Session.SessionListener,
        Publisher.PublisherListener{

    private static String API_KEY = "46852844";
    private static String SESSION_ID = "1_MX40Njg1Mjg0NH5-MTU5NTM4NTU4Njc3OX52WFhMd2NPZ2lSQ2ZaMHk1aVBBa0E2cVF-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00Njg1Mjg0NCZzaWc9ZTVkOTIxZTJkYjM4ODkzZWMwNjEwYzE1Y2NhYjk2MTA4ZmRhZjExZDpzZXNzaW9uX2lkPTFfTVg0ME5qZzFNamcwTkg1LU1UVTVOVE00TlRVNE5qYzNPWDUyV0ZoTWQyTlBaMmxTUTJaYU1IazFhVkJCYTBFMmNWRi1mZyZjcmVhdGVfdGltZT0xNTk1Mzg1NjYxJm5vbmNlPTAuNjc3MzU0OTcwNzQxNjI1MSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTk3OTc3NjU3JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final String LOG_Tag = VideoChatActivity.class.getSimpleName();
    private static final int RC_VIDEO_APP_PERM = 1234;


    private ImageView closeVideoChatButton;

    private DatabaseReference userREf;
    private String userId;



    private  FrameLayout mPublisherViewControler;
    private  FrameLayout mSubscriberViewControler;


    //1. initialize and  connect to the session

    private Session mSession ;
    private Publisher mPublisher;

    private Subscriber mSubscriber;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);


        userREf = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        closeVideoChatButton = findViewById(R.id.close_video_chat_button);


        closeVideoChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userREf.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(userId).hasChild("Ringing")) {
                            userREf.child(userId).child("Ringing").removeValue();

                            if(mPublisher !=null){
                                mPublisher.destroy();
                            }  if(mSubscriber !=null){
                                mSubscriber.destroy();
                            }



                            startActivity(new Intent(VideoChatActivity.this, RegistrationActivity.class));
                            finish();
                        } else if (snapshot.child(userId).hasChild("Calling")) {
                            userREf.child(userId).child("Calling").removeValue();



                            if(mPublisher !=null){
                                mPublisher.destroy();
                            }  if(mSubscriber !=null){
                                mSubscriber.destroy();
                            }


                            startActivity(new Intent(VideoChatActivity.this, RegistrationActivity.class));
                            finish();
                        } else {


                            if(mPublisher !=null){
                                mPublisher.destroy();
                            }  if(mSubscriber !=null){
                                mSubscriber.destroy();
                            }


                            startActivity(new Intent(VideoChatActivity.this, RegistrationActivity.class));
                            finish();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    requestPermissions();



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,VideoChatActivity.this);

    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private  void requestPermissions(){
            String[] perms={Manifest.permission.INTERNET,Manifest.permission.RECORD_AUDIO};

            if(EasyPermissions.hasPermissions(this,perms)){
                    mPublisherViewControler=findViewById(R.id.publisher_container);
                    mSubscriberViewControler=findViewById(R.id.subscriber_container);

                    mSession=new Session.Builder(this,API_KEY,SESSION_ID).build();
                    mSession.setSessionListener(VideoChatActivity.this);

                    mSession.connect(TOKEN);





            }else{
                EasyPermissions.requestPermissions(this,"Hey this app needs mike and camera permission",RC_VIDEO_APP_PERM);
            }

    }


    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }
//publishing strem to the session
    @Override
    public void onConnected(Session session) {
        Log.i(LOG_Tag,"Session Connected");
        mPublisher=new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(VideoChatActivity.this);

        mPublisherViewControler.addView(mPublisher.getView());


        if(mPublisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);




    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_Tag,"Stream has Disconnected");
    }


    //subscribing to the streams


    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_Tag,"Stream Recived");
        if(mSubscriber==null){
            mSubscriber=new Subscriber.Builder(this,stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewControler.addView(mSubscriber.getView());
        }

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_Tag,"Stream has been droped");

        if(mSubscriber !=null){
            mSubscriber=null;
            mSubscriberViewControler.removeAllViews();

        }

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.i(LOG_Tag,"Stream has been Error");
    }
}