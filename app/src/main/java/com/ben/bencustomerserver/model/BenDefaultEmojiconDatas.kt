package com.ben.bencustomerserver.model

import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.utils.BenSmileUtils
import com.ben.bencustomerserver.views.BenEmojicon

val emojis = arrayOf(
    BenSmileUtils.ee_1,
    BenSmileUtils.ee_2,
    BenSmileUtils.ee_3,
    BenSmileUtils.ee_4,
    BenSmileUtils.ee_5,
    BenSmileUtils.ee_6,
    BenSmileUtils.ee_7,
    BenSmileUtils.ee_8,
    BenSmileUtils.ee_9,
    BenSmileUtils.ee_10,
    BenSmileUtils.ee_11,
    BenSmileUtils.ee_12,
    BenSmileUtils.ee_13,
    BenSmileUtils.ee_14,
    BenSmileUtils.ee_15,
    BenSmileUtils.ee_16,
    BenSmileUtils.ee_17,
    BenSmileUtils.ee_18,
    BenSmileUtils.ee_19,
    BenSmileUtils.ee_20,
    BenSmileUtils.ee_21,
    BenSmileUtils.ee_22,
    BenSmileUtils.ee_23,
    BenSmileUtils.ee_24,
    BenSmileUtils.ee_25,
    BenSmileUtils.ee_26,
    BenSmileUtils.ee_27,
    BenSmileUtils.ee_28,
    BenSmileUtils.ee_29,
    BenSmileUtils.ee_30,
    BenSmileUtils.ee_31,
    BenSmileUtils.ee_32,
    BenSmileUtils.ee_33,
    BenSmileUtils.ee_34,
    BenSmileUtils.ee_35,
    BenSmileUtils.ee_36,
    BenSmileUtils.ee_37,
    BenSmileUtils.ee_38,
    BenSmileUtils.ee_39,
    BenSmileUtils.ee_40,
    BenSmileUtils.ee_41,
    BenSmileUtils.ee_42,
    BenSmileUtils.ee_43,
    BenSmileUtils.ee_44,
    BenSmileUtils.ee_45,
    BenSmileUtils.ee_46,
    BenSmileUtils.ee_47
)
val icons = intArrayOf(
    R.drawable.ee_1,
    R.drawable.ee_2,
    R.drawable.ee_3,
    R.drawable.ee_4,
    R.drawable.ee_5,
    R.drawable.ee_6,
    R.drawable.ee_7,
    R.drawable.ee_8,
    R.drawable.ee_9,
    R.drawable.ee_10,
    R.drawable.ee_11,
    R.drawable.ee_12,
    R.drawable.ee_13,
    R.drawable.ee_14,
    R.drawable.ee_15,
    R.drawable.ee_16,
    R.drawable.ee_17,
    R.drawable.ee_18,
    R.drawable.ee_19,
    R.drawable.ee_20,
    R.drawable.ee_21,
    R.drawable.ee_22,
    R.drawable.ee_23,
    R.drawable.ee_24,
    R.drawable.ee_25,
    R.drawable.ee_26,
    R.drawable.ee_27,
    R.drawable.ee_28,
    R.drawable.ee_29,
    R.drawable.ee_30,
    R.drawable.ee_31,
    R.drawable.ee_32,
    R.drawable.ee_33,
    R.drawable.ee_34,
    R.drawable.ee_35,
    R.drawable.ee_36,
    R.drawable.ee_37,
    R.drawable.ee_38,
    R.drawable.ee_39,
    R.drawable.ee_40,
    R.drawable.ee_41,
    R.drawable.ee_42,
    R.drawable.ee_43,
    R.drawable.ee_44,
    R.drawable.ee_45,
    R.drawable.ee_46,
    R.drawable.ee_47
)

object BenDefaultEmojiconDatas {
    val data = createData()
    private fun createData(): Array<BenEmojicon> {
        val datas = mutableListOf<BenEmojicon>()
        for (i in icons.indices) {
            datas.add(i,BenEmojicon(icons[i], emojis[i], BenEmojicon.Type.NORMAL))
        }
        return datas.toTypedArray()
    }
}