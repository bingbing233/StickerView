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
class BackgroundDrawer(private val stickerView: StickerView) : IDrawer {

    val TAG = "BackgroundDrawer"
    private var bitmap =
        BitmapFactory.decodeResource(stickerView.context.resources, R.drawable.lena)
    private val paint = Paint()

    //保存图片缩放、位置、旋转等信息
    private val matrix = Matrix()

    //matrix展开后的数组 MTRANS_X
    private val array = FloatArray(9)
    private val maxScale = 2.0f
    private val minScale = 0.2f
    private val rectF = RectF()
    private val p = Paint().apply {
        color = Color.RED
        this.strokeWidth = 10f
        style = Paint.Style.STROKE
    }
    private val pAngle = Paint().apply {
        color = Color.RED
        textSize = 50f
    }

    init {
        stickerView.post {
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
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawBitmap(bitmap, matrix, paint)
        canvas?.drawRect(rectF, p)
        canvas?.drawCircle(array[MTRANS_X], array[MTRANS_Y], 4f, p)
    }

    private var point1 = PointF(0f, 0f)
    private var point2 = PointF(0f, 0f)
    private var distance = 0f

    var canMove = true
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
                if (event.pointerCount == 1 && canMove) {
                    val dx = event.x - point1.x
                    val dy = event.y - point1.y
                    matrix.postTranslate(dx, dy)
                    point1.x = event.x
                    point1.y = event.y
                    mapRect()
                    stickerView.invalidate()
                }

                if (event.pointerCount == 2) {
                    canMove = false
                    val p1 = PointF(event.getX(0), event.getY(0))
                    val p2 = PointF(event.getX(1), event.getY(1))
                    val newDistance = StickerUtils.calculateDistance(p1, p2)
                    val newRatio = newDistance / distance
                    matrix.getValues(array)
                    val x = array[MTRANS_X]
                    val y = array[MTRANS_Y]
                    val oldRatio = array[MSCALE_X]
//                    if ((oldRatio >= maxScale && newRatio < 1)
//                        || (oldRatio <= minScale && newRatio > 1)
//                        || oldRatio in minScale..maxScale
//                    ) {
                    //双指缩放
                    matrix.postScale(newRatio,
                        newRatio,
                        x + bitmap.width * oldRatio / 2,
                        y + bitmap.height * oldRatio / 2)
//                    }
                    distance = newDistance
                    //双指旋转
                    val d1 = StickerUtils.calculateDegree(point1, point2)
                    val d2 = StickerUtils.calculateDegree(p1, p2)
                    matrix.postRotate((d2 - d1).toFloat(),
                        (rectF.left + rectF.right) / 2,
                        (rectF.top + rectF.bottom) / 2)
                    point1 = p1
                    point2 = p2
                    mapRect()
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
                mapRect()
                val x = rectF.left
                val y = rectF.top
                rebound(x, y)
                canMove = true
            }
        }
        return true
    }

    /**
     * 当图片超出view的边界时，回弹到中心
     */
    private fun rebound(x: Float, y: Float) {
        val vW = stickerView.width
        val vH = stickerView.height
        val scaleRatio = array[MSCALE_X]
        val bW = bitmap.width * scaleRatio
        val bH = bitmap.height * scaleRatio
        mapRect()
        if (rectF.top < 0 || rectF.right > vW || rectF.left < 0 || rectF.bottom > vH) {
            val anim = ValueAnimator.ofFloat(1f, 0f)
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.addUpdateListener {
                mapRect()
                matrix.getValues(array)
                val value = it.animatedValue as Float
                if (rectF.top < 0) {
                    matrix.postTranslate(0f, y * value - rectF.top)
                }
                if (rectF.bottom > vH) {
                    matrix.postTranslate(0f, (y + rectF.height() - vH) * value + vH - rectF.bottom)
                }
                if (rectF.left < 0) {
                    matrix.postTranslate(x * value - rectF.left, 0f)
                }
                if (rectF.right > vW) {
                    matrix.postTranslate((x + rectF.width() - vW) * value + vW - rectF.right, 0f)
                }
                stickerView.invalidate()
            }
            anim.start()
        }
    }

//    private fun rebound(x: Float, y: Float) {
//        matrix.getValues(array)
//        val scaleRatio = array[MSCALE_X]
//        val bW = bitmap.width * scaleRatio
//        val bH = bitmap.height * scaleRatio
//        val vW = stickerView.width
//        val vH = stickerView.height
//
//        if (x < 0 ||
//            y < 0 ||
//            x + bW > vW ||
//            y + bH > vH
//        ) {
//            val anim = ValueAnimator.ofFloat(1f, 0f)
//            anim.interpolator = AccelerateDecelerateInterpolator()
//            anim.addUpdateListener {
//                val value = it.animatedValue as Float
//                matrix.getValues(array)
//                if (x < 0) {
//                    matrix.postTranslate(x * value - array[MTRANS_X], 0f)
//                }
//                if (x + bW > vW) {
//                    matrix.postTranslate((x - (vW - bW)) * value + (vW - bW) - array[MTRANS_X], 0f)
//                }
//
//                if (y < 0) {
//                    matrix.postTranslate(0f, y * value - array[MTRANS_Y])
//                }
//                if (y + bH > vH) {
//                    matrix.postTranslate(0f, (y - (vH - bH)) * value + (vH - bH) - array[MTRANS_Y])
//                }
//                mapRect()
//                stickerView.invalidate()
//            }
//            anim.start()
//        }
//    }

    /**
     * 图片映射成矩形
     */

    private fun mapRect() {
        matrix.getValues(array)
        val width = abs(bitmap.width * array[MSCALE_X])
        val height = abs(bitmap.height * array[MSCALE_X])
        val angle = getAngle()
        var radian = Math.toRadians(getAngle())
        val line = abs(width * tan(abs(radian)).toFloat())  //是那条短的变

        when (angle) {
            in -90.0..0.0 -> {
                rectF.left = array[MTRANS_X] - line
                rectF.right = array[MTRANS_X] + width
                rectF.top = array[MTRANS_Y]
                rectF.bottom = array[MTRANS_Y] + height + line
            }
            in 0.0..90.0 -> {
                rectF.left = array[MTRANS_X]
                rectF.right = array[MTRANS_X] + line + width
                rectF.top = array[MTRANS_Y] - line
                rectF.bottom = array[MTRANS_Y] + height
            }
            in -180.0..-90.0 -> {
                rectF.left = array[MTRANS_X] - height - line
                rectF.right = array[MTRANS_X]
                rectF.top = array[MTRANS_Y] - height
                rectF.bottom = array[MTRANS_Y] + line
            }
            in 90.0..180.0 -> {
                rectF.left = array[MTRANS_X] - width
                rectF.right = array[MTRANS_X] + line
                rectF.top = array[MTRANS_Y] - height - line
                rectF.bottom = array[MTRANS_Y]
            }
        }
    }

    /**
     * 图片旋转角度
     */
    @JvmName("getAngle1")
    private fun getAngle(): Double {
        return (atan2(array[MSKEW_X], array[MSCALE_X]) * (180 / Math.PI))
    }

    fun setImage(image: Any) {
        bitmap = StickerUtils.setImage(stickerView.context, image)
    }
}