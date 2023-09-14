package com.ben.bencustomerserver.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ben.bencustomerserver.connnect.RecieveMessageManager
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.NetMessageBean
import com.ben.bencustomerserver.model.NetMessageBeanOut
import com.ben.bencustomerserver.model.TokenAndWsEntity
import com.ben.bencustomerserver.model.UpFileEntity
import com.ben.bencustomerserver.repositories.ChatRepository
import com.ben.bencustomerserver.utils.MMkvTool
import com.symbol.lib_net.model.NetResult
import kotlinx.coroutines.launch
import java.io.File

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _messages = MutableLiveData<NetMessageBeanOut>()

    private val _tokenAndWs = MutableLiveData<TokenAndWsEntity>()

    private val _upImg = MutableLiveData<UpFileEntity>()
    private val _upFile = MutableLiveData<UpFileEntity>()

    /**
     * 最终网络和  socket转化后的数据
     */
    private val _finalMessages = MutableLiveData<List<BaseMessageModel>>()

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
    fun chatMessages(page: Int): MutableLiveData<NetMessageBeanOut> {
        val map = HashMap<String, String>()
        map["uid"] = "${MMkvTool.getUserId()}"
        map["page"] = "$page"
        map["t"] = "${System.currentTimeMillis()}"
        map["u"] = "${MMkvTool.getSellerCode()}"
        map["tk"]="${MMkvTool.getToken()}"

        viewModelScope.launch {
            when (val result = repository.getMessageList(map)) {
                is NetResult.Success -> {
                    _messages.postValue(result.data)
                    _messages.value?.data.let {
                        Log.e("symbol-3", " 历史消息条数： ${it?.size}")
                        it?.let { tmp ->
                            coverNetMessageToBaseViewModel(tmp)
                        }
                    }
                }

                is NetResult.Error -> {
                    Log.e("symbol", "getChatMessages is error: ${result.exception.message}")
                }
            }
        }
        return _messages

    }


    /***
     * 网络消息转换为 BaseViewModel 的形式
     */
    fun coverNetMessageToBaseViewModel(netList: List<NetMessageBean>) {

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
                    }

                }

                is NetResult.Error -> {
                    Log.e("symbol", "getTokenAndWs is error: ${result.exception.message}")
                }
            }
        }
    }


    fun sendMessage(msg:BaseMessageModel,isHume: Boolean){
        if (isHume){// 人工走 socket

        }else{//



        }
    }


    fun uploadImg(f: File) {
        viewModelScope.launch {
            val result = repository.uploadImg(f)
            when (result) {
                is NetResult.Success -> {
                    _upImg.postValue(result.data)
                }

                is NetResult.Error -> {
                    Log.e("symbol", "getTokenAndWs is error: ${result.exception.message}")

                }
            }
        }
    }

    fun uploadFile() {

    }


    fun getEmojis() {

    }

}