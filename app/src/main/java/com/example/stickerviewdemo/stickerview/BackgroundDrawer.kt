package com.example.stickerviewdemo.stickerview

import android.animation.ValueAnimator
import android.graphics.*
import android.media.Image
import android.util.Log
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.stickerviewdemo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

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

    //缩放
    private var ratio = -1f

    init {
        stickerView.post {
            ratio = stickerView.width.toFloat() / bitmap.width
            val tempMatrix = Matrix()
            tempMatrix.setScale(ratio,ratio)
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,tempMatrix,false)
                withContext(Dispatchers.Main){
                    stickerView.invalidate()
                }
            }
        }
    }

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
                matrix.getValues(array)
                val x = array[2]
                val y = array[5]
                rebound(x, y)
            }
        }
        return true
    }

    /**
     * 当图片超出view的边界时，回弹到中心
     */
    private fun rebound(x: Float, y: Float) {
        val bW = bitmap.width
        val bH = bitmap.height
        val vW = stickerView.width
        val vH = stickerView.height

        if (x < 0 ||
            y - bH / 2 + vH / 2 < 0 ||
            x + bW > vW ||
            y + bH > vH
        ) {
            val anim = ValueAnimator.ofFloat(1f, 0f)
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.addUpdateListener {
                val value = it.animatedValue as Float
                matrix.getValues(array)
                if (x < 0 || x + bW > vW) {
                    matrix.postTranslate(x * value - array[2], 0f)
                }
                //y表示它本来在哪； array[5]表示当前在哪
                if (y < 0) {
                    matrix.postTranslate(0f, y * value - array[5])
                }
                if (y + bH > vH) {
                    //计算当前需要移动的距离 = 当前目的位置 - 当前位置
                    matrix.postTranslate(0f,(y-(vH-bH)) * value + (vH-bH) -array[5])
                }
                stickerView.invalidate()
            }
            anim.start()
        }
    }

    fun setImage(image: Any) {
        bitmap = ImageUtils.setImage(stickerView.context, image)
    }
}