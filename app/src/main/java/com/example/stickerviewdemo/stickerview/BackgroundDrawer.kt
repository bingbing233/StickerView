package com.example.stickerviewdemo.stickerview

import android.animation.ValueAnimator
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.stickerviewdemo.R

/**
 * 贴纸背景
 */
class BackgroundDrawer(private val stickerView: StickerView) : IDrawer {

    val TAG = "BackgroundDrawer"
    private var bitmap =
        BitmapFactory.decodeResource(stickerView.context.resources, R.drawable.lena)
    private val paint = Paint()

    //保存图片缩放、位置、旋转等信息
    private val matrix = Matrix()

    //matrix展开后的数组 [2][5]代表位置x,y
    private val array = FloatArray(9)

    init {
        stickerView.post {
            //缩放图片与view等宽，将图片移动到view中间
            val ratio = stickerView.width.toFloat() / bitmap.width
            matrix.setScale(ratio, ratio, bitmap.width / 2f, bitmap.height / 2f)
            matrix.getValues(array)
            val dx = (bitmap.width - bitmap.width * array[0]) / 2
            val dy = (bitmap.height - bitmap.height * array[0]) / 2
            matrix.postTranslate(-1 * dx, -1 * dy)
            matrix.postTranslate(0f, (stickerView.height - bitmap.height * array[0]) / 2)
            stickerView.invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawBitmap(bitmap, matrix, paint)
    }

    private var point1 = PointF(0f, 0f)
    private var point2 = PointF(0f, 0f)
    private var distance = 0f

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

                if (event.pointerCount == 2) {
                    val p1 = PointF(event.getX(0), event.getY(0))
                    val p2 = PointF(event.getX(1), event.getY(1))
                    val newDistance = StickerUtils.calculateDistance(p1, p2)
                    val newRatio = newDistance / distance
                    matrix.getValues(array)
                    val x = array[2]
                    val y = array[5]
                    val oldRatio = array[0]

                    //双指缩放
                    matrix.postScale(newRatio,
                        newRatio,
                        x + bitmap.width * oldRatio / 2,
                        y + bitmap.height * oldRatio / 2)
                    distance = newDistance

                    //双指旋转
                    val d1 = StickerUtils.calculateDegree(point1,point2)
                    val d2 = StickerUtils.calculateDegree(p1, p2)
                    matrix.postRotate((d2-d1).toFloat(),
                        x + bitmap.width * oldRatio / 2,
                        y + bitmap.height * oldRatio / 2)
                    point1 = p1
                    point2 = p2
                    stickerView.invalidate()
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {

                if (event.pointerCount == 2) {
                    point1 = PointF(event.getX(0), event.getY(0))
                    point2 = PointF(event.getX(1), event.getY(1))
                    distance = StickerUtils.calculateDistance(point1, point2)
                }
            }

            MotionEvent.ACTION_UP -> {
                matrix.getValues(array)
                val x = array[2]
                val y = array[5]
                Log.e(TAG, "onTouchEvent: $x $y")
                rebound(x, y)
            }
        }
        return true
    }

    /**
     * 当图片超出view的边界时，回弹到中心
     */
    private fun rebound(x: Float, y: Float) {
        matrix.getValues(array)
        val scaleRatio = array[0]
        val bW = bitmap.width * scaleRatio
        val bH = bitmap.height * scaleRatio
        val vW = stickerView.width
        val vH = stickerView.height

        if (x < 0 ||
            y < 0 ||
            x + bW > vW ||
            y + bH > vH
        ) {
            val anim = ValueAnimator.ofFloat(1f, 0f)
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.addUpdateListener {
                val value = it.animatedValue as Float
                matrix.getValues(array)
                if (x < 0) {
                    matrix.postTranslate(x * value - array[2], 0f)
                }
                if (x + bW > vW) {
                    matrix.postTranslate((x - (vW - bW)) * value + (vW - bW) - array[2], 0f)
                }

                if (y < 0) {
                    matrix.postTranslate(0f, y * value - array[5])
                }
                if (y + bH > vH) {
                    matrix.postTranslate(0f, (y - (vH - bH)) * value + (vH - bH) - array[5])
                }
                stickerView.invalidate()
            }
            anim.start()
        }
    }

    fun setImage(image: Any) {
        bitmap = StickerUtils.setImage(stickerView.context, image)
    }
}