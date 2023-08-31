package com.ben.bencustomerserver.listener

import android.view.View
import android.widget.PopupWindow
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.views.EasePopupWindowHelper
import com.ben.bencustomerserver.views.MenuItemBean

/**
 * EasePopupWindowHelper}中的条目点击事件
 */
interface OnMenuChangeListener {
    /**
     * 展示Menu之前
     * @param helper
     * @param message
     */
    fun onPreMenu(helper: EasePopupWindowHelper?, message: BaseMessageModel?, v: View?)

    /**
     * 点击条目
     * @param item
     * @param message
     */
    fun onMenuItemClick(item: MenuItemBean?, message: BaseMessageModel?): Boolean

    /**
     * 消失
     * @param menu
     */
    fun onDismiss(menu: PopupWindow?) {}
}