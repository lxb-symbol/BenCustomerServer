package com.ben.bencustomerserver.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ben.bencustomerserver.utils.DensityUtil
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.adapter.BenBaseRecyclerViewAdapter
import com.ben.bencustomerserver.listener.OnItemClickListener
import com.ben.bencustomerserver.model.BenMessageMenuData
import com.ben.bencustomerserver.utils.BenCommonUtils
import com.ben.bencustomerserver.views.BenPopupWindow.OnPopupWindowDismissListener
import com.ben.bencustomerserver.views.BenPopupWindow.OnPopupWindowItemClickListener
import java.util.Collections

class BenPopupWindowHelper {
    private var pMenu: BenPopupWindow? = null
    private val menuItems: MutableList<MenuItemBean?> = ArrayList()
    private val menuItemMap: MutableMap<Int, MenuItemBean> = HashMap()
    private var tvTitle: TextView? = null
    private var rvMenuList: RecyclerView? = null
    private var context: Context? = null
    private var adapter: MenuAdapter? = null
    private var itemClickListener: OnPopupWindowItemClickListener? = null
    private var dismissListener: OnPopupWindowDismissListener? = null
    private var touchable = false
    private var background: Drawable? = null
    var view: View? = null
        private set
    private var menuStyle = BenPopupWindow.Style.BOTTOM_SCREEN
    private var itemMenuIconVisible = true
    private var rvTop: RelativeLayout? = null
    private var flBottom: FrameLayout? = null
    var pRealHeight = 0
    private var mPopupView: View? = null

    init {
        if (pMenu != null) {
            pMenu!!.dismiss()
        }
        menuItems.clear()
        menuItemMap.clear()
    }

