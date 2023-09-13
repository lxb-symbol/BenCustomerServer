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

    fun getKFName(): String? = MMKV.defaultMMKV().getString(Constants.KEY_KF_NAME, "")

    fun putUserId(id: String) {
        MMKV.defaultMMKV().putString(Constants.KEY_USER_ID, id)
    }

    fun getUserId(): String? = MMKV.defaultMMKV().getString(Constants.KEY_USER_ID, "")

    fun putUserName(name: String) {
        MMKV.defaultMMKV().putString(Constants.KEY_USER_NAME, "")
    }

    fun getUserName(): String? = MMKV.defaultMMKV().getString(Constants.KEY_USER_NAME, "")


    fun putUserAvatar(avatar: String) {
        MMKV.defaultMMKV().putString(Constants.KEY_USER_AVATAR, avatar)
    }

    fun getUserAvatar(): String? = MMKV.defaultMMKV().getString(Constants.KEY_USER_AVATAR, "")
    fun putWsURL(socketUrl: String) {
        MMKV.defaultMMKV().putString(Constants.KEY_WS_URL, socketUrl)
    }

}