package com.nhom9.socialapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom9.socialapp.Adapter.PostAdapter;
import com.nhom9.socialapp.Model.Post;
import com.nhom9.socialapp.Model.User;
import com.nhom9.socialapp.PostStatusActivity;
import com.nhom9.socialapp.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostFragment extends Fragment {

    private RecyclerView posts_view;

    private CircleImageView profile_image;
    private MaterialEditText status_input;
    private ImageButton btn_add_photo;
    private TextView txt_add_photo;

    private List<Post> mPosts;

    FirebaseUser fuser;
    DatabaseReference reference;

    PostAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        posts_view = view.findViewById(R.id.posts_view);
        posts_view.setHasFixedSize(true);
        posts_view.setLayoutManager(new LinearLayoutManager(getContext()));

        profile_image = view.findViewById(R.id.profile_image);
        status_input = view.findViewById(R.id.txt_status);
        btn_add_photo = view.findViewById(R.id.btn_add_photo);
        txt_add_photo = view.findViewById(R.id.txt_add_photo);

        status_input.setInputType(InputType.TYPE_NULL);
        status_input.setTextIsSelectable(false);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        mPosts = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isAdded()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getImageURL().equals("default")) {
                        profile_image.setImageResource(R.mipmap.ic_user_round);
                    } else {
                        Glide.with(getContext()).load(user.getImageURL()).into(profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        newsfeed();

        btn_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostStatusActivity.class);
                intent.putExtra("hasImage","true");
                startActivity(intent);
            }
        });

        txt_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostStatusActivity.class);
                intent.putExtra("hasImage","true");
                startActivity(intent);
            }
        });

        status_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostStatusActivity.class);
                intent.putExtra("hasImage","false");
                startActivity(intent);
            }
        });
        return view;
    }

    private void newsfeed(){
        reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPosts.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    mPosts.add(post);
                }
                postAdapter = new PostAdapter(getContext(), mPosts);
                posts_view.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
