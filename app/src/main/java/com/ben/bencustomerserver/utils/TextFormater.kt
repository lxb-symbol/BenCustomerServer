package com.ben.bencustomerserver.utils

import android.content.Context
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import java.util.Locale

object TextFormater {
    fun getDataSize(bytes: Long): String {
        val format = DecimalFormat("###.00")
        return if (bytes < 0) {
            "error"
        } else if (bytes < 1024) {
            bytes.toString() + "bytes"
        } else if (bytes < 1024 * 1024) {
            format.format((bytes / 1024f).toDouble()) + "KB"
        } else if (bytes < 1024 * 1024 * 1024) {
            format.format((bytes / 1024f / 1024f).toDouble()) + "MB"
        } else {
            format.format((bytes / 1024f / 1024f / 1024f).toDouble()) + "GB"
        }
    }

    /**
     *
     * @param kb
     * @return
     */
    fun getKBDataSize(kb: Long): String {
        val format = DecimalFormat("###.00")
        return if (kb < 1024) {
            kb.toString() + "KB"
        } else if (kb < 1024 * 1024) {
            format.format((kb / 1024f).toDouble()) + "MB"
        } else if (kb < 1024 * 1024 * 1024) {
            format.format((kb / 1024f / 1024f).toDouble()) + "GB"
        } else {
            "error"
        }
    }

    fun formatStr(context: Context, resId: Int, str: String?): String {
        val res = context.getText(resId).toString()
        return String.format(res, str)
    }

    private const val GB_SP_DIFF = 160
    private val secPosvalueList = intArrayOf(
        1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787,
        3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027, 4086,
        4390, 4558, 4684, 4925, 5249, 5600
    )
    private val firstLetter = charArrayOf(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j',
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
        't', 'w', 'x', 'y', 'z'
    )


}