package com.example.stickerviewdemo.stickerview

import android.animation.ValueAnimator
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.example.stickerviewdemo.R

/**
 * 贴纸背景
 */
class BackgroundDrawer(private val stickerView: StickerView) : IDrawer {

    private var bitmap =
        BitmapFactory.decodeResource(stickerView.context.resources, R.drawable.lena)
    private val paint = Paint()

    //保存图片缩放、位置、旋转等信息
    private val matrix = Matrix()

    //matrix展开后的数组 [2][5]代表位置x,y
    private val array = FloatArray(9)

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawBitmap(bitmap, matrix, paint)
    }

    private val point1 = PointF(0f, 0f)
    private val point2 = PointF(0f, 0f)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //这里使用actionMasked，只有actionMask才能检测到多指按下
        when (event?.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                if (event.pointerCount == 1) {
                    point1.x = event.x
                    point1.y = event.y
                }
            }

            MotionEvent.ACTION_MOVE -> {
                //单指移动背景
                if (event.pointerCount == 1) {
                    val dx = event.x - point1.x
                    val dy = event.y - point1.y
                    matrix.postTranslate(dx, dy)
                    point1.x = event.x
                    point1.y = event.y
                    stickerView.invalidate()
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {

            }

            MotionEvent.ACTION_UP -> {
                Log.e("TAG", "onTouchEvent: $matrix")

                matrix.getValues(array)
                val x = array[2]
                val y = array[5]
                //出界回弹
                if (x < 0 || y < 0) {
                    val anim = ValueAnimator.ofFloat(0f, 1f)
                    anim.interpolator = AccelerateDecelerateInterpolator()
                    anim.addUpdateListener {
                        val value = it.animatedValue as Float
                        matrix.getValues(array)
                        matrix.setTranslate(x + x * value * -1, y + y * value * -1)
                        stickerView.invalidate()
                    }
                    anim.start()
                }
            }
        }
        return true
    }

    fun setImage(image: Any) {
        bitmap = ImageUtils.setImage(stickerView.context, image)
    }
}