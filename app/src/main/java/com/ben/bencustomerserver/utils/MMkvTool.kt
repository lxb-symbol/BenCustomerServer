package com.ben.bencustomerserver.utils

import com.ben.bencustomerserver.model.Constants
import com.tencent.mmkv.MMKV

object MMkvTool {

    fun putToken(token: String) {
        MMKV.defaultMMKV().putString(Constants.KEY_TOKEN, token)
    }

    fun getToken() = MMKV.defaultMMKV().getString(Constants.KEY_TOKEN, "")


    fun putTime(time: String) {
        MMKV.defaultMMKV().putString(Constants.KEY_TIME, time)
    }

    fun getTime(): String? = MMKV.defaultMMKV().getString(Constants.KEY_TIME, "")


    fun putSellerId(id: String) = MMKV.defaultMMKV().putString(Constants.KEY_SELLER_ID, id)


    /**
     * TODO 测试用
     * MMKV.defaultMMKV().getString(Constants.KEY_SELLER_ID, "-1")
     */
    fun getSellerId() = MMKV.defaultMMKV().getString(Constants.KEY_SELLER_ID,"1")

    fun putSellerCode(code: String) {
        MMKV.defaultMMKV().putString(Constants.KEY_SELLER_CODE, code)
    }

    fun getSellerCode(): String? =
        MMKV.defaultMMKV().getString(Constants.KEY_SELLER_CODE, "")


    fun putKFId(id: String) {
        MMKV.defaultMMKV().putString(Constants.KEY_KF_ID, id)
    }

    fun getKFId(): String? =
        MMKV.defaultMMKV().getString(Constants.KEY_KF_ID, "")


    fun putKFName(name: String) {
        MMKV.defaultMMKV().putString(Constants.KEY_KF_NAME, name)
    }

    fun putKFAvatar(avatar: String) = MMKV.defaultMMKV().putString(Constants.KEY_KF_AVATAR, avatar)

    fun getKFAvatar() = MMKV.defaultMMKV().getString(Constants.KEY_KF_AVATAR,"")
    fun putKFCode(code: String) = MMKV.defaultMMKV().putString(Constants.KEY_KF_CODE, code)
    fun getKFName(): String? = MMKV.defaultMMKV().getString(Constants.KEY_KF_NAME, "")

    fun putUserId(id: String) {
        MMKV.defaultMMKV().putString(Constants.KEY_USER_ID, id)
    }

    fun getUserId(): String? = MMKV.defaultMMKV().getString(Constants.KEY_USER_ID, "")

    fun putUserName(name: String) {
        MMKV.defaultMMKV().putString(Constants.KEY_USER_NAME, name)
    }

    fun getUserName(): String? = MMKV.defaultMMKV().getString(Constants.KEY_USER_NAME, "")


    fun putUserAvatar(avatar: String) {
        MMKV.defaultMMKV().putString(Constants.KEY_USER_AVATAR, avatar)
    }

    fun getUserAvatar(): String? = MMKV.defaultMMKV().getString(Constants.KEY_USER_AVATAR, "")
    fun putWsURL(socketUrl: String) {
        MMKV.defaultMMKV().putString(Constants.KEY_WS_URL, socketUrl)
    }

    fun getIsHuman(): Boolean = MMKV.defaultMMKV().getBoolean(Constants.KEY_IS_HUMAN, false)

    fun putIsHuman(isHuman: Boolean) {
        MMKV.defaultMMKV().putBoolean(Constants.KEY_IS_HUMAN, isHuman)
    }

}