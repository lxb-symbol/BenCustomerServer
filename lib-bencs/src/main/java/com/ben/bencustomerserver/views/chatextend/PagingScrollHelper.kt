package com.ben.bencustomerserver.views.chatextend

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.recyclerview.widget.RecyclerView

/**
 * 实现RecycleView分页滚动的工具类
 * 增加了判断，如果页面没有变动，则不调用[.mOnPageChangeListener]
 * 参考博文：https://blog.csdn.net/Y_sunny_U/article/details/89500464
 */
class PagingScrollHelper {
    var mRecyclerView: RecyclerView? = null
    private val mOnScrollListener = PageOnScrollListener()
    private val mOnFlingListener = PageOnFlingListener()
    private var offsetY = 0
    private var offsetX = 0
    var startY = 0
    var startX = 0
    var mAnimator: ValueAnimator? = null
    private val mOnTouchListener = MyOnTouchListener()
    private var firstTouch = true
    private var mOnPageChangeListener: onPageChangeListener? = null
    private var lastPageIndex = 0
    private var mOrientation = ORIENTATION.HORIZONTAL
    private var currentPosition = 0

    internal enum class ORIENTATION {
        HORIZONTAL, VERTICAL, NULL
    }

    fun setUpRecycleView(recycleView: RecyclerView?) {
        requireNotNull(recycleView) { "recycleView must be not null" }
        mRecyclerView = recycleView
        //处理滑动
        recycleView.onFlingListener = mOnFlingListener
        //设置滚动监听，记录滚动的状态，和总的偏移量
        recycleView.addOnScrollListener(mOnScrollListener)
        //记录滚动开始的位置
        recycleView.setOnTouchListener(mOnTouchListener)
        //获取滚动的方向
        updateLayoutManger()
    }

    fun updateLayoutManger() {
        val layoutManager = mRecyclerView!!.layoutManager
        if (layoutManager != null) {
            mOrientation = if (layoutManager.canScrollVertically()) {
                ORIENTATION.VERTICAL
            } else if (layoutManager.canScrollHorizontally()) {
                ORIENTATION.HORIZONTAL
            } else {
                ORIENTATION.NULL
            }
            if (mAnimator != null) {
                mAnimator!!.cancel()
            }
            startX = 0
            startY = 0
            offsetX = 0
            offsetY = 0
        }
    }

    val pageCount: Int
        /**
         * 获取总共的页数
         */
        get() {
            if (mRecyclerView != null) {
                if (mOrientation == ORIENTATION.NULL) {
                    return 0
                }
                if (mOrientation == ORIENTATION.VERTICAL && mRecyclerView!!.computeVerticalScrollExtent() != 0) {
                    return mRecyclerView!!.computeVerticalScrollRange() / mRecyclerView!!.computeVerticalScrollExtent()
                } else if (mRecyclerView!!.computeHorizontalScrollExtent() != 0) {
                    return mRecyclerView!!.computeHorizontalScrollRange() / mRecyclerView!!.computeHorizontalScrollExtent()
                }
            }
            return 0
        }

    fun scrollToPosition(position: Int) {
        currentPosition = position
        if (mAnimator == null) {
            mOnFlingListener.onFling(0, 0)
        }
        if (mAnimator != null) {
            val startPoint = if (mOrientation == ORIENTATION.VERTICAL) offsetY else offsetX
            var endPoint = 0
            endPoint =
                if (mOrientation == ORIENTATION.VERTICAL) {
                    mRecyclerView!!.height * position
                } else {
                    mRecyclerView!!.width * position
                }
            if (startPoint != endPoint) {
                mAnimator!!.setIntValues(startPoint, endPoint)
                mAnimator!!.start()
            }
        }
    }

    fun checkCurrentStatus() {
        if (mOrientation == ORIENTATION.VERTICAL) {
            if (mRecyclerView != null) {
                if (offsetY != mRecyclerView!!.height * currentPosition) {
                    offsetX = mRecyclerView!!.height * currentPosition
                    mRecyclerView!!.scrollTo(0, offsetY)
                }
            }
        } else {
            if (mRecyclerView != null) {
                if (offsetX != mRecyclerView!!.width * currentPosition) {
                    offsetX = mRecyclerView!!.width * currentPosition
                    mRecyclerView!!.scrollTo(offsetX, 0)
                }
            }
        }
    }

