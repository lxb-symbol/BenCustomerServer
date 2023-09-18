package com.ben.bencustomerserver.model

data class TextMessage(
    val content: String
) : IMessageModel

data class FileMessage(
    val name: String? = "",
    val fileSize: Long = 0,
    val localPath: String? = "",
    var netPath: String? = ""
) : IMessageModel

data class ImageMessage(
    val localPath: String? = "",
    var netPath: String? = ""
) : IMessageModel

data class VoiceMessage(
    val localPath: String? = "",
    var netPath: String? = "",
    val duration: Int = 0
) : IMessageModel


/**
 * location[39.913607,116.324786,北京市海淀区复兴路辅路]
 */
data class LocationMessage(
    val name: String,
    val lat: Double,
    val lng: Double,
    val buildingName:String
    ) : IMessageModel


/**
 * video(\/uploads\/20230909\/3efac0e1c49f82d0d33e037e0c920ed3.mp4)[3efac0e1c49f82d0d33e037e0c920ed3.mp4]
 */
data class VideoMessage(
    val localPath: String? = null,
    val netPath: String? = null,
    val name: String? = null,
    val localCover: String? = null,
    val netConver: String? = null,
    val length:Int ?=0
) : IMessageModel







