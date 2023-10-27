package com.ben.bencustomerserver.views

/**
 * 一组表情所对应的实体类
 *
 */
class BenEmojiconGroupEntity {
    /**
     * 表情数据
     */
    var emojiconList: List<BenEmojicon>? = null

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
    var type: BenEmojicon.Type? = null

    constructor()
    constructor(
        icon: Int,
        emojiconList: List<BenEmojicon>? = emptyList(),
        type: BenEmojicon.Type? = BenEmojicon.Type.NORMAL
    ) {
        this.icon = icon
        this.emojiconList = emojiconList
        this.type = type
    }


}