    inner class PageOnFlingListener : RecyclerView.OnFlingListener() {
        override fun onFling(velocityX: Int, velocityY: Int): Boolean {
            if (mOrientation == ORIENTATION.NULL) {
                return false
            }
            //获取开始滚动时所在页面的index
            var p: Int = startPageIndex

            //记录滚动开始和结束的位置
            var endPoint = 0
            var startPoint = 0

            //如果是垂直方向
            if (mOrientation == ORIENTATION.VERTICAL) {
                startPoint = offsetY
                if (velocityY < 0) {
                    p--
                } else if (velocityY > 0) {
                    p++
                }
                //更具不同的速度判断需要滚动的方向
                //注意，此处有一个技巧，就是当速度为0的时候就滚动会开始的页面，即实现页面复位
                endPoint = p * mRecyclerView!!.height
            } else {
                startPoint = offsetX
                if (velocityX < 0) {
                    p--
                } else if (velocityX > 0) {
                    p++
                }
                endPoint = p * mRecyclerView!!.width
            }
            if (endPoint < 0) {
                endPoint = 0
            }

            //使用动画处理滚动
            if (mAnimator == null) {
                mAnimator = ValueAnimator.ofInt(startPoint, endPoint)
                mAnimator?.duration = 200
                mAnimator?.addUpdateListener { animation ->
                    val nowPoint = animation.animatedValue as Int
                    if (mOrientation == ORIENTATION.VERTICAL) {
                        val dy = nowPoint - offsetY
                        //这里通过RecyclerView的scrollBy方法实现滚动。
                        mRecyclerView!!.scrollBy(0, dy)
                    } else {
                        val dx = nowPoint - offsetX
                        mRecyclerView!!.scrollBy(dx, 0)
                    }
                }
                mAnimator?.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        val pageIndex: Int = pageIndex
                        if (lastPageIndex != pageIndex) {
                            //回调监听
                            if (null != mOnPageChangeListener) {
                                mOnPageChangeListener!!.onPageChange(pageIndex)
                            }
                            lastPageIndex = pageIndex
                        }
                        //修复双击item bug
                        mRecyclerView!!.stopScroll()
                        startY = offsetY
                        startX = offsetX
                    }
                })
            } else {
                mAnimator!!.cancel()
                mAnimator!!.setIntValues(startPoint, endPoint)
            }
            mAnimator!!.start()
            return true
        }
    }

    inner class PageOnScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            //newState==0表示滚动停止，此时需要处理回滚
            if (newState == 0 && mOrientation != ORIENTATION.NULL) {
                val move: Boolean
                var vX = 0
                var vY = 0
                if (mOrientation == ORIENTATION.VERTICAL) {
                    val absY = Math.abs(offsetY - startY)
                    //如果滑动的距离超过屏幕的一半表示需要滑动到下一页
                    move = absY > recyclerView.height / 2
                    vY = 0
                    if (move) {
                        vY = if (offsetY - startY < 0) -1000 else 1000
                    }
                } else {
                    val absX = Math.abs(offsetX - startX)
                    move = absX > recyclerView.width / 2
                    if (move) {
                        vX = if (offsetX - startX < 0) -1000 else 1000
                    }
                }
                mOnFlingListener.onFling(vX, vY)
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            //滚动结束记录滚动的偏移量
            offsetY += dy
            offsetX += dx
        }
    }

    inner class MyOnTouchListener : OnTouchListener {
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            //手指按下的时候记录开始滚动的坐标
            if (firstTouch) {
                //第一次touch可能是ACTION_MOVE或ACTION_DOWN,所以使用这种方式判断
                firstTouch = false
                startY = offsetY
                startX = offsetX
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                firstTouch = true
            }
            return false
        }
    }

    private val pageIndex: Int
        private get() {
            var p = 0
            if (mRecyclerView!!.height == 0 || mRecyclerView!!.width == 0) {
                return p
            }
            p =
                if (mOrientation == ORIENTATION.VERTICAL) {
                    offsetY / mRecyclerView!!.height
                } else {
                    offsetX / mRecyclerView!!.width
                }
            return p
        }
    private val startPageIndex: Int
        private get() {
            var p = 0
            if (mRecyclerView!!.height == 0 || mRecyclerView!!.width == 0) {
                //没有宽高无法处理
                return p
            }
            p =
                if (mOrientation == ORIENTATION.VERTICAL) {
                    startY / mRecyclerView!!.height
                } else {
                    startX / mRecyclerView!!.width
                }
            return p
        }

    /**
     * set page change listener
     * @param listener
     */
    fun setOnPageChangeListener(listener: onPageChangeListener?) {
        mOnPageChangeListener = listener
    }

    interface onPageChangeListener {
        fun onPageChange(index: Int)
    }
}