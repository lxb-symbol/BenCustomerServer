package com.ben.bencustomerserver.model

/**
 * {
 * name: "测试",
 * avatar: "/uploads/20220819/5d47d54062b8261da81891bc1445ac25.png",
 * id: "KF_6114d5bf7fe73",
 * time: "2023-09-27 22:58:22",
 * content: "1231",
 * protocol: "ws",
 * chat_log_id: "42879"
 * }
 */
data class WsChatMessage(
    var name: String,
    var avatar: String,
    var id: String,
    var time: String,
    var content: String,
    var protocol: String,
    var chat_log_id: String
)