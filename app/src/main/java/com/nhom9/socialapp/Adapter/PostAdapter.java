package com.nhom9.socialapp.Adapter;

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
import com.nhom9.socialapp.CustomGridview;
import com.nhom9.socialapp.Model.Post;
import com.nhom9.socialapp.R;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private Context mContext;
    private List<Post> mPosts;

    FirebaseUser fuser;
    ArrayList<Uri> lstImageUri;

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
            viewHolder.profile_image.setImageResource(R.mipmap.ic_user_round);
        }else {
            Glide.with(mContext).load(post.getOwner().getImageURL()).into(viewHolder.profile_image);
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
