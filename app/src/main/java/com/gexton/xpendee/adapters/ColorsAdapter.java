package com.gexton.xpendee.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gexton.xpendee.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ViewHolder> {
    Context context;
    int index = 0;
    String[] colors;

    public ColorsAdapter(Context context, String[] colors) {
        this.context = context;
        this.colors = colors;
    }

    @NonNull
    @Override
    public ColorsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_colors, null);
        ColorsAdapter.ViewHolder viewHolder = new ColorsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ColorsAdapter.ViewHolder holder, int position) {

        holder.text_view_color.setColorFilter(Color.parseColor(colors[position]), PorterDuff.Mode.SRC_IN);
        //holder.text_view_color.setColorFilter(Color.parseColor(colors[position]), PorterDuff.Mode.MULTIPLY);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = position;
                notifyDataSetChanged();
            }
        });

        int drawableResId = index != position ? R.drawable.cirrcle_empty : R.drawable.circle_for_colors;
        holder.item_background_layout.setBackgroundResource(drawableResId);

        if (index == position) {
            GradientDrawable background = (GradientDrawable) holder.item_background_layout.getBackground();
            background.setColor(Color.parseColor(colors[position]));
        }
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView text_view_color;
        RelativeLayout item_background_layout;

        public ViewHolder(@NonNull View view) {
            super(view);
            text_view_color = view.findViewById(R.id.text_view_color);
            item_background_layout = view.findViewById(R.id.item_background_layout);
        }
    }
}