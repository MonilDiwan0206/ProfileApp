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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private StorageReference mStorage;
    private EditText dName;
    private EditText dEmail;
    private EditText dPass;
    private Button mLogin;
    private Button mRegister;
    private CircleImageView pImage;
    private ProgressDialog mProgress;
    private String currentUser;
    private static final int GALLERY_PICK = 1;
    private Uri imageUri;
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if(!isConnected(RegisterActivity.this)) {
            buildDialog(RegisterActivity.this).show();
            //finish();
        }
        imageUri = null;
        mAuth = FirebaseAuth.getInstance();


        mRef = FirebaseDatabase.getInstance().getReference();

        mStorage = FirebaseStorage.getInstance().getReference();

        dName = findViewById(R.id.dName);
        dEmail = findViewById(R.id.dEmail);
        dPass = findViewById(R.id.dPass);
        mLogin = findViewById(R.id.mLogin);
        mRegister = findViewById(R.id.mRegister);
        pImage = findViewById(R.id.pImage);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Creating your account...");
        mProgress.setCanceledOnTouchOutside(true);


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        pImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = dName.getText().toString();
                final String email = dEmail.getText().toString();
                String pass = dPass.getText().toString();
                if(!TextUtils.isEmpty(name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass) || imageUri != null){
                    mProgress.show();
                    mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                if (mAuth != null) {
                                    currentUser = mAuth.getCurrentUser().getUid();
                                }

                                userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
                                userRef.child("Name").setValue(name);
                                userRef.child("Details").setValue(email);
//                                userRef.child("SignIn Method").setValue("Email");
                                final StorageReference filePath = mStorage.child("Profile_Pic");
                                filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> uploadtask) {
                                        if(uploadtask.isSuccessful()){
                                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String downloadUrl = uri.toString();
                                                    userRef.child("Image").setValue(downloadUrl);
                                                    mProgress.dismiss();
                                                    Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }
                                            });
                                        } else {
                                            mProgress.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Failed to Upload photo....", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            } else {
                                mProgress.dismiss();
                                Toast.makeText(RegisterActivity.this, "Invalid Email ID or your ID is already used...", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill in your complete details...", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            imageUri = data.getData();
            pImage.setImageURI(imageUri);
        }
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
                Intent mainIntent = new Intent(RegisterActivity.this,RegisterActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        return builder;
    }
}
