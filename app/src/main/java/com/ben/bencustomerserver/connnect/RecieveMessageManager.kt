package com.ben.bencustomerserver.connnect

import android.text.TextUtils
import android.util.Log
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.Direct
import com.ben.bencustomerserver.model.FileMessage
import com.ben.bencustomerserver.model.ImageMessage
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
import com.google.gson.GsonBuilder
import org.json.JSONObject


/***
 * 对接接收到的消息管理和处理,
 * 然后把处理过的数据存放到此处
 */
object RecieveMessageManager {

    const val TAG = "symbol-RecieveMessageManager:"

    open var msgListeners: WebSocketMessageListener? = null
    open var webSocketStatusListeners: WebSocketStatusListener? = null

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
        val dataJson = obj.getString("data")
        when (obj.optString("cmd")) {

            OriginMessageType.TYPE_CUSTOMER_IN -> {// customerIn

            }

            OriginMessageType.TYPE_USER_INIT -> {

            }

            OriginMessageType.TYPE_MESSAGE_READ -> {// 已读消息

            }

            OriginMessageType.TYPE_IS_CLOSE -> {// 关闭

            }

            OriginMessageType.TYPE_HELLO -> {// 欢迎语

            }

            OriginMessageType.TYPE_QUESTION -> {// 问答

            }

            OriginMessageType.TYPE_CHAT_MESSAGE -> {// 处理具体消息
                if (TextUtils.isEmpty(dataJson)) return
                val bean = GsonBuilder().create()
                    .fromJson<MessageTemplateBean>(dataJson, MessageTemplateBean::class.java)
                if (TextUtils.isEmpty(bean.content)) {//添加一条消息
                    val model = BaseMessageModel(
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
                        innerMessage = TextMessage(bean.content)
                    )
                    msgs.add(model)
                    return
                }
                val model = BaseMessageModel()
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
                                lat = (latStr ?: "0L").toLong(),
                                lng = (lngStr ?: "0L").toLong()
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
                if (!TextUtils.isEmpty(model.content) && !TextUtils.isEmpty(model.from_id)) {
                    msgs.add(model)
                }

            }
        }

    }


    /***
     * 获取消息类型根据内容
     */
    fun getMessageTypeByContentAndExt(content: String): MessageType {

        if (content.startsWith(OriginMessageType.TAG_FACE)) {
            return MessageType.TXT
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
        return MessageType.CMD
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
                        lat = (latStr ?: "0L").toLong(),
                        lng = (lngStr ?: "0L").toLong()
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
            msgs.add(model)
        }
    }


}