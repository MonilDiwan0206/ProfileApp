package com.example.monil0206.profileapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private Button logoutBtn;
    private FirebaseAuth mAuth;
    private CircleImageView profileImage;
    private DatabaseReference mRef;
    private String currentUser;
    private TextView displayName;
    private ProgressDialog mProgress;
    private TextView details;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        logoutBtn = view.findViewById(R.id.logoutBtn);
        mProgress = new ProgressDialog(getActivity());
        mProgress.setTitle("Loading");
        mProgress.setMessage("Please wait...");
        mProgress.show();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent main = new Intent(getActivity(),LoginActivity.class);
                startActivity(main);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null){
            currentUser = mAuth.getCurrentUser().getUid();

        }
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        mRef.keepSynced(true);
        profileImage = view.findViewById(R.id.profileImage);
        displayName = view.findViewById(R.id.displayName);
        details = view.findViewById(R.id.details);


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("Name").getValue().toString();
                String image = dataSnapshot.child("Image").getValue().toString();
                String email = dataSnapshot.child("Details").getValue().toString();
                displayName.setText(name);
                details.setText(email);
                Picasso.with(getActivity()).load(image).placeholder(R.mipmap.defpic).into(profileImage);
                mProgress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

}
