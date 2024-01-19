package com.example.circuit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.circuit.Models.InventoryStoreItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ItemDetailsAndInfo extends AppCompatActivity {

    Button btn, msgbtn;
    DatabaseReference dbref;
    EditText editName, editDescription,editPrice, phone;
    FirebaseStorage storage;
    ImageView imgview;
    boolean toggled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbref = FirebaseDatabase.getInstance().getReference("Inventory");

        storage = FirebaseStorage.getInstance();
        setContentView(R.layout.activity_item_details_and_info);

        String name = getIntent().getExtras().getString("name");
        String desc = getIntent().getExtras().getString("description");
        String img = getIntent().getExtras().getString("image");
        String price = getIntent().getExtras().getString("price");

        editName = findViewById(R.id.editName);
        editDescription = findViewById(R.id.editDescription);
        imgview = findViewById(R.id.imagePreview);
        editPrice = findViewById(R.id.editPrice);
        editName.setText(name);
        editDescription.setText(desc);
        editPrice.setText(price);

        StorageReference imgRef2 = FirebaseStorage.getInstance().getReference("Images/"+ name);

        System.out.println(imgRef2.toString());
        try {
            final File localFile = File.createTempFile(name, "jpg");
            imgRef2.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ItemDetailsAndInfo.this, "Picture gotten", Toast.LENGTH_SHORT).show();
                    Bitmap bt = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    imgview.setImageBitmap(bt);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ItemDetailsAndInfo.this, "Picture retrieval failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void EditITemToggleClick(View view) {
        btn = findViewById(R.id.editItemButtonToggle);
        editName = findViewById(R.id.editName);
        editDescription = findViewById(R.id.editDescription);
        editPrice = findViewById(R.id.editPrice);

        toggled = !toggled;
        if(toggled){
            btn.setText("Save");
            editName.setFocusableInTouchMode(true);
            editDescription.setFocusableInTouchMode(true);
            editPrice.setFocusableInTouchMode(true);
        }
        else{
            String varName = editName.getText().toString();
            String varDesc = editDescription.getText().toString();
            String varPrice = editPrice.getText().toString();

                BitmapDrawable drawable = (BitmapDrawable) imgview.getDrawable();
                Bitmap photo = drawable.getBitmap();
                Bitmap image = Bitmap.createScaledBitmap(photo, imgview.getWidth(), imgview.getHeight(), true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String name = getIntent().getExtras().getString("name");
                photo.recycle();
                StorageReference imgRef2 = storage.getReference("Images/" + name);

                //insert the items into the database
                InventoryStoreItem inven = new InventoryStoreItem(varName, imgRef2.toString() +"/"+ varName, varDesc, varPrice);

                Toast.makeText(ItemDetailsAndInfo.this,"changes made",Toast.LENGTH_SHORT).show();
                uploadPicture(byteArray, varName);
                deleteimage(name);
                dbref.child(name).removeValue();
                dbref.child(varName).setValue(inven);
                editName.setFocusableInTouchMode(false);
                editDescription.setFocusableInTouchMode(false);
                editPrice.setFocusableInTouchMode(false);


            btn.setText("Edit Item");
        }
    }

    private void uploadPicture(byte[] arr, String name) {
        StorageReference imgRef2 = storage.getReference("Images/" + name);
        String choice;
        imgRef2.putBytes(arr).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ItemDetailsAndInfo.this, "upload done", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ItemDetailsAndInfo.this, "upload fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteimage(String name){
        StorageReference imgRef2 = storage.getReference("Images/" + name);
        imgRef2.delete();
    }


    public void sendAsSMS(View view) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View addSMSView = getLayoutInflater().inflate(R.layout.send_sms, null);
        phone = (EditText) addSMSView.findViewById(R.id.phoneNo);
        msgbtn = (Button) addSMSView.findViewById(R.id.msgsend);
        dialogBuilder.setView(addSMSView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        msgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("now");
                if(phone.getText().toString().isEmpty()){
                    Toast.makeText(ItemDetailsAndInfo.this, "Phone number must not be blank", Toast.LENGTH_SHORT).show();
                } else if (phone.getText().toString().matches("[0-9]+")) {

                    Uri uri = Uri.parse("smsto:" + phone.getText().toString());
                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                    it.putExtra("sms_body", "Hi, please help me get this item " + editName.getText().toString() + "From the store. It costs "+ editPrice.getText().toString());
                    startActivity(it);

                }
                else{
                    Toast.makeText(ItemDetailsAndInfo.this, "This must contain only numbers", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}