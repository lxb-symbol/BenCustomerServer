package com.ben.bencustomerserver

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.NetMessageBeanOut
import com.ben.bencustomerserver.model.TokenAndWsEntity
import com.ben.bencustomerserver.model.UpFileEntity
import com.symbol.lib_net.model.BaseModel
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Streaming

interface ChatApiService {

    /**
     * uid：用户uid
     * page：页数
     * tk：token
     * t：当前时间戳
     * u：商家code
     */
    @GET(ChatApi.URL_CHAT_MESSAGES)
    suspend fun getChatMessages(@QueryMap map:HashMap<String,String>): BaseModel<NetMessageBeanOut>



    @FormUrlEncoded
    @POST(ChatApi.URL_TOKEN_OBTAIN)
    suspend fun getTokenAndWs(@Field("seller_code") sellerCode:String):BaseModel<TokenAndWsEntity>



    @FormUrlEncoded
    @POST(ChatApi.URL_UP_IMAGE)
    suspend fun uploadImg(@Body body: RequestBody):BaseModel<UpFileEntity>

    @Streaming
    @FormUrlEncoded
    @POST(ChatApi.URL_UP_FILE)
    suspend fun uploadFile(@Body body: RequestBody):BaseModel<UpFileEntity>

}