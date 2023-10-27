package com.ben.bencustomerserver.listener

import com.ben.bencustomerserver.views.BenEmojiconGroupEntity

interface IChatEmojiconMenu {
    /**
     * 添加表情
     * @param groupEntity
     */
    fun addEmojiconGroup(groupEntity: BenEmojiconGroupEntity?)

    /**
     * 添加表情列表
     * @param groupEntitieList
     */
    fun addEmojiconGroup(groupEntitieList: List<BenEmojiconGroupEntity?>?)

    /**
     * 移除表情
     * @param position
     */
    fun removeEmojiconGroup(position: Int)

    /**
     * 设置TabBar是否可见
     * @param isVisible
     */
    fun setTabBarVisibility(isVisible: Boolean)

    /**
     * 设置表情监听
     * @param listener
     */
    fun setEmojiconMenuListener(listener: BenEmojiconMenuListener?)
}