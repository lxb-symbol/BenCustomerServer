package com.ben.bencustomerserver.listener

import com.ben.bencustomerserver.views.EasePopupWindowHelper
import com.ben.bencustomerserver.views.MenuItemBean

interface IPopupWindow {
    /**
     * 是否展示默认的条目菜单
     * @param showDefault
     */
    fun showItemDefaultMenu(showDefault: Boolean)

    /**
     * 清除所有菜单项
     */
    fun clearMenu()

    /**
     * 添加菜单项
     * @param item
     */
    fun addItemMenu(item: MenuItemBean?)

    /**
     * 添加菜单项
     * @param groupId
     * @param itemId
     * @param order
     * @param title
     */
    fun addItemMenu(groupId: Int, itemId: Int, order: Int, title: String?)

    /**
     * 查找菜单对象，如果id不存在则返回null
     * @param id
     * @return
     */
    fun findItem(id: Int): MenuItemBean?

    /**
     * 设置菜单项可见性
     * @param id
     * @param visible
     */
    fun findItemVisible(id: Int, visible: Boolean)

    /**
     * 设置菜单条目监听
     * @param listener
     */
    fun setOnPopupWindowItemClickListener(listener: OnMenuChangeListener?)

    /**
     * 返回菜单帮助类
     * @return
     */
    val menuHelper: EasePopupWindowHelper?
}