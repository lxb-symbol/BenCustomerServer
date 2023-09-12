package com.ben.bencustomerserver.utils

import com.ben.bencustomerserver.model.Constants
import com.tencent.mmkv.MMKV

object MmkvTool {

    fun putSellerCode(code: String) {
        MMKV.defaultMMKV().putString(Constants.KEY_SELLER_CODE, code)
    }

    fun getSellerCode(key: String): String? =
        MMKV.defaultMMKV().getString(Constants.KEY_SELLER_CODE, "")

}