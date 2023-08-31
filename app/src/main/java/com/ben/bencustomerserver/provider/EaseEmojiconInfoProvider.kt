package com.ben.bencustomerserver.provider

import com.ben.bencustomerserver.views.EaseEmojicon

interface EaseEmojiconInfoProvider {
    /**
     * return EaseEmojicon for input emojiconIdentityCode
     * @param emojiconIdentityCode
     * @return
     */
    fun getEmojiconInfo(emojiconIdentityCode: String?): EaseEmojicon?

    /**
     * get Emojicon map, key is the text of emoji, value is the resource id or local path of emoji icon(can't be URL on internet)
     * @return
     */
    fun getTextEmojiconMapping(): Map<String?, Any?>?
}