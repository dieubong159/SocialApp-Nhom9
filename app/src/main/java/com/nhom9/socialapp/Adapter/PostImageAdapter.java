package com.nhom9.socialapp.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nhom9.socialapp.R;

import java.util.ArrayList;

public class PostImageAdapter extends BaseAdapter {
    private Context mContext;
    private int pos;
    private LayoutInflater inflater;
    private ImageView ivGallery;
    ArrayList<Uri> mArrayUri;

    public PostImageAdapter(Context mContext, ArrayList<Uri> mArrayUri){
        this.mContext = mContext;
        this.mArrayUri = mArrayUri;
    }

    @Override
    public int getCount() {
        return mArrayUri.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayUri.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        pos = position;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.post_gv_item, parent,false);
        ivGallery = itemView.findViewById(R.id.iv_gallery);
//        ivGallery.setImageURI(mArrayUri.get(position));
        Glide.with(mContext).load(mArrayUri.get(position)).into(ivGallery);
        return itemView;
    }
}
