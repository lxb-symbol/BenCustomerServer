package com.ben.bencustomerserver.model

data class TextMessage(
    val content: String
) : IMessageModel

data class FileMessage(
    var name: String? = "",
    val fileSize: Long = 0,
    var localPath: String? = "",
    var netPath: String? = ""
) : IMessageModel

data class ImageMessage(
    val localPath: String? = "",
    var netPath: String? = ""
) : IMessageModel

data class VoiceMessage(
    var localPath: String? = "",
    var netPath: String? = "",
    val duration: Int = 0,
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
    var localPath: String? = null,
    var netPath: String? = null,
    var name: String? = null,
    var localCover: String? = null,
    var netConver: String? = null,
    var length:Int ?=0
) : IMessageModel







