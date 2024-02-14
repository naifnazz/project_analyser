package com.example.sportanalyzer;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder> {

    private int[] images;

    public ImagePagerAdapter(int[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        switch (position) {
            case 0:
                holder.imageView.setImageResource(images[position]);
                break;
            case 1:
                holder.imageView.setImageResource(images[position]);
                break;
            case 2:
                holder.imageView.setImageResource(images[position]);
                break;
            case 3:
                holder.imageView.setImageResource(images[position]);
                break;
            // Add more cases if needed
        }
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView1); // Adjust ID based on the position
        }
    }
}
