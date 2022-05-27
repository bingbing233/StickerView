package com.example.stickerviewdemo.stickerview

import android.graphics.Canvas
import android.view.MotionEvent

/**
 * p类,对贴纸、背景进行操作
 */
class StickerPresenter(private val stickerView: StickerView) {

    private val backgroundDrawer = BackgroundDrawer(stickerView)

    fun setBackgroundImage(image:Any){
        backgroundDrawer.setImage(image)
    }

    fun onDraw(canvas: Canvas?){
        backgroundDrawer.onDraw(canvas)
    }

    fun onTouchEvent(event: MotionEvent?):Boolean{
        backgroundDrawer.onTouchEvent(event)
        return true
    }
}