package com.ben.bencustomerserver.model

import com.ben.bencustomerserver.utils.MMkvTool

//{
//    customer_id: 用户uid,
//    customer_name: 用户名称,
//    customer_avatar: 用户头像,
//    seller_code: 商家code,
//    tk: 1接口返回的token,
//    t: 当前时间戳
//}

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

//    fun createCustomerInData(bean:BaseMessageModel):CustomerInMessage{
//        CustomerInMessage(MessageType.)
//    }

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




}