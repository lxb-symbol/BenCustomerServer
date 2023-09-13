package com.ben.bencustomerserver.connnect

import com.ben.bencustomerserver.model.BaseMessageModel
import com.tencent.mmkv.MMKV


/***
 * 对接接收到的消息管理和处理,
 * 然后把处理过的数据存放到此处
 */
object RecieveMessageManager {

    open var msgListeners: WebSocketMessageListener? = null
    open var webSocketStatusListeners: WebSocketStatusListener? = null

    /**
     * 作为数据的缓存类
     */
    open val msgs: MutableList<BaseMessageModel> = mutableListOf()


    /**
     * 统一接受到消息之后进行处理和转存
     */
    fun parseMessageContent(jsonString: String) {

    }


}