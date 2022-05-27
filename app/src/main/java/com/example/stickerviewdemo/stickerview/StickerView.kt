package com.example.stickerviewdemo.stickerview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class StickerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : View(context, attributeSet, defStyle) {

    val TAG = "StickerView"

    //    var viewModelStoreOwner:ViewModelStoreOwner?=null
//    val viewModel by lazy {
//        ViewModelProvider(viewModelStoreOwner!!).get(StickerViewModel::class.java)
//    }
//
//    init {
//        if(viewModelStoreOwner == null){
//            viewModelStoreOwner = ViewTreeViewModelStoreOwner.get(this)
//            Log.e(TAG, "if you want to use viewModel to manage this view,init viewModelStoreOwner first.", )
//        }
//    }
    private val presenter = StickerPresenter(this)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        presenter.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return presenter.onTouchEvent(event)
    }

    /**
     * 背景图，缩放、移动
     */
    fun setBackgroundImage(image: Any) {
        presenter.setBackgroundImage(image)
    }
}