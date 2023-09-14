package com.ben.bencustomerserver.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ben.bencustomerserver.ChatApi
import com.ben.bencustomerserver.ChatApiService
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.NetMessageBeanOut
import com.ben.bencustomerserver.model.TokenAndWsEntity
import com.ben.bencustomerserver.model.UpFileEntity
import com.symbol.lib_net.BaseRepository
import com.symbol.lib_net.model.BaseModel
import com.symbol.lib_net.model.NetResult
import com.symbol.lib_net.net.RetrofitClient
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ChatRepository(private val service: RetrofitClient) : BaseRepository() {

    suspend fun getMessageList(map:HashMap<String,String>): NetResult<NetMessageBeanOut> {
        return callRequest {
            handleResponse(service.create(ChatApiService::class.java).getChatMessages(map))
        }
    }


    suspend fun getTokenAndWs(code:String): NetResult<TokenAndWsEntity> {
        return callRequest {
            handleResponse(service.create(ChatApiService::class.java).getTokenAndWs(code))
        }
    }


    suspend fun uploadImg( file: File):NetResult<UpFileEntity>{
        return callRequest {
            val body:RequestBody =file.asRequestBody("image/jpg".toMediaType())
            handleResponse(service.create(ChatApiService::class.java).uploadImg(body))
        }
    }

}