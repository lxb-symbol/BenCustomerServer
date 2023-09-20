package com.ben.bencustomerserver.connnect

import android.text.TextUtils
import android.util.Log
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.ChatSocketBean
import com.ben.bencustomerserver.model.Direct
import com.ben.bencustomerserver.model.FileMessage
import com.ben.bencustomerserver.model.ImageMessage
import com.ben.bencustomerserver.model.InUserInit
import com.ben.bencustomerserver.model.LocationMessage
import com.ben.bencustomerserver.model.MessageRegular
import com.ben.bencustomerserver.model.MessageStatus
import com.ben.bencustomerserver.model.MessageTemplateBean
import com.ben.bencustomerserver.model.MessageType
import com.ben.bencustomerserver.model.NetMessageBean
import com.ben.bencustomerserver.model.OriginMessageType
import com.ben.bencustomerserver.model.TextMessage
import com.ben.bencustomerserver.model.VideoMessage
import com.ben.bencustomerserver.model.VoiceMessage
import com.ben.bencustomerserver.utils.MMkvTool
import com.ben.bencustomerserver.vm.ChatViewModel
import com.google.gson.GsonBuilder
import com.symbol.lib_net.net.RetrofitClient
import org.json.JSONObject
import java.lang.ref.WeakReference


/***
 * 对接接收到的消息管理和处理,
 * 然后把处理过的数据存放到此处
 */
object RecieveMessageManager {

    var vm:WeakReference<ChatViewModel>?=null

    const val TAG = "symbol-RecieveMessageManager:"

    open var socketMsgListeners = mutableMapOf<String, WebSocketMessageListener>()
    open var webSocketStatusListeners = mutableMapOf<String, WebSocketStatusListener>()
    open var httpMsgListeners = mutableMapOf<String, HttpMessageListener>()

    /**
     * 作为数据的缓存类
     */
    open val msgs: MutableList<BaseMessageModel> = mutableListOf()


