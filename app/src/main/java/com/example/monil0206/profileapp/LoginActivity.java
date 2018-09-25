package com.example.monil0206.profileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private Button mLogin;
    private Button mRegister;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
//    private SignInButton gSignIn;
//    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mRef;
    private String currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(!isConnected(LoginActivity.this)){
            buildDialog(LoginActivity.this).show();
            //finish();
        }

        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.mEmail);
        mPass = findViewById(R.id.mPass);
        mLogin = findViewById(R.id.mLogin);
        mRegister = findViewById(R.id.mRegister);
        //gSignIn = findViewById(R.id.gSignIn);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Logging you in...");
        mRef = FirebaseDatabase.getInstance().getReference();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String pass = mPass.getText().toString();
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)) {
                    mProgress.show();
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mProgress.dismiss();
                                sendToMain();

                            } else {
                                mProgress.dismiss();
                                Toast.makeText(LoginActivity.this, "Invalid User Credentials....", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Fields Empty...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register);
            }
        });

        //------GOOGLE SIGN-IN---------
//        gSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Configure Google Sign In
//                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                        .requestIdToken(getString(R.string.default_web_client_id))
//                        .requestEmail()
//                        .build();
//                mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this,gso);
//                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//                startActivityForResult(signInIntent, RC_SIGN_IN);
//            }
//        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
//                // ...
//            }
//        }
//
//    }
//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            if(mAuth != null){
//                                currentUser = mAuth.getCurrentUser().getUid();
//                            }
//                            DatabaseReference mUser = mRef.child("Users").child(currentUser);
//                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);
//                            if(acct != null){
//                                String name = acct.getDisplayName();
//                                String email = acct.getEmail();
//                                Uri imgUri = acct.getPhotoUrl();
//                                mUser.child("Name").setValue(name);
//                                //mUser.child("Image").setValue(imgUri.toString());
//                                mUser.child("Details").setValue(email);
//                                mUser.child("SignIn Method").setValue("Google Sign In");
//                            }
//                            sendToMain();
//
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                        }
//
//                        // ...
//                    }
//                });
//    }
    public void sendToMain(){
        Intent main = new Intent(LoginActivity.this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(main);
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();
        isConnected(this);
    }

    public boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if(netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        }
        return false;
    }
    public AlertDialog.Builder buildDialog(Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please make sure your mobile data or wifi is on to proceed....");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent mainIntent = new Intent(LoginActivity.this,LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        return builder;
    }
}
