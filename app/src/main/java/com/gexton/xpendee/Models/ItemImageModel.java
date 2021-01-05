package com.gexton.xpendee.Models;

import android.graphics.Bitmap;

public class ItemImageModel {
    Bitmap image;

    public ItemImageModel(Bitmap image) {
        this.image = image;
    }

    public ItemImageModel() {
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
