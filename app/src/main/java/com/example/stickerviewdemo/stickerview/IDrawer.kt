package com.example.stickerviewdemo.stickerview

import android.graphics.Canvas
import android.view.MotionEvent

interface IDrawer {
    fun onDraw(canvas: Canvas?)
    fun onTouchEvent(event:MotionEvent?):Boolean
}