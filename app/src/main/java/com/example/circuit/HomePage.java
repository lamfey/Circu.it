package com.example.circuit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.circuit.Interface.ItemClickListener;
import com.example.circuit.Models.InventoryStoreItem;
import com.example.circuit.Models.Item;
import com.example.circuit.Models.MyAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HomePage extends AppCompatActivity implements ItemClickListener {
    RecyclerView recyclerView;
    DatabaseReference dbref;
    MyAdapter myAdapter;
    ArrayList<Item> list;
    EditText makename, makedescription, makeprice;
    Button saveDetails;
    ImageView takenPicture, imageplaceholder;
    ImageButton takePicture;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private FirebaseStorage storage;
    private StorageReference storeRef;
    public Uri imageUri;
    private String uid;
    private FirebaseAuth mAuth;

    private TextView fname;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        storage = FirebaseStorage.getInstance();
        storeRef = storage.getReference();
        recyclerView = findViewById(R.id.itemList);
        dbref = FirebaseDatabase.getInstance().getReference("Inventory");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid().toString();
        fname = findViewById(R.id.textView10);



        DocumentReference dbref2 = db.collection("Users").document(uid);
        if(uid.isEmpty()){
        }
        else{
         dbref2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
             @Override
             public void onSuccess(DocumentSnapshot documentSnapshot) {
                 if (documentSnapshot.exists()){
                    String ffname = documentSnapshot.getString("FirstName");
                     fname.setText("Welcome "+ ffname);
                 }else{
                     Toast.makeText(HomePage.this, "This does not exist", Toast.LENGTH_SHORT).show();
                 }
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(HomePage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
             }
         });
        }
        list = new ArrayList<>();
        myAdapter = new MyAdapter(this, list, this);
        recyclerView.setAdapter(myAdapter);

        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Item item = dataSnapshot.getValue(Item.class);
                    list.add(item);
                }
                myAdapter.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            switch(direction){
                case ItemTouchHelper.LEFT:
                    String name = list.get(position).getName();
                    list.remove(position);
                    myAdapter.notifyItemRemoved(position);
                    dbref.child(name).removeValue().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println(e.getMessage().toString());
                        }
                    });
                    StorageReference imgRef2 = storage.getReference("Images/" + name);
                    imgRef2.delete();
                    break;
                case ItemTouchHelper.RIGHT:
                    Item it = list.get(position);
                    myAdapter.onBindViewHolder((MyAdapter.ItemViewHolder) viewHolder,position);

                    myAdapter.notifyItemChanged(position);
                    viewHolder.itemView.setBackgroundColor(Color.parseColor("#46EA74"));
                    Toast.makeText(HomePage.this, "Item marked as bought", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(HomePage.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(Color.parseColor("#ff0000"))
                    .addSwipeLeftActionIcon(R.drawable.delete_item)
                    .addSwipeRightBackgroundColor(Color.parseColor("#46EA74"))
                    .addSwipeRightActionIcon(R.drawable.baseline_check_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    };
    public void addItemToList(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View addItemView = getLayoutInflater().inflate(R.layout.item_list_form, null);
        makename = (EditText) addItemView.findViewById(R.id.enterName);
        makedescription = (EditText) addItemView.findViewById(R.id.enterDescription);
        makeprice = (EditText) addItemView.findViewById(R.id.enterPrice);
        takenPicture = (ImageView) addItemView.findViewById(R.id.enterImage);
        imageplaceholder = (ImageView) addItemView.findViewById(R.id.enterImage2);
        takePicture = (ImageButton) addItemView.findViewById(R.id.takePicture);
        saveDetails = (Button) addItemView.findViewById(R.id.saveitem);
        dialogBuilder.setView(addItemView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = makename.getText().toString();
                String description = makedescription.getText().toString();
                String price = makeprice.getText().toString();
                 if(hasImage(takenPicture)) {
                    if(name.isEmpty() || description.isEmpty() || price.isEmpty()){
                        Toast.makeText(HomePage.this, "fields cannot be empty", Toast.LENGTH_LONG).show();
                    }
                    else{
                        BitmapDrawable drawable = (BitmapDrawable) takenPicture.getDrawable();
                        Bitmap photo = drawable.getBitmap();
                        Bitmap image = Bitmap.createScaledBitmap(photo, takenPicture.getWidth(), takenPicture.getHeight(), true);

                        //convert to bitmap and upload to cloud storage
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        uploadPicture(byteArray, name);
                        photo.recycle();

                        //insert all the other items into the database
                        StorageReference imgRef2 = storage.getReference("Images/" + name);
                        System.out.println(imgRef2.toString());
                        InventoryStoreItem inven = new InventoryStoreItem(name, imgRef2.toString(), description, price);
                        dbref.child(name).setValue(inven);
                        dialog.dismiss();
                    }
                }else{
                     Toast.makeText(HomePage.this, "a valid image must be taken", Toast.LENGTH_LONG).show();
                 }
            }
        });

    }
    public void imageclicked(View view) {
        Intent myIntent = new Intent(HomePage.this, Login.class);
        startActivity(myIntent);
    }

    public void openListForm(View view) {
        addItemToList();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Bitmap image = Bitmap.createScaledBitmap(photo,takenPicture.getWidth(),takenPicture.getHeight(),true);
            takenPicture.setImageBitmap(image);
            imageplaceholder.setVisibility(View.GONE);
            imageUri = data.getData();

        }
    }

    private void uploadPicture(byte[] arr, String name) {

        StorageReference imgRef = storeRef.child("Images/");
        StorageReference imgRef2 = storage.getReference("Images/" + name);
        String choice;
        imgRef2.putBytes(arr).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(HomePage.this, "upload done", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomePage.this, "upload fail", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(Item item) {


        Intent intent = new Intent(this, ItemDetailsAndInfo.class);
        intent.putExtra("name", item.getName());
        intent.putExtra("description", item.getDescription());
        intent.putExtra("price", item.getPrice());

        int[] arr = new int[]{1,4,5,6,9};
        intent.putExtra("arr", arr);

        startActivity(intent);
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }
}