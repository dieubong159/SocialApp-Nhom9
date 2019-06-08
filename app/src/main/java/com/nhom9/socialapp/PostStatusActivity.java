package com.nhom9.socialapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nhom9.socialapp.Fragments.PostFragment;
import com.nhom9.socialapp.Model.Post;
import com.nhom9.socialapp.Model.User;
import com.rengwuxian.materialedittext.MaterialMultiAutoCompleteTextView;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PostStatusActivity extends AppCompatActivity {

    MaterialMultiAutoCompleteTextView post_content;
    ImageButton btn_add_photo,btn_share;
    ImageView uploaded_photo;
    private static final int IMAGE_REQUEST = 1;
    Uri imageUri;
    Bitmap bmpUploadedPhoto;
    String timeStamp;
    StorageReference storageReference;
    private StorageTask uploadTask;
    List<String> imagelist;
    User owner;
    Boolean hasImage;

    FirebaseUser fuser;
    DatabaseReference reference, mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_status);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storageReference = FirebaseStorage.getInstance().getReference("post");

        btn_add_photo = findViewById(R.id.btn_add_photo);
        btn_share = findViewById(R.id.btn_share);
        uploaded_photo = findViewById(R.id.uploaded_photo);
        post_content = findViewById(R.id.post_content);
        imagelist = new ArrayList<>();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(fuser.getUid());
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                owner = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        btn_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PostStatusActivity.this, "Posting...", Toast.LENGTH_SHORT).show();
                final String postContent = post_content.getText().toString().trim();
                if (!TextUtils.isEmpty(postContent)) {
                    uploadImage();
                }else {
                    Toast.makeText(PostStatusActivity.this, "Status is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Intent intent = new Intent(getIntent());
        hasImage = Boolean.parseBoolean(intent.getStringExtra("hasImage"));
        if(hasImage){
            openImage();
        }
    }
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = PostStatusActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

     private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(PostStatusActivity.this);
        pd.setMessage("Uploading");
        pd.show();

        if(imageUri!=null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){

                        reference = FirebaseDatabase.getInstance().getReference();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("content", post_content.getText().toString().trim());
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        if(imagelist!=null) {
                            imagelist.clear();
                            imagelist.add(mUri);
                        }
                        else {
                            imagelist = new ArrayList<>();
                            imagelist.add(mUri);
                        }
                        hashMap.put("image", imagelist);
                        hashMap.put("owner", owner);
                        timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                        hashMap.put("uploadtime", timeStamp);

                        reference.child("Posts").push().setValue(hashMap);
                        pd.dismiss();

                        Intent intent = new Intent(PostStatusActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(PostStatusActivity.this, "Failed!!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostStatusActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(PostStatusActivity.this, "No image selected!!", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();
            try {
                bmpUploadedPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                uploaded_photo.setImageBitmap(bmpUploadedPhoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
