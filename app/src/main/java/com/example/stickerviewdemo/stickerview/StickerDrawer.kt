package com.example.stickerviewdemo.stickerview

import android.graphics.Bitmap
import kotlin.properties.Delegates

class StickerDrawer(stickerView: StickerView, bitmap: Bitmap):Drawer(stickerView,bitmap) {

    override fun onInit() {
        //初始化位置
        matrix.setTranslate(((stickerView.width-bitmap.width)/2).toFloat(),
            ((stickerView.height-bitmap.height)/2).toFloat())
    }
}