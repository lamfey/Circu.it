package com.example.circuit.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.circuit.Interface.ItemClickListener;
import com.example.circuit.ItemDetailsAndInfo;
import com.example.circuit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ItemViewHolder> implements View.OnClickListener{

    Context context;
    ArrayList<Item> list;
    private ItemClickListener itemClickListener;

    public MyAdapter(Context context, ArrayList<Item> list, ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = list.get(position);
        holder.itemName.setText(item.getName());
        holder.description.setText(item.getDescription());
        holder.price.setText(item.getPrice());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(list.get(position));
            }
        });
        StorageReference imgRef2 = FirebaseStorage.getInstance().getReference("Images/"+ item.getName());

        System.out.println(imgRef2.toString());
        try {
            final File localFile = File.createTempFile(item.getName(), "jpg");
            imgRef2.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                  Bitmap bt = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    holder.image.setImageBitmap(bt);
                }
            });
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View v) {

    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView itemName, description, price;
        ImageView image;

        CardView cardView;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.dbitemname);
            description = itemView.findViewById(R.id.dbitemdesc);
            image = itemView.findViewById(R.id.itemImage);
            price = itemView.findViewById(R.id.dbitemprice);
            cardView= itemView.findViewById(R.id.cardd);
        }
    }
}
