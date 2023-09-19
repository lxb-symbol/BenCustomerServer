package com.ben.bencustomerserver

import com.ben.bencustomerserver.model.NetMessageBean
import com.ben.bencustomerserver.model.TokenAndWsEntity
import com.ben.bencustomerserver.model.UpFileEntity
import com.symbol.lib_net.model.BaseModel
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.QueryMap

interface ChatApiService {

    /**
     * uid：用户uid
     * page：页数
     * tk：token
     * t：当前时间戳
     * u：商家code
     */
    @GET(ChatApi.URL_CHAT_MESSAGES)
    suspend fun getChatMessages(@QueryMap map: HashMap<String, String>): BaseModel<List<NetMessageBean>>


    @FormUrlEncoded
    @POST(ChatApi.URL_TOKEN_OBTAIN)
    suspend fun getTokenAndWs(@Field("seller_code") sellerCode: String): BaseModel<TokenAndWsEntity>


    @Multipart
    @POST(ChatApi.URL_UP_IMAGE)
    suspend fun uploadImg(@Part body: MultipartBody.Part): BaseModel<UpFileEntity>


    @Multipart
    @POST(ChatApi.URL_UP_FILE)
    suspend fun uploadFile(@Part body: MultipartBody.Part): BaseModel<UpFileEntity>


    @FormUrlEncoded
    @POST(ChatApi.URL_QUERY_BOLT)
    suspend fun queryBolt(@FieldMap map: HashMap<String, String>): BaseModel<String>


    @FormUrlEncoded
    @POST(ChatApi.URL_AUTO_ANSWER)
    suspend fun autoAnswer(@FieldMap map: HashMap<String, String>): BaseModel<String>

    @FormUrlEncoded
    @POST(ChatApi.URL_EMOJI_LIST)
    suspend fun emojis(@Field("seller_code") code: String): BaseModel<List<String>>


}