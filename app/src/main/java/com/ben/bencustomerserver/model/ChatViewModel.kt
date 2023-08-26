package com.ben.bencustomerserver.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ben.bencustomerserver.repositories.ChatRepository
import com.symbol.lib_net.BaseRepository
import com.symbol.lib_net.model.NetResult
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _messages = MutableLiveData<List<String>>()

    fun getDataMessages() = _messages

    fun chatMessages(): MutableLiveData<List<String>> {
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





}