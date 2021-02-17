package com.gexton.xpendee.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gexton.xpendee.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    int[] images;
    Context context;
    int index = 0;

    public ImageAdapter(Context context, int[] images) {
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_image, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {

        holder.row_image.setImageResource(images[position]);

        holder.row_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = position;
                notifyDataSetChanged();
            }
        });

        int drawableResId = index != position ? R.drawable.cirrcle_empty : R.drawable.circle_for_images;
        holder.row_image.setBackgroundResource(drawableResId);

        if (index == position) {
            holder.row_image.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            holder.row_image.setColorFilter(ContextCompat.getColor(context, R.color.dark_grey), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView row_image;
        RelativeLayout item_background_layout;

        public ViewHolder(@NonNull View view) {
            super(view);
            row_image = view.findViewById(R.id.image_view);
            item_background_layout = view.findViewById(R.id.item_background_layout);
        }
    }
}