package com.ben.bencustomerserver.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * 为了解决出现“trying to use a recycled bitmap android.graphics.Bitmap@2d46e6b”的异常
 */
class EImageView : AppCompatImageView {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    )

    override fun onDraw(canvas: Canvas) {
        try {
            super.onDraw(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}