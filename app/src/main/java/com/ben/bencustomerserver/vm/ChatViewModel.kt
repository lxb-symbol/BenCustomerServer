package com.ben.bencustomerserver.vm

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ben.bencustomerserver.connnect.RecieveMessageManager
import com.ben.bencustomerserver.connnect.WsManager
import com.ben.bencustomerserver.listener.INetCallback
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.Direct
import com.ben.bencustomerserver.model.FileMessage
import com.ben.bencustomerserver.model.ImageMessage
import com.ben.bencustomerserver.model.MessageStatus
import com.ben.bencustomerserver.model.MessageType
import com.ben.bencustomerserver.model.MessageUtil
import com.ben.bencustomerserver.model.NetMessageBean
import com.ben.bencustomerserver.model.OriginMessageType
import com.ben.bencustomerserver.model.TokenAndWsEntity
import com.ben.bencustomerserver.model.UpFileEntity
import com.ben.bencustomerserver.model.VideoMessage
import com.ben.bencustomerserver.model.VoiceMessage
import com.ben.bencustomerserver.repositories.ChatRepository
import com.ben.bencustomerserver.utils.MMkvTool
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.symbol.lib_net.exception.ResultException
import com.symbol.lib_net.model.NetResult
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.launch
import java.io.File


class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _messages = MutableLiveData<List<NetMessageBean>>()

    private val _tokenAndWs = MutableLiveData<TokenAndWsEntity>()

    private val _upImg = MutableLiveData<UpFileEntity>()
    private val _upFile = MutableLiveData<UpFileEntity>()

    // 上传错误之后，发送消息 ID
    private val _errorUp = MutableLiveData<String>()


    /**
     * 最终网络和  socket转化后的数据
     */
    private val _finalMessages = MutableLiveData<List<BaseMessageModel>>()

    private val _isHumanTalk = MutableLiveData<Boolean>()

    fun getErrorUpId() = _errorUp

    fun getHumanTak() = _isHumanTalk

    fun saveUserId(id: String) {
        MMkvTool.putUserId(id)
    }


    fun saveUserName(name: String) {
        MMkvTool.putUserName(name)
    }

    fun saveUserAvatar(avatar: String) {
        MMkvTool.putUserAvatar(avatar)
    }

    fun saveKFName(name: String) {
        MMkvTool.putKFName(name)
    }

    fun saveKFId(id: String) {
        MMkvTool.putKFId(id)
    }

    fun saveSellerCode(code: String) {
        MMkvTool.putSellerCode(code)
    }

    fun getFinalResultMessages() = _finalMessages
    fun getDataMessagesResult() = _messages

    fun getTokenAndWsResul() = _tokenAndWs

    fun getUpImgResult() = _upImg
    fun getUpFileResult() = _upFile


    /**
     *  * 用户uid
     *      * page：页数
     *      * tk：token
     *      * t：当前时间戳
     *      * u：商家code
     */
    fun chatMessages(page: Int): MutableLiveData<List<NetMessageBean>> {
        val map = HashMap<String, String>()
        map["uid"] = "${MMkvTool.getUserId()}"
        map["page"] = "$page"
        map["t"] = "${System.currentTimeMillis()}"
        map["u"] = "${MMkvTool.getSellerCode()}"
        map["tk"] = "${MMkvTool.getToken()}"

        viewModelScope.launch {
            when (val result = repository.getMessageList(map)) {
                is NetResult.Success -> {
                    _messages.postValue(result.data)
                    _messages.value?.let {
                        Log.e("symbol-3", " 历史消息条数： ${it.size}")
                        it.let { tmp ->
                            coverNetMessageToBaseViewModel(tmp)
                        }
                    }
                }

                is NetResult.Error -> {
                    Log.e("symbol", "getChatMessages is error: ${result.exception.message}")
                }

                else -> {

                }
            }
        }
        return _messages

    }


    /***
     * 网络消息转换为 BaseViewModel 的形式
     */
    private fun coverNetMessageToBaseViewModel(netList: List<NetMessageBean>) {

        if (netList.isEmpty()) return
        for (i in netList.indices) {
            val bean = netList[i]
            RecieveMessageManager.parseMessageFromNet(bean)
        }
        _finalMessages.postValue(RecieveMessageManager.msgs)
    }


    fun getTokenAndWs(code: String) {
        Log.e("symbol", "getTokenAndWs")
        viewModelScope.launch {
            when (val result = repository.getTokenAndWs(code)) {
                is NetResult.Success -> {
                    _tokenAndWs.postValue(result.data)
                    result.data.let {
                        MMkvTool.putTime(it.time)
                        MMkvTool.putToken(it.token)
                        MMkvTool.putWsURL(it.socket_url)
                        MMkvTool.putSellerId(it.seller_id)
                    }

                }

                is NetResult.Error -> {
                    Log.e("symbol", "getTokenAndWs is error: ${result.exception.message}")
                }
            }
        }
    }


    /**
     *  发送消息
     */
    fun sendMessage(msg: BaseMessageModel, callback: INetCallback<String>?) {
        val isHuman = MMkvTool.getIsHuman()
        msg.isBolt = !isHuman
        msg.direct = Direct.SEND
        when (msg.messageType) {

            MessageType.TXT -> {
                if (isHuman) {
                    WsManager.mWebSocket?.let {
                        val str = MessageUtil.generateWsMessageTxt(msg)
                        Log.i("symbol", "send ws TXT : $str")
                        it.send(str)
                        msg.status = MessageStatus.SUCCESS
                        RecieveMessageManager.msgs.add(msg)
                        callback?.let {
                            it.onSuccess("")
                        }
                    }
                } else {
                    msg.status = MessageStatus.SUCCESS
                    RecieveMessageManager.msgs.add(msg)
                    queryBolt(msg.content, object : INetCallback<String> {
                        override fun onSuccess(data: String) {
                            Log.i("symbol--4", "$data")
                            callback?.let {
                                it.onSuccess("")
                            }
                        }

                        override fun onError(code: Int, msg: String) {
                            Log.i("symbol--4", "$msg")
                            callback?.let {
                                it.onError(code, msg)
                            }

                            if (code == -3 || code == -1) {// 来自机器人的回复,取 msg 的值
                                RecieveMessageManager.addBoltResponseData(msg, MessageType.TXT, "")
                            }
                        }
                    })

                }
            }

            MessageType.CMD -> {
                val ext = msg.extString
                if (TextUtils.equals(ext, OriginMessageType.TYPE_CMD_SWITCH_HUMAN)) {
                    WsManager.mWebSocket?.let {
                        val str = MessageUtil.generateWsMessageSwitchHuman(msg)
                        it.send(str)
                    }
                }
            }

            MessageType.IMAGE -> {
                if (isHuman) {// 人工都是 socket
                    val innerMsg: ImageMessage = msg.innerMessage as ImageMessage
                    val localPath = innerMsg.localPath
                    uploadImg(msg.msgId, File(localPath), object : INetCallback<UpFileEntity> {
                        override fun onSuccess(data: UpFileEntity) {
                            (msg.innerMessage as ImageMessage).netPath = data.src

                            Log.e("symbol-4", "-->" + data.name)
                            Log.e("symbol-4", "-->" + data.src)
                            WsManager.mWebSocket?.let {
                                var str = MessageUtil.generateWsMessageImage(msg)
                                it.send(str)
                                msg.status = MessageStatus.SUCCESS
                                RecieveMessageManager.msgs.add(msg)
                                callback?.let {
                                    it.onSuccess("")
                                }
                            }
                        }

                        override fun onError(code: Int, msg1: String) {
                            RecieveMessageManager.msgs.add(msg)
                        }

                    })
                } else {
                    Log.e("symbol-4", "-->$msg")
                    val innerMsg: ImageMessage = msg.innerMessage as ImageMessage
                    val localPath = innerMsg.localPath
                    val f = File(localPath)
                    uploadImg(msg.msgId, f, object : INetCallback<UpFileEntity> {
                        override fun onSuccess(data: UpFileEntity) {
                            (msg.innerMessage as ImageMessage).netPath = data.src

                            Log.e("symbol-4", "-->" + data.name)
                            Log.e("symbol-4", "-->" + data.src)
                            // TODO 缺少
                            msg.status = MessageStatus.SUCCESS
                            RecieveMessageManager.msgs.add(msg)
                            callback?.let {
                                it.onSuccess("")
                            }
                        }

                        override fun onError(code: Int, errorMsg: String) {
                            msg.status = MessageStatus.FAIL
                            RecieveMessageManager.msgs.add(msg)

                        }

                    })

                }

            }

            MessageType.VOICE -> {
                if (isHuman) {// 人工都是 socket
                    val innerMsg: VoiceMessage = msg.innerMessage as VoiceMessage
                    val localPath = innerMsg.localPath
                    uploadFile(File(localPath), object : INetCallback<UpFileEntity> {
                        override fun onSuccess(data: UpFileEntity) {
                            Log.e("symbol-4", "-->" + data.name)
                            Log.e("symbol-4", "-->" + data.src)
                            (msg.innerMessage as VoiceMessage).netPath = data.src
                            msg.status = MessageStatus.SUCCESS

                            WsManager.mWebSocket?.let {
                                var str = MessageUtil.generateWsMessageVoice(msg)
                                Log.e("symbol: voice", str)
                                it.send(str)
                                RecieveMessageManager.msgs.add(msg)
                                callback?.let {
                                    it.onSuccess("")
                                }
                            }
                        }

                        override fun onError(code: Int, em: String) {
                            Log.e("symbol-4", "-->$em")
                            msg.status = MessageStatus.FAIL

                        }
                    })
                } else {
                    val innerMsg: VoiceMessage = msg.innerMessage as VoiceMessage
                    val localPath = innerMsg.localPath
                    uploadFile(File(localPath), object : INetCallback<UpFileEntity> {
                        override fun onSuccess(data: UpFileEntity) {
                            Log.e("symbol-4", "-->" + data.name)
                            Log.e("symbol-4", "-->" + data.src)
                            (msg.innerMessage as VoiceMessage).netPath = data.src
                            msg.status = MessageStatus.SUCCESS
                            //TODO 缺少接口
                            RecieveMessageManager.msgs.add(msg)
                            callback?.let {
                                it.onSuccess("")
                            }
                        }

                        override fun onError(code: Int, msg: String) {
                            Log.e("symbol-4", "-->$msg")
                        }

                    })
                }

            }

            MessageType.VIDEO -> {
                if (isHuman) {// 人工都是 socket
                    val innerMsg: VideoMessage = msg.innerMessage as VideoMessage
                    val localPath = innerMsg.localPath
                    uploadFile(File(localPath), object : INetCallback<UpFileEntity> {
                        override fun onSuccess(data: UpFileEntity) {
                            Log.e("symbol-4", "-->" + data.name)
                            Log.e("symbol-4", "-->" + data.src)
                            (msg.innerMessage as VideoMessage).netPath = data.src
                            msg.status = MessageStatus.SUCCESS

                            WsManager.mWebSocket?.let {
                                var str = MessageUtil.generateWsMessageVideo(msg)
                                it.send(str)
                                RecieveMessageManager.msgs.add(msg)
                                callback?.let {
                                    it.onSuccess("")
                                }
                            }
                        }

                        override fun onError(code: Int, msg: String) {
                            Log.e("symbol-4", "-->$msg")
                        }
                    })
                } else {
                    val innerMsg: VideoMessage = msg.innerMessage as VideoMessage
                    val localPath = innerMsg.localPath
                    uploadFile(File(localPath), object : INetCallback<UpFileEntity> {
                        override fun onSuccess(data: UpFileEntity) {
                            Log.e("symbol-4", "-->" + data.name)
                            Log.e("symbol-4", "-->" + data.src)
                            (msg.innerMessage as VideoMessage).netPath = data.src
                            msg.status = MessageStatus.SUCCESS

                            //TODO 缺少接口
                            RecieveMessageManager.msgs.add(msg)
                            callback?.let {
                                it.onSuccess("")
                            }
                        }

                        override fun onError(code: Int, msg: String) {
                            Log.e("symbol-4", "-->$msg")
                        }

                    })
                }
            }

            MessageType.FILE -> {
                if (isHuman) {// 人工都是 socket
                    val innerMsg: FileMessage = msg.innerMessage as FileMessage
                    val localPath = innerMsg.localPath
                    val f = File(localPath)
                    innerMsg.name =f.name
                    uploadFile(f, object : INetCallback<UpFileEntity> {
                        override fun onSuccess(data: UpFileEntity) {
                            Log.e("symbol-4", "-->" + data.name)
                            Log.e("symbol-4", "-->" + data.src)
                            (msg.innerMessage as FileMessage).netPath = data.src
                            msg.status = MessageStatus.SUCCESS

                            WsManager.mWebSocket?.let {
                                var str = MessageUtil.generateWsMessageFile(msg)
                                it.send(str)
                                RecieveMessageManager.msgs.add(msg)
                                callback?.let {
                                    it.onSuccess("")
                                }
                            }
                        }

                        override fun onError(code: Int, msg: String) {
                        }

                    })
                } else {
                    val innerMsg: FileMessage = msg.innerMessage as FileMessage
                    val localPath = innerMsg.localPath
                    uploadFile(File(localPath), object : INetCallback<UpFileEntity> {
                        override fun onSuccess(data: UpFileEntity) {
                            Log.e("symbol-4", "-->" + data.name)
                            Log.e("symbol-4", "-->" + data.src)
                            (msg.innerMessage as FileMessage).netPath = data.src
                            msg.status = MessageStatus.SUCCESS

                            RecieveMessageManager.msgs.add(msg)
                            callback?.let {
                                it.onSuccess("")
                            }
                        }

                        override fun onError(code: Int, emsg: String) {
                            msg.status = MessageStatus.SUCCESS
                            callback?.let {
                                it.onError(code, emsg)
                            }
                        }

                    })

                }

            }

            else -> {

            }
        }


    }


    private fun uploadImg(id: String, f: File, callback: INetCallback<UpFileEntity>?) {
        viewModelScope.launch {
            val result = repository.uploadImg(f)
            when (result) {
                is NetResult.Success -> {
                    _upImg.postValue(result.data)
                    callback?.let {
                        it.onSuccess(result.data)
                    }
                }

                is NetResult.Error -> {
                    Log.e("symbol", "getTokenAndWs is error: ${result.exception.message}")
                    callback?.let {
                        it.onError(-1, result.exception.message.toString())
                    }
                }
            }
        }
    }

    private fun uploadFile(f: File, callback: INetCallback<UpFileEntity>?) {
        viewModelScope.launch {
            val result = repository.uploadFile(f)
            when (result) {
                is NetResult.Success -> {
                    _upFile.postValue(result.data)
                    callback?.let {
                        it.onSuccess(result.data)
                    }
                }

                is NetResult.Error -> {
                    callback?.let {
                        it.onError(-1, result.exception.message.toString())
                    }
                }
            }
        }
    }


    fun getEmojis(back: INetCallback<String>?) {
        val code = MMkvTool.getSellerCode()
        viewModelScope.launch {
            when (val result = repository.getEmojis(code)) {
                is NetResult.Success -> {
//                    Log.e("symbol", "表情：${result.data}")
                    covertToJsonAndSave(result.data)
                }

                is NetResult.Error -> {
//                    Log.e("symbol", "表情：${result.exception.message}")
                }
            }
        }
    }

    private fun covertToJsonAndSave(source: List<String>) {
        val result = mutableListOf<String>()
        for (str in source) {
            result.add("https://$str")
        }
        val type = object : TypeToken<ArrayList<String>>() {}.type
        val json = GsonBuilder().create().toJson(result, type)
        MMkvTool.putEmojis(json)
    }


    /**
     * 问机器人
     */
    fun queryBolt(content: String, back: INetCallback<String>?) {
        val map = HashMap<String, String>()
        map["q"] = "" + content
        map["seller_id"] = "" + MMkvTool.getSellerId()
        map["from_id"] = "" + MMkvTool.getUserId()
        map["from_name"] = "" + MMkvTool.getUserName()
        map["from_avatar"] = "" + MMkvTool.getUserAvatar()
        map["seller_code"] = "" + MMkvTool.getSellerCode()

        viewModelScope.launch {
            val result = repository.queryBolt(map)
            when (result) {
                is NetResult.Success -> {
                    val str = result.data
                    back?.let {
                        it.onSuccess(str)
                    }
                }

                is NetResult.Error -> {
                    back?.let {
                        val code = (result.exception as ResultException).errCode ?: "-1"
                        it.onError(code.toInt(), result.exception.message.toString())
                    }
                }
            }
        }
    }

}