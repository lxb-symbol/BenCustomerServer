package com.ben.bencustomerserver.model

import com.ben.bencustomerserver.views.BenEmojicon

class BenReactionEmojiconEntity {
    var emojicon: BenEmojicon? = null
    var count = 0
    var userList: List<String>? = null
    var isAddedBySelf = false
    override fun toString(): String {
        return "BenReactionEmojiconEntity{" +
                "emojicon=" + emojicon +
                ", count=" + count +
                ", userList=" + userList +
                ", isAddedBySelf=" + isAddedBySelf +
                '}'
    }
}