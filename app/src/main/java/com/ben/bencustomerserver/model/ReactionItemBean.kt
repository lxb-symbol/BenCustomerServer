package com.ben.bencustomerserver.model

class ReactionItemBean {
    var identityCode: String? = null
    var icon = 0
    var emojiText: String? = null
    override fun toString(): String {
        return "ReactionItemBean{" +
                "identityCode='" + identityCode + '\'' +
                ", icon=" + icon +
                ", emojiText='" + emojiText + '\'' +
                '}'
    }
}