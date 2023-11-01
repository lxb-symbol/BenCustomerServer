package com.ben.bencustomerserver.model

import android.net.Uri
import android.text.TextUtils
import com.ben.bencustomerserver.utils.MMkvTool
import com.google.gson.GsonBuilder

//{
//    customer_id: 用户uid,
//    customer_name: 用户名称,
//    customer_avatar: 用户头像,
//    seller_code: 商家code,
//    tk: 1接口返回的token,
//    t: 当前时间戳
//}

data class UserInit(
    val cmd: String = OriginMessageType.TYPE_USER_INIT,
    val data: InUserInit
)


data class UserInitReal(
    val code: Int,
    val data: InUserInitReal,
    val msg: String
)

data class InUserInitReal(
    val kefu_avatar: String,
    val kefu_code: String,
    val kefu_name: String
)




data class InUserInit(
    val uid: String? = MMkvTool.getUserId(),
    val name: String? = MMkvTool.getUserName(),
    val avatar: String? = MMkvTool.getUserAvatar(),
    val seller: String? = MMkvTool.getSellerCode(),
    val tk: String? = MMkvTool.getToken(),
    val t: String? = "${System.currentTimeMillis()}",
// 下面是接收端字段
    val kefu_id: String? = "",
    val kefu_code: String? = "",
    val kefu_name: String? = "",
    val kefu_avatar: String? = "",
    val max_service_num: Int = 0,
    val seller_id: String? = ""
)

data class DirectLink(
    val cmd: String = OriginMessageType.TYPE_DIRECT_LINK_SERVER,
    val data: InDirectLink
)

data class InDirectLink(
    val uid: String? = MMkvTool.getUserId(),
    val name: String? = MMkvTool.getUserName(),
    val avatar: String? = MMkvTool.getUserAvatar(),
    val seller: String? = MMkvTool.getSellerCode(),
    val tk: String? = MMkvTool.getToken(),
    val t: String? = "${System.currentTimeMillis()}",
    val type: Int = 2,
    val kefu_code: String? = ""
)

data class CustomerInData(
    val customer_id: String,
    val customer_name: String,
    val customer_avatar: String,
    val seller_code: String,
    val tk: String,
    val t: String
)

data class CustomerInMessage(
    val cmd: String,
    val data: CustomerInData
)

object MessageUtil {

    fun createTestMsg(): CustomerInMessage {
        return CustomerInMessage(
            "customerIn", CustomerInData(
                MMkvTool.getUserId() ?: "",
                MMkvTool.getUserName() ?: "",
                MMkvTool.getUserAvatar() ?: "",
                MMkvTool.getSellerCode() ?: "",
                MMkvTool.getToken() ?: "",
                System.currentTimeMillis().toString()
            )
        )
    }


    /**
     * 视频 model
     */
    fun generateVideoModel(videoUri: Uri?, videoLength: Int, thumbPath: String): BaseMessageModel {
        return BaseMessageModel(
            messageType = MessageType.VIDEO,
            from_avatar = MMkvTool.getUserAvatar() ?: "",
            from_id = MMkvTool.getUserId() ?: "",
            from_name = MMkvTool.getUserName() ?: "",
            to_id = MMkvTool.getKFId() ?: "",
            to_name = MMkvTool.getKFName() ?: "",
            innerMessage = VideoMessage(
                localPath = videoUri?.path,
                localCover = thumbPath,
                length = videoLength
            )
        )
    }

    fun generateLocationModel(
        latitude: Double, longitude: Double, locationAddress: String?,
        buildingName: String?
    ): BaseMessageModel {
        return BaseMessageModel(
            messageType = MessageType.LOCATION,
            from_avatar = MMkvTool.getUserAvatar() ?: "",
            from_id = MMkvTool.getUserId() ?: "",
            from_name = MMkvTool.getUserName() ?: "",
            to_id = MMkvTool.getKFId() ?: "",
            to_name = MMkvTool.getKFName() ?: "",
            innerMessage = LocationMessage(
                lat = latitude,
                lng = longitude,
                name = locationAddress ?: "",
                buildingName = buildingName ?: ""
            )
        )
    }

