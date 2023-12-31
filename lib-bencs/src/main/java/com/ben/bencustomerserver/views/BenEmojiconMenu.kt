package com.ben.bencustomerserver.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.listener.BenEmojiconMenuListener
import com.ben.bencustomerserver.listener.IChatEmojiconMenu
import com.ben.bencustomerserver.model.BenDefaultEmojiconDatas.data
import com.ben.bencustomerserver.views.emojicon.BenEmojiconIndicatorView
import com.ben.bencustomerserver.views.emojicon.BenEmojiconPagerView
import com.ben.bencustomerserver.views.emojicon.BenEmojiconPagerView.BenEmojiconPagerViewListener
import com.ben.bencustomerserver.views.emojicon.BenEmojiconScrollTabBar
import com.ben.bencustomerserver.views.emojicon.BenEmojiconScrollTabBar.BenScrollTabBarItemClickListener

class BenEmojiconMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), IChatEmojiconMenu {
    private var emojiconColumns = 0
    private var bigEmojiconColumns = 0
    private val tabBar: BenEmojiconScrollTabBar
    private val indicatorView: BenEmojiconIndicatorView
    private val pagerView: BenEmojiconPagerView
    private val emojiconGroupList: MutableList<BenEmojiconGroupEntity> = ArrayList()
    private var listener: BenEmojiconMenuListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.ben_widget_emojicon, this)
        pagerView = findViewById<View>(R.id.pager_view) as BenEmojiconPagerView
        indicatorView = findViewById<View>(R.id.indicator_view) as BenEmojiconIndicatorView
        tabBar = findViewById<View>(R.id.tab_bar) as BenEmojiconScrollTabBar
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.BenEmojiconMenu)
        emojiconColumns = ta.getInt(R.styleable.BenEmojiconMenu_emojiconColumns, defaultColumns)
        bigEmojiconColumns =
            ta.getInt(R.styleable.BenEmojiconMenu_bigEmojiconRows, defaultBigColumns)
        ta.recycle()
    }

    @JvmOverloads
    fun init(groupEntities: MutableList<BenEmojiconGroupEntity>? = null) {
        var groupEntities = groupEntities
        if (groupEntities.isNullOrEmpty()) {
            groupEntities = ArrayList()
            groupEntities.add(BenEmojiconGroupEntity(R.drawable.ee_1, data.asList()))
        }
        for (groupEntity in groupEntities) {
            emojiconGroupList.add(groupEntity)
            tabBar.addTab(groupEntity.icon)
        }
        pagerView.setPagerViewListener(EmojiconPagerViewListener())
        pagerView.init(emojiconGroupList, emojiconColumns, bigEmojiconColumns)
        tabBar.setTabBarItemClickListener(object : BenScrollTabBarItemClickListener {
            override fun onItemClick(position: Int) {
                pagerView.setGroupPostion(position)
            }
        })
    }

    /**
     * add emojicon group
     * @param groupEntity
     */
    override fun addEmojiconGroup(groupEntity: BenEmojiconGroupEntity?) {
        groupEntity?.let {
            emojiconGroupList.add(it)
        }
        pagerView.addEmojiconGroup(groupEntity!!, true)
        tabBar.addTab(groupEntity.icon)
    }

    /**
     * add emojicon group list
     * @param groupEntitieList
     */
    override fun addEmojiconGroup(groupEntitieList: List<BenEmojiconGroupEntity?>?) {
        for (i in groupEntitieList!!.indices) {
            val groupEntity = groupEntitieList[i]
            groupEntity?.let {
                emojiconGroupList.add(it)
            }
            pagerView.addEmojiconGroup(
                groupEntity!!,
                i == groupEntitieList.size - 1
            )
            tabBar.addTab(groupEntity.icon)
        }
    }

    /**
     * remove emojicon group
     * @param position
     */
    override fun removeEmojiconGroup(position: Int) {
        emojiconGroupList.removeAt(position)
        pagerView.removeEmojiconGroup(position)
        tabBar.removeTab(position)
    }

    override fun setTabBarVisibility(isVisible: Boolean) {
        if (!isVisible) {
            tabBar.visibility = GONE
        } else {
            tabBar.visibility = VISIBLE
        }
    }

    override fun setEmojiconMenuListener(listener: BenEmojiconMenuListener?) {
        this.listener = listener
    }

    private inner class EmojiconPagerViewListener : BenEmojiconPagerViewListener {
        override fun onPagerViewInited(groupMaxPageSize: Int, firstGroupPageSize: Int) {
            indicatorView.init(groupMaxPageSize)
            indicatorView.updateIndicator(firstGroupPageSize)
            tabBar.selectedTo(0)
        }

        override fun onGroupPositionChanged(groupPosition: Int, pagerSizeOfGroup: Int) {
            indicatorView.updateIndicator(pagerSizeOfGroup)
            tabBar.selectedTo(groupPosition)
        }

        override fun onGroupInnerPagePostionChanged(oldPosition: Int, newPosition: Int) {
            indicatorView.selectTo(oldPosition, newPosition)
        }

        override fun onGroupPagePostionChangedTo(position: Int) {
            indicatorView.selectTo(position)
        }

        override fun onGroupMaxPageSizeChanged(maxCount: Int) {
            indicatorView.updateIndicator(maxCount)
        }

        override fun onDeleteImageClicked() {
            if (listener != null) {
                listener!!.onDeleteImageClicked()
            }
        }

        override fun onExpressionClicked(emojicon: BenEmojicon?) {
            if (listener != null) {
                listener!!.onExpressionClicked(emojicon)
            }
        }
    }

    companion object {
        private const val defaultColumns = 7
        private const val defaultBigColumns = 4
    }
}