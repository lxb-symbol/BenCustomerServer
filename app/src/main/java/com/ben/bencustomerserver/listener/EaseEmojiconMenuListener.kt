package com.ben.bencustomerserver.listener

interface EaseEmojiconMenuListener {
    /**
     * on emojicon clicked
     * @param emojicon
     */
    fun onExpressionClicked(emojicon: Any?)

    /**
     * on delete image clicked
     */
    fun onDeleteImageClicked() {}
}