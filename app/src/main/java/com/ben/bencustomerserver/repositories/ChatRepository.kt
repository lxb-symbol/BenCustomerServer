package com.ben.bencustomerserver.repositories

import com.ben.bencustomerserver.ChatApiService
import com.ben.bencustomerserver.model.BaseMessageModel
import com.symbol.lib_net.BaseRepository
import com.symbol.lib_net.model.NetResult
import com.symbol.lib_net.net.RetrofitClient

class ChatRepository(private val service:RetrofitClient) :BaseRepository() {

    suspend fun getMessageList():NetResult<MutableList<BaseMessageModel>>{
        return callRequest {
            handleResponse(service.create(ChatApiService::class.java).getChatMessages())
        }
    }

}