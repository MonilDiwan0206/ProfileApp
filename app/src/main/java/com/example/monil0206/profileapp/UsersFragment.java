package com.example.monil0206.profileapp;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    private RecyclerView mUsersList;
    private DatabaseReference mRef;



    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        mUsersList = view.findViewById(R.id.mUsersList);

        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersList.addItemDecoration(new DividerItemDecoration(mUsersList.getContext(), DividerItemDecoration.HORIZONTAL));



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users,UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(
                Users.class,
                R.layout.users_single_layout,
                UserViewHolder.class,
                mRef
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, Users model, int position) {
                viewHolder.setDisplayName(model.getName());
                viewHolder.setUserDetails(model.getDetails());
                viewHolder.setUserImage(model.getImage(), getContext());


            }
        };

       mUsersList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDisplayName(String name){
            TextView dispName = (TextView) mView.findViewById(R.id.dispName);
            dispName.setText(name);
        }
        public void setUserDetails(String details){
            TextView status = (TextView) mView.findViewById(R.id.status);
            status.setText(details);
        }
        public void setUserImage(String image, Context ctx){
            CircleImageView pImage = (CircleImageView) mView.findViewById(R.id.pImage);
            Picasso.with(ctx).load(image).placeholder(R.mipmap.defpic).into(pImage);
        }
    }

}
