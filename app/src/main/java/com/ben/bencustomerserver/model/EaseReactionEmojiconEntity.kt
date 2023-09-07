package com.ben.bencustomerserver.model

import com.ben.bencustomerserver.views.EaseEmojicon

class EaseReactionEmojiconEntity {
    var emojicon: EaseEmojicon? = null
    var count = 0
    var userList: List<String>? = null
    var isAddedBySelf = false
    override fun toString(): String {
        return "EaseReactionEmojiconEntity{" +
                "emojicon=" + emojicon +
                ", count=" + count +
                ", userList=" + userList +
                ", isAddedBySelf=" + isAddedBySelf +
                '}'
    }
}