    /**
     * @param context
     */
    fun initMenu(context: Context) {
        this.context = context
        var closeChangeBg = true
        if (menuStyle !== BenPopupWindow.Style.ATTACH_ITEM_VIEW) {
            closeChangeBg = false
        }
        pMenu = BenPopupWindow(context, closeChangeBg)
        view = LayoutInflater.from(context).inflate(R.layout.ben_layout_menu_popupwindow, null)
        pMenu!!.setContentView(view!!)
        mPopupView = view!!.findViewById<View>(R.id.popup_view)
        tvTitle = view!!.findViewById<TextView>(R.id.tv_title)
        rvMenuList = view!!.findViewById<RecyclerView>(R.id.rv_menu_list)
        rvTop = view!!.findViewById<RelativeLayout>(R.id.rl_top)
        flBottom = view!!.findViewById<FrameLayout>(R.id.fl_bottom)
        adapter = MenuAdapter()
        rvMenuList?.adapter = adapter
        adapter?.mOnItemClickListener = object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                dismiss()
                if (itemClickListener != null) {
                    itemClickListener!!.onMenuItemClick(adapter?.getItem(position))
                }
            }

        }
    }

    /**
     * Add header view for menu layout
     * @param headerView
     */
    fun addHeaderView(headerView: View?) {
        if (menuStyle !== BenPopupWindow.Style.ATTACH_ITEM_VIEW) {
            if (rvTop != null && headerView != null) {
                rvTop!!.removeAllViews()
                val params = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                rvTop!!.addView(headerView, params)
                rvTop!!.visibility = View.VISIBLE
            }
        }
    }

    fun clear() {
        menuItems.clear()
        menuItemMap.clear()
    }

    fun setDefaultMenus() {
        var bean: MenuItemBean
        for (i in 0 until BenMessageMenuData.MENU_ITEM_IDS.size) {
            bean = MenuItemBean(
                0, BenMessageMenuData.MENU_ITEM_IDS[i], (i + 1) * 10,
                context!!.getString(BenMessageMenuData.MENU_TITLES[i])
            )
            bean.resourceId = BenMessageMenuData.MENU_ICONS[i]
            addItemMenu(bean)
        }
    }

    fun addItemMenu(item: MenuItemBean) {
        if (!menuItemMap.containsKey(item.itemId)) {
            menuItemMap[item.itemId] = item
        }
    }

    fun addItemMenu(groupId: Int, itemId: Int, order: Int, title: String?) {
        val item = MenuItemBean(groupId, itemId, order, title!!)
        addItemMenu(item)
    }

    fun findItem(id: Int): MenuItemBean? {
        return if (menuItemMap.containsKey(id)) {
            menuItemMap[id]
        } else null
    }

    fun findItemVisible(id: Int, visible: Boolean) {
        if (menuItemMap.containsKey(id)) {
            menuItemMap[id]!!.isVisible = visible
        }
    }

    fun setOutsideTouchable(touchable: Boolean) {
        this.touchable = touchable
    }

    fun setBackgroundDrawable(background: Drawable?) {
        this.background = background
    }

    fun showHeaderView(showHeaderView: Boolean) {
        if (rvTop != null) {
            rvTop!!.visibility = if (showHeaderView) View.VISIBLE else View.GONE
        }
    }

    private fun showPre() {
        pMenu!!.isOutsideTouchable = touchable
        pMenu!!.setBackgroundDrawable(background!!)
        checkIfShowItems()
        sortList(menuItems)
        adapter?.setData(menuItems!!)
    }

    private fun sortList(menuItems: List<MenuItemBean?>) {
        Collections.sort(menuItems) { o1, o2 ->
            val order1 = o1?.order
            val order2 = o2?.order
            if (order2!! < order1!!) {
                1
            } else if (order1 == order2) {
                0
            } else {
                -1
            }
        }
    }

    private fun checkIfShowItems() {
        if (menuItemMap.size > 0) {
            menuItems.clear()
            val iterator: Iterator<MenuItemBean> = menuItemMap.values.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (item.isVisible) {
                    menuItems.add(item)
                }
            }
        }
    }

    fun showTitle(title: String) {
        if (pMenu == null) {
            throw NullPointerException("please must init first!")
        }
        tvTitle!!.text = title
        tvTitle!!.visibility = View.VISIBLE
    }

    fun setMenuStyle(style: BenPopupWindow.Style) {
        menuStyle = style
    }

    fun setRlTopLayout(view: View?) {
        rvTop!!.removeAllViews()
        rvTop!!.addView(view)
    }

    @JvmOverloads
    fun show(parent: View, v: View, isTop: Boolean = false) {
        showPre()
        if (menuItems.size <= 0) {
            Log.e(
                "BenPopupWindowHelper",
                "Span count should be at least 1. Provided " + menuItems.size
            )
            return
        }
        if (menuStyle === BenPopupWindow.Style.ATTACH_ITEM_VIEW) {
            showAttachItemViewStyle(parent, v, isTop)
        } else if (menuStyle === BenPopupWindow.Style.BOTTOM_SCREEN) {
            showBottomToScreen(parent, v)
        } else {
            showCenterToScreen(parent, v)
        }
    }

    private fun showCenterToScreen(parent: View, v: View) {
        // Set screen's alpha
        pMenu!!.setBackgroundAlpha(screenBgAlpha)
        // Set popup window's background alpha
        view!!.alpha = popupWindowBgAlpha
        rvMenuList!!.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        pMenu!!.showAtLocation(parent, Gravity.CENTER, 0, 0)
    }

    private fun showBottomToScreen(parent: View, v: View) {
        setBottomStyleTouchEvent()
        // Set screen's alpha
        pMenu!!.setBackgroundAlpha(screenBgAlpha)
        // Set popup window's background alpha
        view!!.alpha = popupWindowBgAlpha
        rvMenuList!!.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        pMenu!!.height = ViewGroup.LayoutParams.MATCH_PARENT
        pMenu!!.width = ViewGroup.LayoutParams.MATCH_PARENT
        pMenu!!.animationStyle = R.style.message_menu_popup_window_anim_style
        pMenu!!.showAtLocation(parent, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
        val params = mPopupView!!.layoutParams
        params.width = BenCommonUtils.getScreenInfo(context!!)[0].toInt()
        params.height = DensityUtil.dp2px(context!!, 210f).toInt()
        mPopupView!!.post { pRealHeight = mPopupView!!.measuredHeight }
    }

    private fun setBottomStyleTouchEvent() {
        val screenHeight: Float = BenCommonUtils.getScreenInfo(context!!)[1]
        val minPopupWindowHeight = screenHeight.toInt() * 2 / 5
        val maxPopupWindowHeight =
            screenHeight.toInt() - BenCommonUtils.dip2px(context!!, 50).toInt()
        val expandIcon = view!!.findViewById<View>(R.id.expand_icon)
        expandIcon.visibility = View.VISIBLE
        expandIcon.setOnTouchListener(object : OnTouchListener {
            var orgX = 0
            var orgY = 0
            var offsetX = 0
            var offsetY = 0
            var popupWindowCurHeight = 0
            var slippingHeight = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        orgX = event.rawX.toInt()
                        orgY = event.rawY.toInt()
                        if (popupWindowCurHeight == 0) {
                            popupWindowCurHeight = pRealHeight
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {
                        offsetX = event.rawX.toInt() - orgX
                        offsetY = event.rawY.toInt() - orgY
                        slippingHeight = popupWindowCurHeight - offsetY
                        val layoutParams = mPopupView!!.layoutParams
                        layoutParams.height = slippingHeight
                        mPopupView!!.requestLayout()
                    }

                    MotionEvent.ACTION_UP -> {
                        if (offsetY > 0) { // slip down
                            if (minPopupWindowHeight > pRealHeight) {
                                if (slippingHeight > minPopupWindowHeight) {
                                    popupWindowCurHeight = minPopupWindowHeight
                                } else if (slippingHeight < pRealHeight - BenCommonUtils.dip2px(
                                        context!!,
                                        20
                                    ).toInt()
                                ) {
                                    dismiss()
                                } else {
                                    popupWindowCurHeight = pRealHeight
                                }
                            } else {
                                if (slippingHeight < pRealHeight - BenCommonUtils.dip2px(
                                        context!!,
                                        20
                                    ).toInt()
                                ) {
                                    dismiss()
                                } else {
                                    popupWindowCurHeight = pRealHeight
                                }
                            }
                        } else { // slip up
                            if (minPopupWindowHeight > pRealHeight) {
                                if (slippingHeight > minPopupWindowHeight) {
                                    popupWindowCurHeight = maxPopupWindowHeight
                                } else if (slippingHeight > pRealHeight) {
                                    popupWindowCurHeight = minPopupWindowHeight
                                } else {
                                    dismiss()
                                }
                            } else {
                                if (slippingHeight > pRealHeight) {
                                    popupWindowCurHeight = maxPopupWindowHeight
                                } else {
                                    dismiss()
                                }
                            }
                        }
                        val layoutParams2 = mPopupView!!.layoutParams
                        layoutParams2.height = popupWindowCurHeight
                        mPopupView!!.requestLayout()
                    }
                }
                return true
            }
        })
        view!!.findViewById<View>(R.id.top_view).setOnClickListener { dismiss() }
    }

    private fun showAttachItemViewStyle(parent: View, v: View, isTop: Boolean) {
        if (menuItems.size < SPAN_COUNT) {
            rvMenuList!!.layoutManager = GridLayoutManager(
                context,
                menuItems.size,
                RecyclerView.VERTICAL,
                false
            )
        } else {
            rvMenuList!!.layoutManager = GridLayoutManager(
                context,
                SPAN_COUNT,
                RecyclerView.VERTICAL,
                false
            )
        }
        view!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = view!!.measuredWidth
        val popupHeight = view!!.measuredHeight

        //Gets the coordinates attached to the view
        val location = IntArray(2)
        v.getLocationInWindow(location)

        //Gets the coordinates of the parent layout
        val location2 = IntArray(2)
        parent.getLocationInWindow(location2)

        //Sets the spacing between attached views
        val margin = BenCommonUtils.dip2px(context!!, 5) as Int
        val screenInfo: FloatArray = BenCommonUtils.getScreenInfo(context!!)
        var yOffset = 0
        yOffset = if (isTop) {
            if (location[1] - popupHeight - margin < location2[1]) {
                location[1] + v.height + margin
            } else {
                location[1] - popupHeight - margin
            }
        } else {
            if (location[1] + v.height + popupHeight + margin > screenInfo[1]) {
                location[1] - popupHeight - margin
            } else {
                location[1] + v.height + margin
            }
        }
        var xOffset = 0
        xOffset = if (location[0] + v.width / 2 + popupWidth / 2 + BenCommonUtils.dip2px(
                context!!,
                10
            ) > parent.width
        ) {
            (parent.width - BenCommonUtils.dip2px(context!!, 10) - popupWidth).toInt()
        } else {
            location[0] + v.width / 2 - popupWidth / 2
        }
        // Add left judgment
        if (xOffset < BenCommonUtils.dip2px(context!!, 10)) {
            xOffset = BenCommonUtils.dip2px(context!!, 10).toInt()
        }
        pMenu!!.showAtLocation(v, Gravity.NO_GRAVITY, xOffset, yOffset)
    }

    fun dismiss() {
        if (pMenu == null) {
            throw NullPointerException("please must init first!")
        }
        pMenu!!.dismiss()
        if (dismissListener != null) {
            dismissListener!!.onDismiss(pMenu)
        }
    }

    /**
     * Set item click listener
     * @param listener
     */
    fun setOnPopupMenuItemClickListener(listener: OnPopupWindowItemClickListener?) {
        itemClickListener = listener
    }

    /**
     * Listener the dismiss event
     * @param listener
     */
    fun setOnPopupMenuDismissListener(listener: OnPopupWindowDismissListener?) {
        dismissListener = listener
    }

    val popupWindow: PopupWindow?
        get() = pMenu

    fun setItemMenuIconVisible(visible: Boolean) {
        itemMenuIconVisible = visible
    }

    private inner class MenuAdapter : BenBaseRecyclerViewAdapter<MenuItemBean?>() {
        override fun getViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder<MenuItemBean?> {
            val view: View = LayoutInflater.from(context).inflate(
                if (menuStyle === BenPopupWindow.Style.ATTACH_ITEM_VIEW) R.layout.ben_layout_item_menu_popupwindow else R.layout.ben_layout_item_menu_popupwindow_horizontal,
                parent,
                false
            )
            return MenuViewHolder(view) as ViewHolder<MenuItemBean?>
        }

        val emptyLayoutId: Int
            get() = R.layout.ben_layout_no_data_show_nothing

        private inner class MenuViewHolder(itemView: View) : ViewHolder<MenuItemBean>(itemView) {
            private var ivActionIcon: ImageView? = null
            private var tvActionName: TextView? = null
            override fun initView(itemView: View?) {
                ivActionIcon = findViewById(R.id.iv_action_icon)
                tvActionName = findViewById(R.id.tv_action_name)
            }

            override fun setData(item: MenuItemBean, position: Int) {
                val title = item.title
                if (!TextUtils.isEmpty(title)) {
                    tvActionName!!.text = title
                }
                if (item.titleColor != 0) {
                    tvActionName!!.setTextColor(item.titleColor)
                }
                if (item.resourceId != 0 && itemMenuIconVisible) {
                    ivActionIcon!!.visibility = View.VISIBLE
                    ivActionIcon!!.setImageResource(item.resourceId)
                } else {
                    ivActionIcon!!.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        private const val SPAN_COUNT = 5
        private const val screenBgAlpha = 0.3f
        private const val popupWindowBgAlpha = 0.95f
    }
}