    fun generateImgModel(imageUri: Uri?, sendOriginalImage: Boolean): BaseMessageModel {
        return BaseMessageModel(
            messageType = MessageType.IMAGE,
            from_avatar = MMkvTool.getUserAvatar() ?: "",
            from_id = MMkvTool.getUserId() ?: "",
            from_name = MMkvTool.getUserName() ?: "",
            to_id = MMkvTool.getKFId() ?: "",
            to_name = MMkvTool.getKFName() ?: "",
            innerMessage = ImageMessage(
                localPath = imageUri?.path
            )
        )
    }

    fun generateVoiceModel(filePath: Uri?, length: Int): BaseMessageModel {
        return BaseMessageModel(
            messageType = MessageType.VOICE,
            from_avatar = MMkvTool.getUserAvatar() ?: "",
            from_id = MMkvTool.getUserId() ?: "",
            from_name = MMkvTool.getUserName() ?: "",
            to_id = MMkvTool.getKFId() ?: "",
            to_name = MMkvTool.getKFName() ?: "",
            innerMessage = VoiceMessage(
                localPath = filePath?.path,
                duration = length
            )
        )
    }


    /**
     * 文件 model
     */
    fun generateFileModel(fileUri: Uri?): BaseMessageModel {
        // TODO
        return BaseMessageModel(
            messageType = MessageType.FILE,
            from_avatar = MMkvTool.getUserAvatar() ?: "",
            from_id = MMkvTool.getUserId() ?: "",
            from_name = MMkvTool.getUserName() ?: "",
            to_id = MMkvTool.getKFId() ?: "",
            to_name = MMkvTool.getKFName() ?: "",
            innerMessage = FileMessage(
                localPath = fileUri?.path
            )
        )
    }

    /**
     *
     * 构造成 CMD && C
     * 切换到人工服务发送 socket
     */
    fun generateHumeSwitchModel(): BaseMessageModel {
        return BaseMessageModel(
            messageType = MessageType.CMD,
            extString = OriginMessageType.TYPE_CMD_SWITCH_HUMAN,
            from_avatar = MMkvTool.getUserAvatar() ?: "",
            from_id = MMkvTool.getUserId() ?: "",
            from_name = MMkvTool.getUserName() ?: "",
            to_id = MMkvTool.getKFId() ?: "",
            to_name = MMkvTool.getKFName() ?: ""
        )
    }

    fun generateTextModel(content: String): BaseMessageModel {
        var msgType: MessageType = MessageType.TXT

        return BaseMessageModel(
            content =content,
            messageType = msgType,
            from_avatar = MMkvTool.getUserAvatar() ?: "",
            from_id = MMkvTool.getUserId() ?: "",
            from_name = MMkvTool.getUserName() ?: "",
            to_id = MMkvTool.getKFId() ?: "",
            to_name = MMkvTool.getKFName() ?: "",
            innerMessage =  TextMessage(content = content)
        )
    }


    /***
     * 生成 socket 消息
     * 发送
     * 如果不存在客服 ID 发送 userInit
     * 存在客服 ID 发送 direckLink
     */
    fun generateWsMessageSwitchHuman(msg: BaseMessageModel): String {
        var result: String
        val kefuId = MMkvTool.getKFId()
        if (TextUtils.isEmpty(kefuId)) {
            val userInit = UserInit(data = InUserInit())
            result = GsonBuilder().create().toJson(userInit)
        } else {
            val directLink = DirectLink(data = InDirectLink())
            result = GsonBuilder().create().toJson(directLink)
        }
        return result
    }


    /**
     * 生成图片消息
     */
    fun generateWsMessageImage(msg: BaseMessageModel): String {
        val inMsg: ImageMessage = msg.innerMessage as ImageMessage
        val content = "img[${inMsg.netPath}]"
        val img = MessageTemplateBean(
            content = content,
            from_id = MMkvTool.getUserId() ?: "",
            from_name = MMkvTool.getUserName() ?: "",
            from_avatar = MMkvTool.getUserAvatar() ?: "",
            to_id = MMkvTool.getKFId() ?: "",
            to_name = MMkvTool.getKFName() ?: "",
            seller_code = MMkvTool.getSellerCode() ?: ""
        )
        val out = WsMessageSendOut(data = img)
        return GsonBuilder().create().toJson(out)
    }


