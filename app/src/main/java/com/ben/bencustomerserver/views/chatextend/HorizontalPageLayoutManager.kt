package com.ben.bencustomerserver.views.chatextend

import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 本LayoutManager提供了类似ViewPager+GridView的分页效果。
 * 针对原博文（https://blog.csdn.net/Y_sunny_U/article/details/89500464）做了如下修改：
 * 1、recyclerView的高度模式是wrap_content时，主动设置条目的高度[.setItemHeight]
 * 参考博文：https://blog.csdn.net/Y_sunny_U/article/details/89500464
 */
class HorizontalPageLayoutManager(rows: Int, columns: Int) : RecyclerView.LayoutManager(),
    PageDecorationLastJudge {
    private var totalHeight = 0
    private var totalWidth = 0
    private var offsetY = 0
    private var offsetX = 0
    private var rows = 0
    private var columns = 0
    private var pageSize = 0
    private var itemWidth = 0
    private var itemHeight = 0
    private var onePageSize = 0
    private var itemWidthUsed = 0
    private var itemHeightUsed = 0
    private var itemSetHeight = 0
    private var isUseSetHeight = false
    private var heightMode = 0
    private val allItemFrames = SparseArray<Rect>()
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    init {
        this.rows = rows
        this.columns = columns
        onePageSize = rows * columns
    }

    fun setItemHeight(height: Int) {
        itemSetHeight = height
        isUseSetHeight = height > 0
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        detachAndScrapAttachedViews(recycler)
        val newX = offsetX + dx
        var result = dx
        if (newX > totalWidth) {
            result = totalWidth - offsetX
        } else if (newX < 0) {
            result = 0 - offsetX
        }
        offsetX += result
        offsetChildrenHorizontal(-result)
        recycleAndFillItems(recycler, state)
        return result
    }

    private val usableWidth: Int
        get() = width - paddingLeft - paddingRight
    private val usableHeight: Int
        get() = height - paddingTop - paddingBottom

    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
        var heightSpec = heightSpec
        heightMode = View.MeasureSpec.getMode(heightSpec)
        if (heightMode == View.MeasureSpec.AT_MOST) {
            if (isUseSetHeight) {
                heightSpec =
                    View.MeasureSpec.makeMeasureSpec(itemSetHeight * rows, View.MeasureSpec.EXACTLY)
            }
            totalHeight = View.MeasureSpec.getSize(heightSpec)
        }
        super.onMeasure(recycler, state, widthSpec, heightSpec)
    }

    /**
     * 返回true使用recyclerView的自动测量
     * @return
     */
    override fun isAutoMeasureEnabled(): Boolean {
        return false
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler)
            return
        }
        if (state.isPreLayout) {
            return
        }
        //获取每个Item的平均宽高
        itemWidth = usableWidth / columns
        itemHeight = usableHeight / rows

        //针对高度方向为wrap_content的情况
        if (itemHeight == 0) {
            wrapItemHeight
        }

        //计算宽高已经使用的量，主要用于后期测量
        itemWidthUsed = (columns - 1) * itemWidth
        itemHeightUsed = (rows - 1) * itemHeight
        //计算总的页数
        computePageSize(state)
        //计算可以横向滚动的最大值
        totalWidth = (pageSize - 1) * width
        //分离view
        detachAndScrapAttachedViews(recycler)
        val count = itemCount
        var p = 0
        while (p < pageSize) {
            var r = 0
            while (r < rows) {
                var c = 0
                while (c < columns) {
                    val index = p * onePageSize + r * columns + c
                    if (index == count) {
                        //跳出多重循环
                        c = columns
                        r = rows
                        p = pageSize
                        break
                    }
                    val view = recycler.getViewForPosition(index)
                    addView(view)
                    //测量item
                    measureChildWithMargins(view, itemWidthUsed, itemHeightUsed)
                    val width = getDecoratedMeasuredWidth(view)
                    var height = getDecoratedMeasuredHeight(view)
                    //如何设置了条目高度，则使用；没有设置，则使用真实的条目高度作为itemHeight
                    if (isUseSetHeight) {
                        height = wrapItemHeight
                        itemHeight = height
                    } else {
                        if (index == 0 && height != 0) {
                            itemHeight = height
                        }
                    }
                    //记录显示范围
                    var rect = allItemFrames[index]
                    if (rect == null) {
                        rect = Rect()
                    }
                    val x = p * usableWidth + c * itemWidth
                    val y = r * itemHeight
                    rect[x, y, width + x] = height + y
                    allItemFrames.put(index, rect)
                    c++
                }
                r++
            }
            //每一页循环以后就回收一页的View用于下一页的使用
            removeAndRecycleAllViews(recycler)
            p++
        }
        recycleAndFillItems(recycler, state)
        requestLayout()
    }

    private val wrapItemHeight: Int
        /**
         * 获取wrap_content下，条目的高度
         * @return
         */
        private get() {
            //如果条目高度是wrap_content模式
            if (heightMode == View.MeasureSpec.AT_MOST) {
                //如果设置了条目高度，则采用设置的高度
                itemHeight = if (isUseSetHeight) {
                    //判断设置的条目高度是否超过可用高度
                    if (itemSetHeight * rows <= totalHeight) {
                        itemSetHeight
                    } else {
                        totalHeight / rows
                    }
                } else {
                    totalHeight / rows
                }
                return itemHeight
            }
            return itemHeight
        }

    private fun computePageSize(state: RecyclerView.State) {
        pageSize = state.itemCount / onePageSize + if (state.itemCount % onePageSize == 0) 0 else 1
    }

    override fun onDetachedFromWindow(view: RecyclerView, recycler: RecyclerView.Recycler) {
        super.onDetachedFromWindow(view, recycler)
        offsetX = 0
        offsetY = 0
    }

    private fun recycleAndFillItems(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (state.isPreLayout) {
            return
        }
        val displayRect = Rect(
            paddingLeft + offsetX,
            paddingTop,
            width - paddingLeft - paddingRight + offsetX,
            height - paddingTop - paddingBottom
        )
        val childRect = Rect()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            childRect.left = getDecoratedLeft(child!!)
            childRect.top = getDecoratedTop(child)
            childRect.right = getDecoratedRight(child)
            childRect.bottom = getDecoratedBottom(child)
            if (!Rect.intersects(displayRect, childRect)) {
                removeAndRecycleView(child, recycler)
            }
        }
        for (i in 0 until itemCount) {
            if (Rect.intersects(displayRect, allItemFrames[i])) {
                val view = recycler.getViewForPosition(i)
                addView(view)
                measureChildWithMargins(view, itemWidthUsed, itemHeightUsed)
                val rect = allItemFrames[i]
                layoutDecorated(
                    view,
                    rect.left - offsetX,
                    rect.top,
                    rect.right - offsetX,
                    rect.bottom
                )
            }
        }
    }

    override fun isLastRow(index: Int): Boolean {
        if (index >= 0 && index < itemCount) {
            var indexOfPage = index % onePageSize
            indexOfPage++
            if (indexOfPage > (rows - 1) * columns && indexOfPage <= onePageSize) {
                return true
            }
        }
        return false
    }

    override fun isLastColumn(position: Int): Boolean {
        var position = position
        if (position >= 0 && position < itemCount) {
            position++
            if (position % columns == 0) {
                return true
            }
        }
        return false
    }

    override fun isPageLast(position: Int): Boolean {
        var position = position
        position++
        return position % onePageSize == 0
    }

    override fun computeHorizontalScrollRange(state: RecyclerView.State): Int {
        computePageSize(state)
        return pageSize * width
    }

    override fun computeHorizontalScrollOffset(state: RecyclerView.State): Int {
        return offsetX
    }

    override fun computeHorizontalScrollExtent(state: RecyclerView.State): Int {
        return width
    }
}