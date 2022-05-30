package com.example.stickerviewdemo.stickerview

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.TypedValue
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

    fun dp2px(dp:Float): Float {
      return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp, Resources.getSystem().displayMetrics)
    }

    fun px2dp(px:Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,px,Resources.getSystem().displayMetrics)
    }
}