    /**
     * 统一接受到消息之后进行处理和转存，
     * 可能来自于网络，也可能来自于 socket
     */
    fun parseMessageContentFromSocket(jsonString: String) {

        val obj = JSONObject(jsonString)
        if (TextUtils.isEmpty(obj.optString("data"))) return
        val dataJson = obj.getString("data")
        val dataObj = JSONObject(dataJson)
        val code = dataObj.optInt("code")
        // code==0 表示成功处理 socket 消息
        if (code != 0) {
            when (obj.optString("cmd")) {
                OriginMessageType.TYPE_USER_INIT -> {
                    MMkvTool.putIsHuman(false)
                    vm.let {
                        it?.get()?.getHumanTak()?.postValue(false)
                    }

                }
            }
            return
        }

        when (obj.optString("cmd")) {
            OriginMessageType.TYPE_CUSTOMER_IN -> {// customerIn

            }

            OriginMessageType.TYPE_USER_INIT -> {
                try {
                    val initData = GsonBuilder().create().fromJson(dataJson, InUserInit::class.java)
                    MMkvTool.putKFId(initData.kefu_id ?: "")
                    MMkvTool.putKFName(initData.kefu_name ?: "")
                    MMkvTool.putKFCode(initData.kefu_code ?: "")
                    MMkvTool.putKFAvatar(initData.kefu_avatar ?: "")
                    MMkvTool.putSellerId(initData.seller_id ?: "")
                    MMkvTool.putIsHuman(true)
                    vm.let {
                        it?.get()?.getHumanTak()?.postValue(true)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            OriginMessageType.TYPE_MESSAGE_READ -> {// 已读消息

            }

            OriginMessageType.TYPE_IS_CLOSE -> {// 关闭
                MMkvTool.putIsHuman(false)
                vm.let {
                    it?.get()?.getHumanTak()?.postValue(false)
                }
            }

            OriginMessageType.TYPE_HELLO -> {// 欢迎语

            }

            OriginMessageType.TYPE_QUESTION -> {// 问答

            }


            OriginMessageType.TYPE_CHAT_MESSAGE -> {// 处理具体消息
                // 处理文本
                handleChatNormalSocketType(dataJson)
                // 处理图文视频等
                handleOtherSocketType(dataJson)

            }


        }


    }

    private fun handleChatNormalSocketType(dataJson: String) {
        var model: BaseMessageModel? = null
        if (TextUtils.isEmpty(dataJson)) {
            Log.i("symbol", "handleChatNormalSocketType datajson is null ")
            return
        }
        var bean: ChatSocketBean?
        try {
            bean = GsonBuilder().create().fromJson(dataJson, ChatSocketBean::class.java)
            if (bean == null) {
                Log.i("symbol", "handleChatNormalSocketType bean is null ")
                return
            }
            when (getMessageTypeByContentAndExt(bean.content)) {
                MessageType.TXT -> {
                    model = BaseMessageModel(
                        messageType = MessageType.TXT,
                        cmd = OriginMessageType.TYPE_CHAT_MESSAGE,
                        direct = Direct.RECEIEVE,
                        msgId = bean.chat_log_id,
                        status = MessageStatus.SUCCESS,
                        from_id = bean.id,
                        from_avatar = "${RetrofitClient.BASE_URL}/${bean.avatar}",
                        from_name = bean.name,
                        seller_code = "${MMkvTool.getSellerCode()}",
                        to_id = "${MMkvTool.getUserId()}",
                        to_name = "${MMkvTool.getUserName()}",
                        innerMessage = TextMessage(bean.content),
                    )
                    msgs.add(model)
                }

                else -> {
                    Log.i("symbol", "handleChatNormalSocketType bean.content: ${bean.content}")
                }
            }

        } catch (e: Exception) {
            Log.i("symbol", "handleChatNormalSocketType e :${e.message} ")
        }

        for (listener in socketMsgListeners.values) {
            model?.let {
                listener.onReceiveMessage(it)
            }
        }

    }

    private fun handleOtherSocketType(dataJson: String) {
        var model: BaseMessageModel? = null
        if (TextUtils.isEmpty(dataJson)) return
        var bean: MessageTemplateBean? = null
        try {
            bean = GsonBuilder().create()
                .fromJson(dataJson, MessageTemplateBean::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("symbol-2023->:", "${e.message}")
        }

        if (bean == null) {
            Log.i("symbol", "handleOtherSocketType  bean is null  ")
            return
        }


        if (TextUtils.isEmpty(bean.content)) {//添加一条消息
            model = BaseMessageModel(
                messageType = MessageType.TXT,
                cmd = OriginMessageType.TYPE_CHAT_MESSAGE,
                direct = Direct.RECEIEVE,
                msgId = System.currentTimeMillis().toString(),
                status = MessageStatus.SUCCESS,
                from_id = bean.from_id,
                from_avatar = bean.from_avatar,
                from_name = bean.from_name,
                seller_code = bean.seller_code,
                to_id = bean.to_id,
                to_name = bean.to_name,
                innerMessage = TextMessage(bean.content),
            )
            msgs.add(model)
            return
        }
        model = BaseMessageModel()
        when (getMessageTypeByContentAndExt(bean.content)) {
            MessageType.TXT -> {
                with(model) {
                    messageType = MessageType.TXT
                    content = bean.content
                    cmd = OriginMessageType.TYPE_CHAT_MESSAGE
                    direct = Direct.RECEIEVE
                    msgId = System.currentTimeMillis().toString()
                    status = MessageStatus.SUCCESS
                    from_id = bean.from_id
                    from_avatar = bean.from_avatar
                    from_name = bean.from_name
                    seller_code = bean.seller_code
                    to_id = bean.to_id
                    to_name = bean.to_name
                    innerMessage = TextMessage(bean.content)
                }
            }

            MessageType.IMAGE -> {
                val imgContent = bean.content
                val imgUrl = MessageRegular.matchImageMessageUrl(imgContent)
                with(model) {
                    messageType = MessageType.IMAGE
                    content = bean.content
                    cmd = OriginMessageType.TYPE_CHAT_MESSAGE
                    direct = Direct.RECEIEVE
                    msgId = System.currentTimeMillis().toString()
                    status = MessageStatus.CREATE
                    from_id = bean.from_id
                    from_avatar = bean.from_avatar
                    from_name = bean.from_name
                    seller_code = bean.seller_code
                    to_id = bean.to_id
                    to_name = bean.to_name
                    innerMessage = ImageMessage(
                        localPath = "",
                        netPath = imgUrl
                    )
                }
            }

            MessageType.LOCATION -> {

                val locationContent = bean.content
                val tmp = MessageRegular.matchLocationMessage(locationContent)
                val latStr = tmp?.let {
                    MessageRegular.getLocationMessageAttr(tmp, 0)
                }
                val lngStr = tmp?.let {
                    MessageRegular.getLocationMessageAttr(tmp, 1)
                }
                val addrName = tmp?.let {
                    MessageRegular.getLocationMessageAttr(tmp, 2)
                }
                with(model) {
                    messageType = MessageType.VOICE
                    content = bean.content
                    cmd = OriginMessageType.TYPE_CHAT_MESSAGE
                    direct = Direct.RECEIEVE
                    msgId = System.currentTimeMillis().toString()
                    status = MessageStatus.CREATE
                    from_id = bean.from_id
                    from_avatar = bean.from_avatar
                    from_name = bean.from_name
                    seller_code = bean.seller_code
                    to_id = bean.to_id
                    to_name = bean.to_name
                    innerMessage = LocationMessage(
                        name = addrName ?: "",
                        lat = (latStr ?: "0L").toDouble(),
                        lng = (lngStr ?: "0L").toDouble(),
                        buildingName = ""
                    )
                }

            }

            MessageType.VOICE -> {
                val voiceUrlAndDuration = bean.content
                val tmp = MessageRegular.matchVoiceMessageSome(voiceUrlAndDuration)
                val url = tmp?.let {
                    MessageRegular.getVoiceUrl(it)
                }
                val duration = tmp?.let { MessageRegular.getVoiceDuration(it) }
                with(model) {
                    messageType = MessageType.VOICE
                    content = bean.content
                    cmd = OriginMessageType.TYPE_CHAT_MESSAGE
                    direct = Direct.RECEIEVE
                    msgId = System.currentTimeMillis().toString()
                    status = MessageStatus.CREATE
                    from_id = bean.from_id
                    from_avatar = bean.from_avatar
                    from_name = bean.from_name
                    seller_code = bean.seller_code
                    to_id = bean.to_id
                    to_name = bean.to_name
                    innerMessage = VoiceMessage(
                        netPath = url,
                        duration = (duration ?: "0").toInt()
                    )
                }
            }

            MessageType.VIDEO -> {
                val videoContent = bean.content
                val videoUrl = MessageRegular.matchVideoUrl(videoContent)
                val videoName = MessageRegular.matchVideoName(videoContent)
                with(model) {
                    messageType = MessageType.VIDEO
                    content = bean.content
                    cmd = OriginMessageType.TYPE_CHAT_MESSAGE
                    direct = Direct.RECEIEVE
                    msgId = System.currentTimeMillis().toString()
                    status = MessageStatus.CREATE
                    from_id = bean.from_id
                    from_avatar = bean.from_avatar
                    from_name = bean.from_name
                    seller_code = bean.seller_code
                    to_id = bean.to_id
                    to_name = bean.to_name
                    innerMessage = VideoMessage(
                        netPath = "",
                        localPath = videoUrl,
                        name = videoName
                    )
                }

            }

            MessageType.CMD -> {
                var extStr = ""
                if (bean.content.contains(OriginMessageType.TAG_FACE)) {
                    extStr = OriginMessageType.EXT_MSG_EXPRESSION
                }
                with(model) {
                    messageType = MessageType.TXT
                    content = bean.content
                    cmd = OriginMessageType.TYPE_CHAT_MESSAGE
                    direct = Direct.RECEIEVE
                    msgId = System.currentTimeMillis().toString()
                    status = MessageStatus.SUCCESS
                    from_id = bean.from_id
                    from_avatar = bean.from_avatar
                    from_name = bean.from_name
                    seller_code = bean.seller_code
                    to_id = bean.to_id
                    to_name = bean.to_name
                    innerMessage = TextMessage(bean.content)
                    extString = extStr
                }


            }

            MessageType.FILE -> {

                val fileContent = bean.content
                val fileUrl = MessageRegular.matchFileUrl(fileContent)
                val name = MessageRegular.matchFileName(fileContent)

                with(model) {
                    messageType = MessageType.FILE
                    content = bean.content
                    cmd = OriginMessageType.TYPE_CHAT_MESSAGE
                    direct = Direct.RECEIEVE
                    msgId = System.currentTimeMillis().toString()
                    status = MessageStatus.CREATE
                    from_id = bean.from_id
                    from_avatar = bean.from_avatar
                    from_name = bean.from_name
                    seller_code = bean.seller_code
                    to_id = bean.to_id
                    to_name = bean.to_name
                    innerMessage = FileMessage(
                        name = name,
                        netPath = fileUrl ?: "",
                        localPath = "",
                        fileSize = 0
                    )
                }
            }

            else -> {
                Log.e(TAG, "" + bean)
            }
        }


        if (!TextUtils.isEmpty(model.content)
            && !TextUtils.isEmpty(model.from_id)
        ) {
            msgs.add(model)
        }

        for (listener in socketMsgListeners.values) {
            model.let {
                listener.onReceiveMessage(it)
            }
        }

    }


    /***
     * 获取消息类型根据内容
     */
    fun getMessageTypeByContentAndExt(content: String): MessageType {

        if (content.startsWith(OriginMessageType.TAG_FACE)) {
            return MessageType.CMD
        } else if (content.startsWith(OriginMessageType.TAG_IMG)) {
            return MessageType.IMAGE
        } else if (content.startsWith(OriginMessageType.TAG_FACE)) {
            return MessageType.FILE
        } else if (content.startsWith(OriginMessageType.TAG_VIDEO)) {
            return MessageType.VIDEO
        } else if (content.startsWith(OriginMessageType.TAG_VOICE)) {
            return MessageType.VOICE
        } else if (content.startsWith(OriginMessageType.TAG_LOCATION)) {
            return MessageType.LOCATION
        } else if (content.startsWith(OriginMessageType.TAG_FILE)) {
            return MessageType.FILE
        }
        return MessageType.TXT
    }


    /**
     * 接收网络请求的消息
     */
    fun parseMessageFromNet(bean: NetMessageBean) {
        val contentmsg = bean.content
        val model = BaseMessageModel()
        if (TextUtils.isEmpty(bean.content)) {//添加一条消息
            val model = BaseMessageModel(
                messageType = MessageType.TXT,
                cmd = OriginMessageType.TYPE_CHAT_MESSAGE,
                direct = Direct.RECEIEVE,
                msgId = bean.log_id.toString(),
                status = MessageStatus.SUCCESS,
                from_id = bean.from_id,
                from_avatar = bean.from_avatar,
                from_name = bean.from_name,
                seller_code = bean.seller_code,
                to_id = bean.to_id,
                to_name = bean.to_name,
                innerMessage = TextMessage(bean.content)
            )
            msgs.add(model)
            return
        }
        when (getMessageTypeByContentAndExt(contentmsg)) {
            MessageType.TXT -> {
                with(model) {
                    messageType = MessageType.TXT
                    content = bean.content
                    cmd = OriginMessageType.TYPE_CHAT_MESSAGE
                    direct = Direct.RECEIEVE
                    msgId = bean.log_id.toString()
                    status = MessageStatus.SUCCESS
                    from_id = bean.from_id
                    from_avatar = bean.from_avatar
                    from_name = bean.from_name
                    seller_code = bean.seller_code
                    to_id = bean.to_id
                    to_name = bean.to_name
                    innerMessage = TextMessage(bean.content)
                }

            }

            MessageType.IMAGE -> {
                val imgContent = bean.content
                val imgUrl = MessageRegular.matchImageMessageUrl(imgContent)
                with(model) {
                    messageType = MessageType.IMAGE
                    content = bean.content
                    cmd = OriginMessageType.TYPE_CHAT_MESSAGE
                    direct = Direct.RECEIEVE
                    msgId = bean.log_id.toString()
                    status = MessageStatus.CREATE
                    from_id = bean.from_id
                    from_avatar = bean.from_avatar
                    from_name = bean.from_name
                    seller_code = bean.seller_code
                    to_id = bean.to_id
                    to_name = bean.to_name
                    innerMessage = ImageMessage(
                        localPath = "",
                        netPath = imgUrl
                    )
                }
            }

            MessageType.LOCATION -> {

                val locationContent = bean.content
                val tmp = MessageRegular.matchLocationMessage(locationContent)
                val latStr = tmp?.let {
                    MessageRegular.getLocationMessageAttr(tmp, 0)
                }
                val lngStr = tmp?.let {
                    MessageRegular.getLocationMessageAttr(tmp, 1)
                }
                val addrName = tmp?.let {
                    MessageRegular.getLocationMessageAttr(tmp, 2)
                }
                with(model) {
                    messageType = MessageType.VOICE
                    content = bean.content
                    cmd = OriginMessageType.TYPE_CHAT_MESSAGE
                    direct = Direct.RECEIEVE
                    msgId = bean.log_id.toString()
                    status = MessageStatus.CREATE
                    from_id = bean.from_id
                    from_avatar = bean.from_avatar
                    from_name = bean.from_name
                    seller_code = bean.seller_code
                    to_id = bean.to_id
                    to_name = bean.to_name
                    innerMessage = LocationMessage(
                        name = addrName ?: "",
                        lat = (latStr ?: "0L").toDouble(),
                        lng = (lngStr ?: "0L").toDouble(),
                        buildingName = ""
                    )
                }

            }

            MessageType.VOICE -> {
                val voiceUrlAndDuration = bean.content
                val tmp = MessageRegular.matchVoiceMessageSome(voiceUrlAndDuration)
                val url = tmp?.let {
                    MessageRegular.getVoiceUrl(it)
                }
                val duration = tmp?.let { MessageRegular.getVoiceDuration(it) }
                with(model) {
                    messageType = MessageType.VOICE
                    content = bean.content
                    cmd = OriginMessageType.TYPE_CHAT_MESSAGE
                    direct = Direct.RECEIEVE
                    msgId = bean.log_id.toString()
                    status = MessageStatus.CREATE
                    from_id = bean.from_id
                    from_avatar = bean.from_avatar
                    from_name = bean.from_name
                    seller_code = bean.seller_code
                    to_id = bean.to_id
                    to_name = bean.to_name
                    innerMessage = VoiceMessage(
                        netPath = url,
                        duration = (duration ?: "0").toInt()
                    )
                }
            }

            MessageType.VIDEO -> {
                val videoContent = bean.content
                val videoUrl = MessageRegular.matchVideoUrl(videoContent)
                val videoName = MessageRegular.matchVideoName(videoContent)
                with(model) {
                    messageType = MessageType.VIDEO
                    content = bean.content
                    cmd = OriginMessageType.TYPE_CHAT_MESSAGE
                    direct = Direct.RECEIEVE
                    msgId = bean.log_id.toString()
                    status = MessageStatus.CREATE
                    from_id = bean.from_id
                    from_avatar = bean.from_avatar
                    from_name = bean.from_name
                    seller_code = bean.seller_code
                    to_id = bean.to_id
                    to_name = bean.to_name
                    innerMessage = VideoMessage(
                        netPath = "",
                        localPath = videoUrl,
                        name = videoName
                    )
                }

            }

            MessageType.CMD -> {

                val model = BaseMessageModel(
                    messageType = MessageType.CMD,
                    cmd = OriginMessageType.TYPE_CHAT_MESSAGE,
                    direct = Direct.RECEIEVE,
                    msgId = bean.log_id.toString(),
                    status = MessageStatus.SUCCESS,
                    from_id = bean.from_id,
                    from_avatar = bean.from_avatar,
                    from_name = bean.from_name,
                    seller_code = bean.seller_code,
                    to_id = bean.to_id,
                    to_name = bean.to_name,
                    innerMessage = TextMessage(bean.content)
                )
                msgs.add(model)


            }

            MessageType.FILE -> {

                val fileContent = bean.content
                val fileUrl = MessageRegular.matchFileUrl(fileContent)
                val name = MessageRegular.matchFileName(fileContent)

                with(model) {
                    msgId = bean.log_id.toString()
                    messageType = MessageType.FILE
                    content = bean.content
                    cmd = OriginMessageType.TYPE_CHAT_MESSAGE
                    direct = Direct.RECEIEVE
                    msgId = bean.log_id.toString()
                    status = MessageStatus.CREATE
                    from_id = bean.from_id
                    from_avatar = bean.from_avatar
                    from_name = bean.from_name
                    seller_code = bean.seller_code
                    to_id = bean.to_id
                    to_name = bean.to_name
                    innerMessage = FileMessage(
                        name = name,
                        netPath = fileUrl ?: "",
                        localPath = "",
                        fileSize = 0
                    )
                }

            }

            else -> {
                Log.e(TAG, "" + bean)
            }
        }

        if (!TextUtils.isEmpty(model.content) && !TextUtils.isEmpty(model.from_id)) {
            model.direct = Direct.RECEIEVE
            msgs.add(model)
        }
    }


    /**
     * 机器人回复的消息
     */
    fun addBoltResponseData(content: String, type: MessageType, originType: String) {

        var model: BaseMessageModel? = null
        when (type) {
            MessageType.TXT -> {
                model = BaseMessageModel(
                    messageType = type,
                    content = content,
                    extString = originType,
                    from_id = "",
                    from_avatar = "",
                    from_name = "",
                    isBolt = true,
                    to_id = MMkvTool.getUserId() ?: "",
                    direct = Direct.RECEIEVE,
                    innerMessage = TextMessage(content = content)
                )
                msgs.add(model)
            }

            else -> {
                Log.i("symbol-4", "addBoltResponse: $content")
            }
        }

        for (listener in httpMsgListeners.values) {
            model?.let {
                listener.receiveBoltMessage(it)
            }
        }

    }


    /**
     * 更改消息的状态
     *
     */
    fun modifyMessageStatus(id: String, msgContent: String) {


    }


    fun addHttpMessageListener(key: String, lis: HttpMessageListener) {
        if (httpMsgListeners.containsKey(key)) return
        httpMsgListeners[key] = lis
    }

    fun addSocketMessageListener(key: String, lis: WebSocketMessageListener) {
        if (socketMsgListeners.containsKey(key)) return
        socketMsgListeners[key] = lis
    }


}
