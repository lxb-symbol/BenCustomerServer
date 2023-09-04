package com.ben.bencustomerserver

import com.ben.bencustomerserver.model.BaseMessageModel
import com.symbol.lib_net.model.BaseModel
import retrofit2.http.GET
import retrofit2.http.POST

interface ChatApiService {

    @POST(ChatApi.URL_CHAT_MESSAGES)
    suspend fun getChatMessages(): BaseModel<MutableList<BaseMessageModel>>

    @GET(ChatApi.URL_TOKEN_OBTAIN)
    suspend fun getTokenAndWs():BaseModel<MutableList<String>>

}