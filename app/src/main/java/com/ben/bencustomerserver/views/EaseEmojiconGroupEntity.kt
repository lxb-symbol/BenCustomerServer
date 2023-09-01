package com.ben.bencustomerserver.views

/**
 * 一组表情所对应的实体类
 *
 */
class EaseEmojiconGroupEntity {
    /**
     * 表情数据
     */
    var emojiconList: List<EaseEmojicon>? = null

    /**
     * 图片
     */
    @JvmField
    var icon = 0

    /**
     * 组名
     */
    var name: String? = null

    /**
     * 表情类型
     */
    var type: EaseEmojicon.Type? = null

    constructor()
    constructor(
        icon: Int,
        emojiconList: List<EaseEmojicon>? = emptyList(),
        type: EaseEmojicon.Type? = EaseEmojicon.Type.NORMAL
    ) {
        this.icon = icon
        this.emojiconList = emojiconList
        this.type = type
    }


}