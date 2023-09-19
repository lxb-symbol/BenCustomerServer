package com.ben.bencustomerserver.model

// 只关注 data 的数据
//{
//    "cmd": "chatMessage",
//    "data": {
//    "name": "\u6d4b\u8bd5",
//    "avatar": "\/uploads\/20220819\/5d47d54062b8261da81891bc1445ac25.png",
//    "id": "KF_6114d5bf7fe73",
//    "time":"2023-09-1915: 17: 50",
//    "content":"\u674e\u5148\u751f",
//    "protocol":"ws",
//    "chat_log_id":"1193"}}

data class ChatSocketBean(
    var name: String,
    var avatar: String,
    var id: String,
    var time: String,
    var content: String,
    var protocol: String,
    var chat_log_id: String
) : SocketBean
