package com.ben.bencustomerserver.listener

interface IChatExtendMenu {
    /**
     * 清理扩展功能
     */
    fun clear()

    /**
     * 设置条目的排序
     * @param itemId
     * @param order
     */
    fun setMenuOrder(itemId: Int, order: Int)



    /**
     * 添加新的扩展功能
     * @param name
     * @param drawableRes
     * @param itemId
     * @param order
     */
    fun registerMenuItem(name: String?="", drawableRes: Int=0, itemId: Int=0, order: Int=0)



    /**
     * 设置条目监听
     * @param listener
     */
    fun setBenChatExtendMenuItemClickListener(listener: BenChatExtendMenuItemClickListener?)
}