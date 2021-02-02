package com.gexton.xpendee.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gexton.xpendee.R;
import com.gexton.xpendee.model.CategoryBean;
import com.gexton.xpendee.model.ImageBean;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageBeanAdapter extends RecyclerView.Adapter<ImageBeanAdapter.MyViewHolder> {
    Context context;
    ArrayList<String> imageBeansList;
    //ImageBean imageBean;

    public ImageBeanAdapter(Context context, ArrayList<String> imageBeans) {
        this.context = context;
        this.imageBeansList = imageBeans;
    }

    @NonNull
    @Override
    public ImageBeanAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_image_gallery, parent, false);
        return new ImageBeanAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageBeanAdapter.MyViewHolder holder, int position) {
        //imageBean = imageBeansList.get(position);
        //holder.tv_cat_name.setText(categoryBean.categoryName);
        String imageBean = imageBeansList.get(position);
        File file = new File(imageBean);
        Picasso.get().load(file).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageBeansList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

}
