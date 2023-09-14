package com.ben.bencustomerserver.model

data class TextMessage(
    val content: String
) : IMessageModel

data class FileMessage(
    val fileSize: Long = 0,
    val localPath: String? = "",
    val netPath: String? = ""
) : IMessageModel

data class ImageMessage(
    val localPath: String? = "",
    val netPath: String? = ""
) : IMessageModel

data class VoiceMessage(
    val localPath: String? = "",
    val netPath: String? = "",
    val duration: Int = 0
)





