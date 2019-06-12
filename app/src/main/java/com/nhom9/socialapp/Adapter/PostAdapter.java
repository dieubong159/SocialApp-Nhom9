package com.nhom9.socialapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
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
import com.google.firebase.database.ValueEventListener;
import com.nhom9.socialapp.CustomGridview;
import com.nhom9.socialapp.Model.Post;
import com.nhom9.socialapp.Model.User;
import com.nhom9.socialapp.R;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private Context mContext;
    private List<Post> mPosts;


    FirebaseUser fuser;
    ArrayList<Uri> lstImageUri;
    DatabaseReference reference;

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
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        final Post post = mPosts.get(i);
        reference = FirebaseDatabase.getInstance().getReference("Users").child(post.getOwner().getId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                viewHolder.post_content.setText(post.getContent());
                viewHolder.username.setText(post.getOwner().getUsername());
                viewHolder.upload_time.setText(post.getUploadtime());
                User user = dataSnapshot.getValue(User.class);
                if(user.getImageURL().equals("default")){
                    viewHolder.profile_image.setImageResource(R.mipmap.ic_user_round);
                }else {
                    if(!((Activity)mContext).isFinishing()) {
                        Glide.with(mContext).load(user.getImageURL()).into(viewHolder.profile_image);
                    }
                }

                if(post.getImage().get(0).equals("none")){
                    viewHolder.post_img.setVisibility(View.GONE);
                }else {
                    lstImageUri = new ArrayList<>();
                    for(int count = 0;count<post.getImage().size();count++)
                    {
                        lstImageUri.add(Uri.parse(post.getImage().get(count)));
                    }
                    PostImageAdapter galleryAdapter = new PostImageAdapter(mContext, lstImageUri);
                    viewHolder.post_img.setAdapter(galleryAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        public CustomGridview post_img;

        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            upload_time = itemView.findViewById(R.id.upload_time);
            post_content = itemView.findViewById(R.id.post_content);
            post_img = itemView.findViewById(R.id.gv_post);
        }
    }
}