    /**
     * 生成视频消息
     */
    fun generateWsMessageVideo(msg: BaseMessageModel): String {
        val inMsg: VideoMessage = msg.innerMessage as VideoMessage
        val content = "video(${inMsg.netPath})[${inMsg.name}]"
        val img = MessageTemplateBean(
            content = content,
            from_id = MMkvTool.getUserId() ?: "",
            from_name = MMkvTool.getUserName() ?: "",
            from_avatar = MMkvTool.getUserAvatar() ?: "",
            to_id = MMkvTool.getKFId() ?: "",
            to_name = MMkvTool.getKFName() ?: "",
            seller_code = MMkvTool.getSellerCode() ?: ""
        )

        val out = WsMessageSendOut(data = img)
        return GsonBuilder().create().toJson(out)
    }

    /**
     * 生成语音消息
     */
    fun generateWsMessageVoice(msg: BaseMessageModel): String {
        val inMsg: VoiceMessage = msg.innerMessage as VoiceMessage
        val content = "audio[${inMsg.netPath},${inMsg.duration}]"
        val img = MessageTemplateBean(
            content = content,
            from_id = MMkvTool.getUserId() ?: "",
            from_name = MMkvTool.getUserName() ?: "",
            from_avatar = MMkvTool.getUserAvatar() ?: "",
            to_id = MMkvTool.getKFId() ?: "",
            to_name = MMkvTool.getKFName() ?: "",
            seller_code = MMkvTool.getSellerCode() ?: ""
        )
        val out = WsMessageSendOut(data = img)

        return GsonBuilder().create().toJson(out)
    }

    /**
     * 生成文件消息
     */
    fun generateWsMessageFile(msg: BaseMessageModel): String {
        val inMsg: FileMessage = msg.innerMessage as FileMessage
        val content = "file(${inMsg.netPath})[${inMsg.name}]"
        val img = MessageTemplateBean(
            content = content,
            from_id = MMkvTool.getUserId() ?: "",
            from_name = MMkvTool.getUserName() ?: "",
            from_avatar = MMkvTool.getUserAvatar() ?: "",
            to_id = MMkvTool.getKFId() ?: "",
            to_name = MMkvTool.getKFName() ?: "",
            seller_code = MMkvTool.getSellerCode() ?: ""
        )
        val out = WsMessageSendOut(data = img)
        return GsonBuilder().create().toJson(out)
    }


    /**
     * 生成定位消息
     */
    fun generateWsMessageLocation(msg: BaseMessageModel): String {
        val inMsg: LocationMessage = msg.innerMessage as LocationMessage
        val content = "location[${inMsg.lat},${inMsg.lng},${inMsg.name}]"
        val img = MessageTemplateBean(
            content = content,
            from_id = MMkvTool.getUserId() ?: "",
            from_name = MMkvTool.getUserName() ?: "",
            from_avatar = MMkvTool.getUserAvatar() ?: "",
            to_id = MMkvTool.getKFId() ?: "",
            to_name = MMkvTool.getKFName() ?: "",
            seller_code = MMkvTool.getSellerCode() ?: ""
        )
        val out = WsMessageSendOut(data = img)

        return GsonBuilder().create().toJson(out)
    }

    /**
     * 文本消息
     */
    fun generateWsMessageTxt(msg: BaseMessageModel): String {
        val inMsg: TextMessage = msg.innerMessage as TextMessage
        val content = inMsg.content
        val img = MessageTemplateBean(
            content = content,
            from_id = MMkvTool.getUserId() ?: "",
            from_name = MMkvTool.getUserName() ?: "",
            from_avatar = MMkvTool.getUserAvatar() ?: "",
            to_id = MMkvTool.getKFId() ?: "",
            to_name = MMkvTool.getKFName() ?: "",
            seller_code = MMkvTool.getSellerCode() ?: ""
        )
        val out = WsMessageSendOut(data = img)
        return GsonBuilder().create().toJson(out)

    }

    fun generatePing():String{
        val ping =PingMessage()
        return GsonBuilder().create().toJson(ping)
    }


}