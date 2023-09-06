package com.ben.bencustomerserver.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.TokenAndWsEntity
import com.ben.bencustomerserver.model.UpFileEntity
import com.ben.bencustomerserver.repositories.ChatRepository
import com.symbol.lib_net.model.NetResult
import kotlinx.coroutines.launch
import java.io.File

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _messages = MutableLiveData<List<BaseMessageModel>>()

    private val _tokenAndWs = MutableLiveData<TokenAndWsEntity>()

    private val _upImg =MutableLiveData<UpFileEntity>()
    private val _upFile =MutableLiveData<UpFileEntity>()

    fun getDataMessagesResult() = _messages

    fun getTokenAndWsResul() = _tokenAndWs

    fun getUpImgResult()= _upImg
    fun getUpFileResult()= _upFile


    fun chatMessages(): MutableLiveData<List<BaseMessageModel>> {
        viewModelScope.launch {
            when (val result = repository.getMessageList()) {
                is NetResult.Success -> {
                    _messages.postValue(result.data)
                }

                is NetResult.Error -> {
                    Log.e("symbol", "getChatMessages is error: ${result.exception.message}")
                }
            }
        }
        return _messages

    }


    fun getTokenAndWs(code:String) {
        viewModelScope.launch {
            when (val result = repository.getTokenAndWs(code)) {
                is NetResult.Success -> {
                    _tokenAndWs.postValue(result.data)
                }

                is NetResult.Error -> {
                    Log.e("symbol", "getTokenAndWs is error: ${result.exception.message}")
                }
            }
        }
    }


    fun uploadImg(f: File){
        viewModelScope.launch {
            val result = repository.uploadImg(f)
            when(result){
                is NetResult.Success->{
                    _upImg.postValue(result.data)
                }
                is NetResult.Error->{
                    Log.e("symbol", "getTokenAndWs is error: ${result.exception.message}")

                }
            }
        }
    }

    fun uploadFile(){

    }


    fun getEmojis(){

    }

}