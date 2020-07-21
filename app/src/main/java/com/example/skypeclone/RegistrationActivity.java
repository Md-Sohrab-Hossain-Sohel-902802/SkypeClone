package com.example.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity {


    private CountryCodePicker countryCodePicker;
    private EditText phoneText;
    private EditText codeText;
    private Button continueAndNextButton;
    private String checker = "", phoneNumber = "";


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();


        mAuth.signInWithEmailAndPassword("khorshed@gmail.com", "244739").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, "Account Created", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegistrationActivity.this, ContactsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });






     /*   loadingBar=new ProgressDialog(RegistrationActivity.this);

        phoneText=findViewById(R.id.phoneText);
        codeText=findViewById(R.id.codeText);
        continueAndNextButton=findViewById(R.id.continueNextButton);
        countryCodePicker=findViewById(R.id.ccp);


            countryCodePicker.registerCarrierNumberEditText(phoneText);



        continueAndNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(continueAndNextButton.getText().equals("Submit") || checker.equals("code sent")){

                            String verificationcode=codeText.getText().toString();
                            if(codeText.equals("")){
                                codeText.setError("Enter Code First");
                                codeText.requestFocus();
                                return;
                            }else{
                                loadingBar.setTitle("Phone Number Verification");
                                loadingBar.setMessage("Please wait ..");
                                loadingBar.setCanceledOnTouchOutside(false);
                                loadingBar.show();


                                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,verificationcode);
                                signInWithPhoneAuthCredential(credential);

                            }


                }else{
                    phoneNumber=countryCodePicker.getFullNumberWithPlus() ;
                    if(phoneNumber!=null){

                        loadingBar.setTitle("Phone Number Verification");
                        loadingBar.setMessage("Please wait ..");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();




                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                RegistrationActivity.this,               // Activity (for callback binding)
                                mCallbacks);        // OnVerificationStateChangedCallbacks






                    }else{
                        Toast.makeText(RegistrationActivity.this, "Please Enter Your Phone number First", Toast.LENGTH_SHORT).show();
                    }



                }





            }
        });

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {


                continueAndNextButton.setText("Continue");


                codeText.setVisibility(View.GONE);
                Toast.makeText(RegistrationActivity.this, "Invalid Phone Number....", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerificationId=s;
                mResendToken=forceResendingToken;


              checker="code sent";
              continueAndNextButton.setText("Submit");


              codeText.setVisibility(View.VISIBLE);
              loadingBar.dismiss();

                Toast.makeText(RegistrationActivity.this, "Code Sent.Please Check", Toast.LENGTH_SHORT).show();







            }
        };





*/


    }

    @Override
    protected void onStart() {
        super.onStart();


        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(RegistrationActivity.this, ContactsActivity.class);
            startActivity(intent);
        }


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(RegistrationActivity.this, "You Are Logged In Successfully", Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void sendUserToMainActivity() {
        Intent intent = new Intent(RegistrationActivity.this, ContactsActivity.class);
        startActivity(intent);
        finish();
    }












}