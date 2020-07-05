package com.example.bcard;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private Button captureimagebutton, detectimagebutton,listbutton;
    private ImageView imageView;
    private TextView info;
    private Bitmap bitmap;
    String currenttext;
    Uri mImageUri;
    String currentPhotoPath;
    private StorageReference mStoreageref;
    private DatabaseReference mDatabaseref;
    private StorageTask mupload;

    String company,email,phone,website,person,address;
    ArrayList<Contact> cardlist=new ArrayList<>();
    contactAdapter contactadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureimagebutton=findViewById(R.id.capture_button);
        detectimagebutton=findViewById(R.id.detect_button);
        imageView=findViewById(R.id.image_view);
        info=findViewById(R.id.details);
        listbutton=findViewById(R.id.list);

        mStoreageref= FirebaseStorage.getInstance().getReference("uploads");
        FirebaseDatabase database =FirebaseDatabase.getInstance();
        mDatabaseref = database.getReference().child("uploads");


        captureimagebutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                CropImage.activity().start(MainActivity.this);
                info.setText("");


            }
        });



        detectimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectTextFromImage();
                if(mupload==null){
                    uploadFile();
                }

            }

        });

        listbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,bcardlist.class);
                startActivity(intent);
            }
        });




    }

    private void uploadFile() {
        if(mImageUri!=null){
            StorageReference fileReference = mStoreageref.child("uploads"+ UUID.randomUUID().toString());
            mupload=fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this,"upload success",Toast.LENGTH_LONG).show();
                    Contact contact= new Contact(taskSnapshot.getDownloadUrl().toString(),company,email,phone,website,person,address);

                    mDatabaseref.push().setValue(contact);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode ,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK ){
                mImageUri=result.getUri();
                imageView.setImageURI(mImageUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),mImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e=result.getError();
                Toast.makeText(this,"possible error is: "+e,Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void detectTextFromImage() {


        final FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                processText(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void processText(FirebaseVisionText text){
        List<FirebaseVisionText.Block> blocks= text.getBlocks();
        if(blocks.size() == 0){
            Toast.makeText(MainActivity.this,"notext",Toast.LENGTH_LONG).show();
            return;
        }
        for (FirebaseVisionText.Block block :text.getBlocks()){
            currenttext = block.getText();
            info.append(currenttext);
            for(FirebaseVisionText.Line line:block.getLines()) {
                String linetext = line.getText();

                if (linetext != null && !linetext.isEmpty()) {
                    Toast.makeText(this, "text detected", Toast.LENGTH_LONG).show();
                    if (linetext.toString().matches(".[A-Z].[^@$#/-<>!]+")) {
                        company = linetext;

                    } else if (company==null){
                        company="no info";
                    }
                    if (linetext.matches("^[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*@\"\n" +
                            "\t\t+ \"[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$") || linetext.toString().contains("@")) {
                        email = linetext;

                    }else if (email==null){
                        email="no info";
                    }
                    if (linetext.trim().contains("^[0-9]{10}$") || linetext.trim().contains("^\\+\\d{12}(\\d{2})?$")) {
                        phone = linetext;

                    }else if (phone==null){
                        phone="no info";
                    }
                    if (linetext.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]") || linetext.startsWith("www") || linetext.contains("Website") || linetext.contains("www")) {
                        website = linetext;

                    }else if (website==null){
                        website="no info";
                    }
                    if (linetext.matches("[a-zA-z]+([ '-][a-zA-Z]+)*")) {
                        if (linetext != company) {
                            person = linetext;

                        }
                    }else if (person==null){
                        person="no info";
                    }
                    if (linetext.matches("[a-zA-z]+([ '-][a-zA-Z]+)*") && linetext.contains("Address") || linetext.contains("Office") || linetext.contains("Floor") || linetext.contains("Plaza") || linetext.contains("office") || linetext.contains("Floor") || linetext.contains("Floors") || linetext.contains("floors") || linetext.contains("floor") || linetext.contains("Street") || linetext.contains("Road") || linetext.contains("Estate")) {
                        address = currenttext;

                    }else if (address==null){
                        address="no info";
                    }
                }
            }

        }
        cardlist.add(new Contact(currentPhotoPath,company,email,phone,website,person,address));
        }
    }


