package com.ben.bencustomerserver.listener

interface IChatInputMenu {

    /**
     * 设置自定义菜单
     * @param menu
     */
    fun setCustomPrimaryMenu(menu: IChatPrimaryMenu?)

    /**
     * 设置自定义表情
     * @param menu
     */
    fun setCustomEmojiconMenu(menu: IChatEmojiconMenu)

    /**
     * 设置自定义扩展菜单
     * @param menu
     */
    fun setCustomExtendMenu(menu: IChatExtendMenu)

    /**
     * 隐藏扩展区域（包含表情和扩展菜单）
     */
    fun hideExtendContainer()

    /**
     * 是否展示表情菜单
     * @param show
     */
    fun showEmojiconMenu(show: Boolean)

    /**
     * 是否展示扩展菜单
     * @param show
     */
    fun showExtendMenu(show: Boolean)

    /**
     * 隐藏软键盘
     */
    fun hideSoftKeyboard()

    /**
     * 设置菜单监听事件
     * @param listener
     */
    fun setChatInputMenuListener(listener: ChatInputMenuListener?)

    /**
     * 获取菜单
     * @return
     */
    var primaryMenu: IChatPrimaryMenu?

    /**
     * 获取表情菜单
     * @return
     */
    var emojiconMenu: IChatEmojiconMenu?

    /**
     * 获取扩展菜单
     * @return
     */
    var  chatExtendMenu: IChatExtendMenu?

    /**
     * 点击返回
     * @return
     */
    fun onBackPressed(): Boolean
}