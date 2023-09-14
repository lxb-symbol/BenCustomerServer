package com.ben.bencustomerserver.model

/**
 * 正则匹配消息内容
 */
object MessageRegular {

    fun getImageMessageName(str: String): String? {
        val regex = Regex("""img\[(.*?)\]""")
        val matchResult = regex.find(str)
        return matchResult?.groupValues?.get(1)
    }
}