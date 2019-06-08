package com.nhom9.socialapp.Model;

import java.util.ArrayList;
import java.util.Date;

public class Post {
    private String content;
    private ArrayList<String> image;
    private User owner;
    private String uploadtime;

    public Post(String content, ArrayList<String> image, User owner, String uploadtime) {
        this.content = content;
        this.image = image;
        this.owner = owner;
        this.uploadtime = uploadtime;
    }

    public Post() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(String uploadtime) {
        this.uploadtime = uploadtime;
    }
}
