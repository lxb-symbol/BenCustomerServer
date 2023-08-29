package com.ben.bencustomerserver

import android.content.Context
import android.util.TypedValue


object DensityUtil {

    fun dp2px(context:Context,dp:Float):Float =TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.resources.displayMetrics)
}