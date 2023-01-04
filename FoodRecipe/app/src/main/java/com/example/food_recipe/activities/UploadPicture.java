package com.example.pj_off.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.pj_off.R;
import com.example.pj_off.apCompact.MyApp;
import com.example.pj_off.databinding.ActivityUploadPictureBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadPicture extends MyApp {

    ActivityUploadPictureBinding uploadPictureBinding;
    private Uri imagePickUri;
    private ProgressDialog dialog;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uploadPictureBinding = DataBindingUtil.setContentView(this, R.layout.activity_upload_picture);

        storageReference = FirebaseStorage.getInstance().getReference("User Picture");
        buttonFunction();
    }

    private void buttonFunction() {
        uploadPictureBinding.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    ImagePicker.with(UploadPicture.this)  //On adapte l'image
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        uploadPictureBinding.uploadPic.setOnClickListener(view -> {
            if (imagePickUri == null) {
                Toast.makeText(UploadPicture.this, "Select Image", Toast.LENGTH_SHORT).show();

            } else {
                uploadImageFun();
            }
        });
    }

    private void uploadImageFun() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setTitle(getResources().getString(R.string.please_w8));
        StorageReference sRef = storageReference.child(String.valueOf(System.currentTimeMillis()));
        sRef.putFile(imagePickUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri downloadUri) {
                                                                        dialog.dismiss();
                                                                        Toast.makeText(UploadPicture.this, "Upload Image Successfully", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        }
        ).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                float percent = (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                dialog.setMessage("Uploaded :" + (int) percent + "%");
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            imagePickUri = data.getData();
            uploadPictureBinding.picture.setImageURI(imagePickUri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}