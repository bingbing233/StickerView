package com.example.stickerviewdemo.stickerview.drawer

import android.graphics.Bitmap
import com.example.stickerviewdemo.stickerview.StickerView

/**
 * 贴纸类，初始位置为view中间
 */
class StickerDrawer(stickerView: StickerView, bitmap: Bitmap): Drawer(stickerView,bitmap) {

    override fun onInit() {
        //初始化位置
//        matrix.setTranslate(((stickerView.measuredWidth-bitmap.width)/2).toFloat(),
//            ((stickerView.measuredHeight-bitmap.height)/2).toFloat())
        matrix.setTranslate(100f,100f)
    }
}