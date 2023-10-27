package com.ben.bencustomerserver.model

/**
 * 正则匹配消息内容
 */
object MessageRegular {


    /**
     * video(\/uploads\/20230909\/3efac0e1c49f82d0d33e037e0c920ed3.mp4)[3efac0e1c49f82d0d33e037e0c920ed3.mp4]
     */
    fun matchVideoUrl(str: String): String? {
        val regex = Regex("""video\((.*?)\)""")
        val matchResult = regex.find(str)
        return matchResult?.groupValues?.get(1)
    }

    /**
     * video(\/uploads\/20230909\/3efac0e1c49f82d0d33e037e0c920ed3.mp4)[3efac0e1c49f82d0d33e037e0c920ed3.mp4]
     */
    fun matchVideoName(str: String): String? {
        val regex = Regex("""\[(.*?)\]""")
        val matchResult = regex.find(str)
        return matchResult?.groupValues?.get(1)
    }

    /**
     * 输入内容  location[39.913607,116.324786,北京市海淀区复兴路辅路]
     */
    fun matchLocationMessage(str: String): String? {
        val regex = Regex("""location\[(.*?)\]""")
        val matchResult = regex.find(str)
        return matchResult?.groupValues?.get(1)
    }


    fun matchImageMessageUrl(str: String): String? {
        val regex = Regex("""img\[(.*?)\]""")
        val matchResult = regex.find(str)
        return matchResult?.groupValues?.get(1)
    }

    /**
     *  str：audio[/uploads/20230909/64fc1d1dba7d3.wav,8870]
     *
     *后续拿到内容的路径，毫秒数
     *
     */
    fun matchVoiceMessageSome(str: String): String? {
        val regex = Regex("""audio\[(.*?)\]""")
        val matchResult = regex.find(str)
        return matchResult?.groupValues?.get(1)
    }


    /**
     * 输入： /uploads/20230909/64fc1d1dba7d3.wav,8870
     */
    fun getVoiceUrl(str: String): String {
        val dotIndex = str.indexOf(",")
        return str.substring(dotIndex)
    }

    /**
     * 输入： /uploads/20230909/64fc1d1dba7d3.wav,8870
     *
     */
    fun getVoiceDuration(str: String): String {
        val dotIndex = str.indexOf(",")
        return str.substring(dotIndex, str.length)
    }


    fun getLocationMessageAttr(source: String, index: Int): String {
        val attrs = source.split(",")
        if (attrs == null || attrs.isEmpty()) return ""
        return attrs[index]
    }

    /**
     * file(/uploads/20230905/c10498415f42b0adaa53bff956659a3a.docx)[BOM相关功能需前端制作的页面-20221114.docx]
     */
    fun matchFileUrl(str: String): String? {
        val regex = Regex("""file\((.*?)\)""")
        val matchResult = regex.find(str)
        return matchResult?.groupValues?.get(1)
    }


    fun matchFileName(str: String): String? {
        val regex = Regex("""\[(.*?)\]""")
        val matchResult = regex.find(str)
        return matchResult?.groupValues?.get(1)
    }

}