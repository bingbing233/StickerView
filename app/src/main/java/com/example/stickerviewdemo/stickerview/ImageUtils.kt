package com.example.stickerviewdemo.stickerview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.InputStream

object ImageUtils {

    fun setImage(context: Context? = null, image:Any): Bitmap? {
        when(image){
            is String -> {
                return BitmapFactory.decodeFile(image)
            }

            is Uri -> {
                return BitmapFactory.decodeStream(context?.contentResolver?.openInputStream(image))
            }

            is Bitmap -> {
             return image
            }
            else -> {
                return null
            }
        }
    }
}