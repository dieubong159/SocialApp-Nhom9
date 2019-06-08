package com.nhom9.socialapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nhom9.socialapp.Model.Post;
import com.nhom9.socialapp.Model.User;
import com.nhom9.socialapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private Context mContext;
    private List<Post> mPosts;

    FirebaseUser fuser;
    DatabaseReference reference;
    User user;

    public PostAdapter(Context mContext, List<Post> mPosts){
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, viewGroup,false);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final Post post = mPosts.get(i);
        viewHolder.post_content.setText(post.getContent());
        viewHolder.username.setText(post.getOwner().getUsername());
        viewHolder.upload_time.setText(post.getUploadtime());
        if(post.getOwner().getImageURL().equals("default")){
            viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher_round);
        }else {
            Glide.with(mContext).load(post.getOwner().getImageURL()).into(viewHolder.profile_image);
        }

        for(String img_url : post.getImage()){
            if(img_url.equals("none")){
                viewHolder.post_img.setVisibility(View.GONE);
            }else {
                Glide.with(mContext).load(img_url).into(viewHolder.post_img);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        public TextView upload_time;
        public TextView post_content;
        public ImageView post_img;

        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            upload_time = itemView.findViewById(R.id.upload_time);
            post_content = itemView.findViewById(R.id.post_content);
            post_img = itemView.findViewById(R.id.post_img);
        }
    }

//    private User RetrieveUser(final String userid){
//        reference = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference ref = reference.child("Users").child(userid);
//
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                user = dataSnapshot.getValue(User.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        return user;
//    }
}
