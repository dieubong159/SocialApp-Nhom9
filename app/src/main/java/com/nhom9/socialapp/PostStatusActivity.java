package com.nhom9.socialapp;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.mhmtk.twowaygrid.TwoWayGridView;
import com.nhom9.socialapp.Adapter.GalleryAdapter;
import com.nhom9.socialapp.Model.User;
import com.rengwuxian.materialedittext.MaterialMultiAutoCompleteTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class PostStatusActivity extends AppCompatActivity {

    MaterialMultiAutoCompleteTextView post_content;
    ImageButton btn_add_photo,btn_share;
    TwoWayGridView uploaded_photo;
    private static final int IMAGE_REQUEST_MULTIPLE = 1;
    ArrayList<Uri> lstImageUri;
    String timeStamp, imageEncoded;
    StorageReference storageReference;
    private StorageTask uploadTask;
    List<String> imagelist;
    private GalleryAdapter galleryAdapter;
    Integer count = 0;


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
        lstImageUri = new ArrayList<>();
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
                    if(lstImageUri != null) {
                        uploadImage();
                    }else {
                        uploadPostNoImage();
                    }
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

    private void uploadPostNoImage(){
        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("content", post_content.getText().toString().trim());
        hashMap.put("image", "none");
        hashMap.put("owner", owner);
        timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        hashMap.put("uploadtime", timeStamp);

        reference.child("Posts").push().setValue(hashMap);
        Intent intent = new Intent(PostStatusActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void uploadPostWithImage(){
        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("content", post_content.getText().toString().trim());
        hashMap.put("image", imagelist);
        hashMap.put("owner", owner);
        timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        hashMap.put("uploadtime", timeStamp);

        reference.child("Posts").push().setValue(hashMap);
        Intent intent = new Intent(PostStatusActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), IMAGE_REQUEST_MULTIPLE);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = PostStatusActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

     private void uploadImage(){
        imagelist.clear();
        final ProgressDialog pd = new ProgressDialog(PostStatusActivity.this);
        pd.setMessage("Uploading");
        pd.show();

        if(lstImageUri!=null){
            for (Uri uri : lstImageUri) {
                final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(uri));

                uploadTask = fileReference.putFile(uri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return fileReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String mUri = downloadUri.toString();
                            imagelist.add(mUri);
                            if(imagelist.size() == lstImageUri.size()){
                                uploadPostWithImage();
                                pd.dismiss();
                            }
                        } else {
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
            }
        }else {
            Toast.makeText(PostStatusActivity.this, "No image selected!!", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
//                && data != null && data.getData() != null){
//            imageUri = data.getData();
//            try {
//                bmpUploadedPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                uploaded_photo.setImageBitmap(bmpUploadedPhoto);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        try {
            //When an image is picked
            if(requestCode == IMAGE_REQUEST_MULTIPLE && resultCode == RESULT_OK
                    && data != null){
                //Get the image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagelist = new ArrayList<>();
                if(data.getData() != null){
                    lstImageUri.add(data.getData());
                    imagelist.clear();

                    //Get the cursor
                    Cursor cursor = getContentResolver().query(lstImageUri.set(0, data.getData()), filePathColumn, null, null,null);
                    //Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    imagelist.add(imageEncoded);
                    cursor.close();

//                    ArrayList<Uri> mArrayUri = new ArrayList<>();
//                    mArrayUri.add(lstImageUri.set(0, data.getData()));
                    galleryAdapter = new GalleryAdapter(getApplicationContext(), lstImageUri);
                    uploaded_photo.setAdapter(galleryAdapter);
                }else {
                    if(data.getClipData() != null){
                        imagelist.clear();
                        ClipData mClipData = data.getClipData();
                        for(int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            lstImageUri.add(uri);
                            //Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null,null);
                            //Move the first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagelist.add(imageEncoded);
                            cursor.close();

                            galleryAdapter = new GalleryAdapter(getApplicationContext(), lstImageUri);
                            uploaded_photo.setAdapter(galleryAdapter);
                        }
                        Log.v("LOG_TAG", "Selected Images"+lstImageUri.size());
                    }
                }

            }else {
                Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "Something went wrong",Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
