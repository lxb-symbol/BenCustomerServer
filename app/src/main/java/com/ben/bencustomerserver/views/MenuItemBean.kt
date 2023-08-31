package com.ben.bencustomerserver.views

import java.util.Objects

class MenuItemBean(var groupId: Int, var itemId: Int, var order: Int, var title: String) {
    var isVisible = true
    var resourceId = 0
    var titleColor = 0

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as MenuItemBean
        return groupId == that.groupId && itemId == that.itemId && order == that.order
    }

    override fun hashCode(): Int {
        return Objects.hash(groupId, itemId, order)
    }
}