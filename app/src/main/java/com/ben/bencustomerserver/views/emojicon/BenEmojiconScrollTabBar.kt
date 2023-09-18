package com.ben.bencustomerserver.views.emojicon

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.ben.bencustomerserver.utils.DensityUtil.dp2px
import com.ben.bencustomerserver.R

class BenEmojiconScrollTabBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {
    private var context: Context? = null
    private var scrollView: HorizontalScrollView? = null
    private var tabContainer: LinearLayout? = null
    private val tabList: MutableList<ImageView> = ArrayList()
    private var itemClickListener: BenScrollTabBarItemClickListener? = null

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : this(context, attrs)

    init {
        init(context, attrs)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        this.context = context
        LayoutInflater.from(context).inflate(R.layout.ben_widget_emojicon_tab_bar, this)
        scrollView = findViewById(R.id.scroll_view)
        tabContainer = findViewById(R.id.tab_container)
    }

    /**
     * add tab
     *
     * @param icon
     */
    fun addTab(icon: Int) {
        val tabView = inflate(context, R.layout.ben_scroll_tab_item, null)
        val imageView = tabView.findViewById<ImageView>(R.id.iv_icon)
        imageView.setImageResource(icon)
        val tabWidth = 60
        val imgParams = LinearLayout.LayoutParams(
            dp2px(context!!, tabWidth.toFloat()).toInt(),
            LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = imgParams
        tabContainer!!.addView(tabView)
        tabList.add(imageView)
        val position = tabList.size - 1
        imageView.setOnClickListener { v: View? ->
            if (itemClickListener != null) {
                itemClickListener!!.onItemClick(position)
            }
        }
    }

    /**
     * remove tab
     *
     * @param position
     */
    fun removeTab(position: Int) {
        tabContainer!!.removeViewAt(position)
        tabList.removeAt(position)
    }

    fun selectedTo(position: Int) {
        scrollTo(position)
        for (i in tabList.indices) {
            if (position == i) {
                tabList[i].setBackgroundColor(resources.getColor(R.color.emojicon_tab_selected))
            } else {
                tabList[i].setBackgroundColor(resources.getColor(R.color.emojicon_tab_nomal))
            }
        }
    }

    private fun scrollTo(position: Int) {
        val childCount = tabContainer!!.childCount
        if (position < childCount) {
            scrollView!!.post {
                val mScrollX = tabContainer!!.scrollX
                // symbol modified this
                val childX = tabContainer!!.getChildAt(position).x.toInt()
                if (childX < mScrollX) {
                    scrollView!!.scrollTo(childX, 0)
                    return@post
                }
                val childWidth = tabContainer!!.getChildAt(position).width
                val hsvWidth = scrollView!!.width
                val childRight = childX + childWidth
                val scrollRight = mScrollX + hsvWidth
                if (childRight > scrollRight) {
                    scrollView!!.scrollTo(childRight - scrollRight, 0)
                }
            }
        }
    }

    fun setTabBarItemClickListener(itemClickListener: BenScrollTabBarItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    interface BenScrollTabBarItemClickListener {
        fun onItemClick(position: Int)
    }
}