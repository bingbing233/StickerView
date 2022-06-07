package com.example.stickerviewdemo.stickerview.drawer

import android.graphics.Bitmap
import android.graphics.Matrix.MSCALE_X
import com.example.stickerviewdemo.stickerview.StickerView

/**
 * 贴纸背景
 */
class BackgroundDrawer(stickerView: StickerView, bitmap: Bitmap): Drawer(stickerView,bitmap)  {

    override fun onInit() {
        //缩放图片与view等宽，将图片移动到view中间
        val ratio = stickerView.width.toFloat() / bitmap.width
        matrix.setScale(ratio, ratio, bitmap.width / 2f, bitmap.height / 2f)
        matrix.getValues(array)
        val dx = (bitmap.width - bitmap.width * array[MSCALE_X]) / 2
        val dy = (bitmap.height - bitmap.height * array[MSCALE_X]) / 2
        matrix.postTranslate(-1 * dx, -1 * dy)
        matrix.postTranslate(0f, (stickerView.height - bitmap.height * array[MSCALE_X]) / 2)
    }

    override fun allowRotation(): Boolean  = false
    override fun allowScale(): Boolean = false
}