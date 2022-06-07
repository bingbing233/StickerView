package com.example.stickerviewdemo.stickerview

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.view.MotionEvent
import com.example.stickerviewdemo.R

/**
 * p类,对贴纸、背景进行操作
 */
class StickerPresenter(private val stickerView: StickerView) {

    val context = stickerView.context
    val bitmap1 = BitmapFactory.decodeResource(context.resources, R.drawable.wind)
    val bitmap2 = BitmapFactory.decodeResource(context.resources, R.drawable.water)
    val lena = BitmapFactory.decodeResource(stickerView.context.resources, R.drawable.lena)

    private val backgroundDrawer = BackgroundDrawer(stickerView,lena)
    private val stickerDrawers = ArrayList<Drawer>().apply {
        add(StickerDrawer(stickerView,bitmap1))
        add(StickerDrawer(stickerView,bitmap2))
    }

    fun setBackgroundImage(image:Any){
//        backgroundDrawer.setImage(image)
    }

    fun onDraw(canvas: Canvas?){
        backgroundDrawer.onDraw(canvas)
        stickerDrawers.forEach {
            it.onDraw(canvas)
        }
    }

    fun onTouchEvent(event: MotionEvent?):Boolean{
        for (stickerDrawer in stickerDrawers) {
            if (stickerDrawer.onTouchEvent(event))
                return true
        }
        return backgroundDrawer.onTouchEvent(event)
    }
}