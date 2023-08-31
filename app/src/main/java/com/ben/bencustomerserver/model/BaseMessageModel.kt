package com.ben.bencustomerserver.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable


/**
 * 基础消息体
 */
data class BaseMessageModel(
    val id: Long,
    val messageType: Int
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readInt()
    )

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<BaseMessageModel> {
        override fun createFromParcel(parcel: Parcel): BaseMessageModel {
            return BaseMessageModel(parcel)
        }

        override fun newArray(size: Int): Array<BaseMessageModel?> {
            return arrayOfNulls(size)
        }
    }
}


