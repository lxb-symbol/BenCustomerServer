package com.ben.bencustomerserver.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi

object VersionUtils {
    /**
     * 判断当前SDK版本是否是Q版本以上
     * @return
     */
    fun isTargetQ(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= 29 && context.applicationInfo.targetSdkVersion >= 29
    }

    @get:RequiresApi(api = Build.VERSION_CODES.Q)
    val isExternalStorageLegacy: Boolean
        /**
         * 检查app的运行模式
         * @return true 为作用域模式；false为兼容模式
         */
        get() = Environment.isExternalStorageLegacy()
}