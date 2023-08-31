package com.ben.bencustomerserver.views.chatextend

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * item decoration
 * 针对原博文（https://blog.csdn.net/Y_sunny_U/article/details/89500464）做了如下修改：
 * 增加了[.setDrawable]类，支持自定义divider.
 */
class PagingItemDecoration(context: Context, pageDecorationLastJudge: PageDecorationLastJudge?) :
    RecyclerView.ItemDecoration() {
    private var mDivider: Drawable?
    var mPageDecorationLastJudge: PageDecorationLastJudge

    init {
        requireNotNull(pageDecorationLastJudge) { "pageDecorationLastJudge must be no null" }
        mPageDecorationLastJudge = pageDecorationLastJudge
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        a.recycle()
    }

    /**
     * Sets the [Drawable] for this divider.
     * @param drawable Drawable that should be used as a divider.
     */
    fun setDrawable(drawable: Drawable) {
        requireNotNull(drawable) { "Drawable cannot be null." }
        mDivider = drawable
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawHorizontal(c, parent)
        drawVertical(c, parent)
    }

    fun drawHorizontal(c: Canvas?, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val left = child.left - params.leftMargin
            val right = (child.right + params.rightMargin
                    + mDivider!!.intrinsicWidth)
            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider!!.intrinsicHeight
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(c!!)
        }
    }

    fun drawVertical(c: Canvas?, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin
            val left = child.right + params.rightMargin
            val right = left + mDivider!!.intrinsicWidth
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(c!!)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view)
        if (mPageDecorationLastJudge.isPageLast(itemPosition)) {
            outRect[0, 0, 0] = 0
        } else if (mPageDecorationLastJudge.isLastRow(itemPosition)) // 如果是最后一行，则不需要绘制底部
        {
            outRect[0, 0, mDivider!!.intrinsicWidth] = 0
        } else if (mPageDecorationLastJudge.isLastColumn(itemPosition)) // 如果是最后一列，则不需要绘制右边
        {
            outRect[0, 0, 0] = mDivider!!.intrinsicHeight
        } else {
            outRect[0, 0, mDivider!!.intrinsicWidth] = mDivider!!.intrinsicHeight
        }
    }

    companion object {
        private val ATTRS = intArrayOf(R.attr.listDivider)
    }
}