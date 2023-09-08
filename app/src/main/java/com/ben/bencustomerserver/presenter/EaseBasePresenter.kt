package com.ben.bencustomerserver.presenter

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.ben.bencustomerserver.listener.ILoadDataView
import com.ben.bencustomerserver.manager.EaseThreadManager

abstract class EaseBasePresenter : LifecycleObserver {
    /**
     * 生命周期正在销毁
     *
     * @return
     */
    var isDestroy = false
        private set
    protected var isChannel = false
    open var isThread = false
    abstract fun attachView(view: ILoadDataView?)
    abstract fun detachView()
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Log.i(TAG, "$this onCreate")
        isDestroy = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Log.i(TAG, "$this onStart")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Log.i(TAG, "$this onResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        Log.i(TAG, "$this onPause")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        Log.i(TAG, "$this onStop")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy() {
        Log.i(TAG, "$this onDestroy")
        isDestroy = true
    }

    val isActive: Boolean
        /**
         * 生命周期仍在活跃
         *
         * @return
         */
        get() = !isDestroy

    /**
     * 执行UI线程
     *
     * @param runnable
     */
    fun runOnUI(runnable: Runnable?) {
        EaseThreadManager.instance?.runOnMainThread(runnable)
    }

    /**
     * 执行IO异步线程
     *
     * @param runnable
     */
    fun runOnIO(runnable: Runnable?) {
        EaseThreadManager.instance?.runOnIOThread(runnable)
    }

    fun setIsChannel(isChannel: Boolean) {
        this.isChannel = isChannel
    }

    companion object {
        private val TAG = EaseBasePresenter::class.java.simpleName
    }
}