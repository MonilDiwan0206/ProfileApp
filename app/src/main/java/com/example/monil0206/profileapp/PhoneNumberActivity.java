package com.example.monil0206.profileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class PhoneNumberActivity extends AppCompatActivity {

    private EditText pNumber;
    private Button pButton;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private EditText vCode;
    private Button vButton;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private DatabaseReference mRef;
    private EditText mName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        pNumber = findViewById(R.id.pNumber);
        pButton = findViewById(R.id.pButton);
        vCode = findViewById(R.id.vCode);
        vButton = findViewById(R.id.vButton);
        mName = findViewById(R.id.mName);
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Sending Message");
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");

        pButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.show();
                String pNo = pNumber.getText().toString();
                String phoneNumber = "+91"+ pNo;

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        120,
                        TimeUnit.SECONDS,
                        PhoneNumberActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                mProgress.dismiss();
                                vCode.setVisibility(View.VISIBLE);
                                vButton.setVisibility(View.VISIBLE);
                                pNumber.setVisibility(View.VISIBLE);
                                pButton.setVisibility(View.INVISIBLE);
                                mName.setVisibility(View.INVISIBLE);
                                mName.setEnabled(false);
                                pNumber.setEnabled(false);
                                pButton.setEnabled(false);
                                vButton.setEnabled(true);
                                signInWithPhoneAuthCredentials(phoneAuthCredential);


                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                mProgress.dismiss();
                                Toast.makeText(PhoneNumberActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                // This callback is invoked in an invalid request for verification is made,
                                // for instance if the the phone number format is not valid.
                                Log.w("Phone", "onVerificationFailed", e);

                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(PhoneNumberActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    // The SMS quota for the project has been exceeded
                                    // ...
                                }

                                // Show a message and update the UI
                                // ...

                            }

                            @Override
                            public void onCodeSent(String s, final PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                mProgress.dismiss();
                                vCode.setVisibility(View.VISIBLE);
                                vButton.setVisibility(View.VISIBLE);
                                pNumber.setVisibility(View.VISIBLE);
                                pButton.setVisibility(View.INVISIBLE);
                                pNumber.setEnabled(false);
                                pButton.setEnabled(false);
                                vButton.setEnabled(true);
                                mVerificationId = s;
                                mResendToken = forceResendingToken;
                                Log.d("CodeSent", "onCodeSent:" + s);
                                vButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        final String dcode = vCode.getEditableText().toString();
                                        try {
                                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, dcode);
                                            signInWithPhoneAuthCredentials(credential);

                                        } catch (Exception e) {
                                            Toast.makeText(PhoneNumberActivity.this, "Invalid Code...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                );
            }
        });
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential phoneAuthCredential) {
        mProgress.setMessage("Verifying Code.....");
        mProgress.show();
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mProgress.dismiss();
                    String phoneNumber = pNumber.getText().toString();
                    String name = mName.getText().toString();
                    String user_id  = task.getResult().getUser().getUid();
                    mRef.child(user_id).child("Name").setValue(name);
                    mRef.child(user_id).child("Image").setValue(R.mipmap.defpic);
                    mRef.child(user_id).child("SignIn Method").setValue("Phone Number");
                    mRef.child(user_id).child("Details").setValue(phoneNumber);
                    Intent mainIntent = new Intent(PhoneNumberActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else {
                    mProgress.dismiss();
                    // Sign in failed, display a message and update the UI
                    Log.w("Phone", "signInWithCredential:failure", task.getException());
                    Toast.makeText(PhoneNumberActivity.this, "Invalid Verification Code..", Toast.LENGTH_SHORT).show();
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }

                }

            }
        });
    }
}
