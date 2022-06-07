package com.example.stickerviewdemo.stickerview

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.Matrix.*
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.stickerviewdemo.R
import kotlin.math.*

/**
 * 贴纸背景
 */
class BackgroundDrawer( stickerView: StickerView,bitmap: Bitmap):Drawer(stickerView,bitmap)  {

    private val maxScale = 2.0f
    private val minScale = 0.2f
    private val p = Paint().apply {
        color = Color.RED
        this.strokeWidth = 10f
        style = Paint.Style.STROKE
    }

     override fun onDraw(canvas: Canvas?) {
        canvas?.drawBitmap(bitmap, matrix, paint)
        canvas?.drawRect(rectF, p)
    }

    override fun onInit() {
        //缩放图片与view等宽，将图片移动到view中间
        val ratio = stickerView.width.toFloat() / bitmap.width
        matrix.setScale(ratio, ratio, bitmap.width / 2f, bitmap.height / 2f)
        matrix.getValues(array)
        val dx = (bitmap.width - bitmap.width * array[MSCALE_X]) / 2
        val dy = (bitmap.height - bitmap.height * array[MSCALE_X]) / 2
        matrix.postTranslate(-1 * dx, -1 * dy)
        matrix.postTranslate(0f, (stickerView.height - bitmap.height * array[MSCALE_X]) / 2)
        stickerView.invalidate()
    }

    private var point1 = PointF(0f, 0f)
    private var point2 = PointF(0f, 0f)
    private var distance = 0f

}