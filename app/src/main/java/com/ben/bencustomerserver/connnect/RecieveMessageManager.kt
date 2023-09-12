package com.ben.bencustomerserver.connnect

import com.ben.bencustomerserver.model.BaseMessageModel
import com.tencent.mmkv.MMKV


/***
 * 对接接收到的消息管理和处理,
 * 然后把处理过的数据存放到此处
 */
object RecieveMessageManager {

    /**
     * 作为数据的缓存类
     */
    open val msgs: MutableList<BaseMessageModel> = mutableListOf()


    fun parseMessageContent() {
    }


}