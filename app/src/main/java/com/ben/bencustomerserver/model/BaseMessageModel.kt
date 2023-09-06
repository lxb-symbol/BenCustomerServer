package com.ben.bencustomerserver.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable


/**
 * 基础消息体,也算是处理过的消息体
 */
data class BaseMessageModel(
    val id: Long,
    val messageType: Int,
    val cmd:String
) : Serializable


