package com.ben.bencustomerserver.views.emojicon

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.adapter.EmojiconGridAdapter
import com.ben.bencustomerserver.adapter.EmojiconPagerAdapter
import com.ben.bencustomerserver.utils.EaseSmileUtils
import com.ben.bencustomerserver.views.EaseEmojicon
import com.ben.bencustomerserver.views.EaseEmojiconGroupEntity

class EaseEmojiconPagerView @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null
) : ViewPager(
    context, attrs
) {
    private var groupEntities: List<EaseEmojiconGroupEntity>? = null
    private var pagerAdapter: PagerAdapter? = null
    private val emojiconRows = 3
    private var emojiconColumns = 7
    private val bigEmojiconRows = 2
    private var bigEmojiconColumns = 4
    private var firstGroupPageSize = 0
    private var maxPageCount = 0
    private var previousPagerPosition = 0
    private var pagerViewListener: EaseEmojiconPagerViewListener? = null
    private var viewpages: MutableList<View>? = null
    fun init(
        emojiconGroupList: List<EaseEmojiconGroupEntity>?,
        emijiconColumns: Int,
        bigEmojiconColumns: Int
    ) {
        if (emojiconGroupList == null) {
            throw RuntimeException("emojiconGroupList is null")
        }
        groupEntities = emojiconGroupList
        emojiconColumns = emijiconColumns
        this.bigEmojiconColumns = bigEmojiconColumns
        viewpages = ArrayList()
        for (i in groupEntities!!.indices) {
            val group = groupEntities!![i]
            val groupEmojicons = group.emojiconList
            val gridViews = getGroupGridViews(group)
            if (i == 0) {
                firstGroupPageSize = gridViews.size
            }
            maxPageCount = Math.max(gridViews.size, maxPageCount)
            (viewpages as ArrayList<View>).addAll(gridViews)
        }
        pagerAdapter = EmojiconPagerAdapter(viewpages as ArrayList<View>)
        adapter = pagerAdapter
        addOnPageChangeListener(EmojiPagerChangeListener())
        if (pagerViewListener != null) {
            pagerViewListener!!.onPagerViewInited(maxPageCount, firstGroupPageSize)
        }
    }

    fun setPagerViewListener(pagerViewListener: EaseEmojiconPagerViewListener?) {
        this.pagerViewListener = pagerViewListener
    }

    /**
     * set emojicon group position
     * @param position
     */
    fun setGroupPostion(position: Int) {
        if (adapter != null && position >= 0 && position < groupEntities!!.size) {
            var count = 0
            for (i in 0 until position) {
                count += getPageSize(groupEntities!![i])
            }
            currentItem = count
        }
    }

    /**
     * get emojicon group gridview list
     * @param groupEntity
     * @return
     */
    fun getGroupGridViews(groupEntity: EaseEmojiconGroupEntity): List<View> {
        val emojiconList = groupEntity.emojiconList
        var itemSize = emojiconColumns * emojiconRows
        val totalSize = if (emojiconList.isNullOrEmpty()) 0 else emojiconList.size
        val emojiType = groupEntity.type
        if (emojiType == EaseEmojicon.Type.BIG_EXPRESSION) {
            itemSize = bigEmojiconColumns * bigEmojiconRows
        }
        val pageSize =
            if (totalSize % itemSize == 0) totalSize / itemSize else totalSize / itemSize + 1
        val views: MutableList<View> = ArrayList()
        for (i in 0 until pageSize) {
            val view = inflate(context, R.layout.ease_expression_gridview, null)
            val gv = view.findViewById<View>(R.id.gridview) as GridView
            if (emojiType == EaseEmojicon.Type.BIG_EXPRESSION) {
                gv.numColumns = bigEmojiconColumns
            } else {
                gv.numColumns = emojiconColumns
            }
            val list: MutableList<EaseEmojicon?> = ArrayList()
            if (i != pageSize - 1) {
                emojiconList?.subList(i * itemSize, (i + 1) * itemSize)?.let { list.addAll(it) }
            } else {
                emojiconList?.subList(i * itemSize, totalSize)?.let { list.addAll(it) }
            }
            //            if(emojiType != Type.BIG_EXPRESSION){
//                EaseEmojicon deleteIcon = new EaseEmojicon();
//                deleteIcon.setEmojiText(EaseSmileUtils.DELETE_KEY);
//                list.add(deleteIcon);
//            }
            val gridAdapter = EmojiconGridAdapter(context, 1, list, emojiType!!)
            gv.adapter = gridAdapter
            gv.onItemClickListener =
                AdapterView.OnItemClickListener { parent: AdapterView<*>?, view1: View?, position: Int, id: Long ->
                    val emojicon = gridAdapter.getItem(position)
                    if (pagerViewListener != null) {
                        assert(emojicon != null)
                        val emojiText = emojicon!!.emojiText
                        if (emojiText != null && emojiText == EaseSmileUtils.DELETE_KEY) {
                            pagerViewListener!!.onDeleteImageClicked()
                        } else {
                            pagerViewListener!!.onExpressionClicked(emojicon)
                        }
                    }
                }
            views.add(view)
        }
        return views
    }

    /**
     * add emojicon group
     * @param groupEntity
     */
    fun addEmojiconGroup(groupEntity: EaseEmojiconGroupEntity, notifyDataChange: Boolean) {
        val pageSize = getPageSize(groupEntity)
        if (pageSize > maxPageCount) {
            maxPageCount = pageSize
            if (pagerViewListener != null && pagerAdapter != null) {
                pagerViewListener!!.onGroupMaxPageSizeChanged(maxPageCount)
            }
        }
        viewpages!!.addAll(getGroupGridViews(groupEntity))
        if (pagerAdapter != null && notifyDataChange) {
            pagerAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * remove emojicon group
     * @param position
     */
    fun removeEmojiconGroup(position: Int) {
        if (position > groupEntities!!.size - 1) {
            return
        }
        if (pagerAdapter != null) {
            pagerAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * get size of pages
     * @return
     */
    private fun getPageSize(groupEntity: EaseEmojiconGroupEntity): Int {
        val emojiconList = groupEntity.emojiconList
        var itemSize = emojiconColumns * emojiconRows - 1
        val totalSize = if (emojiconList.isNullOrEmpty()) 0 else emojiconList.size
        val emojiType = groupEntity.type
        if (emojiType == EaseEmojicon.Type.BIG_EXPRESSION) {
            itemSize = bigEmojiconColumns * bigEmojiconRows
        }
        return if (totalSize % itemSize == 0) totalSize / itemSize else totalSize / itemSize + 1
    }

    private inner class EmojiPagerChangeListener : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            var endSize = 0
            var groupPosition = 0
            for (groupEntity in groupEntities!!) {
                val groupPageSize = getPageSize(groupEntity)
                //if the position is in current group
                if (endSize + groupPageSize > position) {
                    //this is means user swipe to here from previous page
                    if (previousPagerPosition - endSize < 0) {
                        if (pagerViewListener != null) {
                            pagerViewListener!!.onGroupPositionChanged(groupPosition, groupPageSize)
                            pagerViewListener!!.onGroupPagePostionChangedTo(0)
                        }
                        break
                    }
                    //this is means user swipe to here from back page
                    if (previousPagerPosition - endSize >= groupPageSize) {
                        if (pagerViewListener != null) {
                            pagerViewListener!!.onGroupPositionChanged(groupPosition, groupPageSize)
                            pagerViewListener!!.onGroupPagePostionChangedTo(position - endSize)
                        }
                        break
                    }

                    //page changed
                    if (pagerViewListener != null) {
                        pagerViewListener!!.onGroupInnerPagePostionChanged(
                            previousPagerPosition - endSize,
                            position - endSize
                        )
                    }
                    break
                }
                groupPosition++
                endSize += groupPageSize
            }
            previousPagerPosition = position
        }

        override fun onPageScrollStateChanged(arg0: Int) {}
        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
    }

    interface EaseEmojiconPagerViewListener {
        /**
         * pagerview initialized
         * @param groupMaxPageSize --max pages size
         * @param firstGroupPageSize-- size of first group pages
         */
        fun onPagerViewInited(groupMaxPageSize: Int, firstGroupPageSize: Int)

        /**
         * group position changed
         * @param groupPosition--group position
         * @param pagerSizeOfGroup--page size of group
         */
        fun onGroupPositionChanged(groupPosition: Int, pagerSizeOfGroup: Int)

        /**
         * page position changed
         * @param oldPosition
         * @param newPosition
         */
        fun onGroupInnerPagePostionChanged(oldPosition: Int, newPosition: Int)

        /**
         * group page position changed
         * @param position
         */
        fun onGroupPagePostionChangedTo(position: Int)

        /**
         * max page size changed
         * @param maxCount
         */
        fun onGroupMaxPageSizeChanged(maxCount: Int)
        fun onDeleteImageClicked()
        fun onExpressionClicked(emojicon: EaseEmojicon?)
    }
}