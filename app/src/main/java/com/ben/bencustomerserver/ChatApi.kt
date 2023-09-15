package com.ben.bencustomerserver

object ChatApi {

    /**
     * 历史聊天记录
     *
     * uid：用户uid
     * page：页数
     * tk：token
     * t：当前时间戳
     * u：商家code
     *
     */
    const val URL_CHAT_MESSAGES = "/index/index/getChatLog"

    /**
     *获取token 和 WS 链接
     */
    const val URL_TOKEN_OBTAIN = "index/api/getToken"

    /**
     * 上传图片
     */
    const val URL_UP_IMAGE = "index/upload/uploadImg"

    /**
     * 上传文件
     */
    const val URL_UP_FILE = "index/upload/uploadFile"

    /**
     * 询问机器人
     */
    const val URL_QUERY_BOLT = "index/robot/service"


    /**
     * 自动回答
     */
    const val URL_AUTO_ANSWER = "index/robot/autoAnswer "

}