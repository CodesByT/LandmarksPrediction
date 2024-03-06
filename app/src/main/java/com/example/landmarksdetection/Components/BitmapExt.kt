package com.example.landmarksdetection.Components

import android.graphics.Bitmap

fun Bitmap.centerCrop(
    d_width: Int,
    d_height: Int
): Bitmap {

    val xStart = (width - d_width) / 2
    val yStart = (height - d_height) / 2

    if (xStart < 0 || yStart < 0 || d_width > width || d_height > height) {
        throw IllegalArgumentException("Invalid Arguments for center cropping")
    }

    return Bitmap.createBitmap(this,xStart,yStart,d_width,d_height)
}