package com.ben.bencustomerserver.manager

import android.os.Handler
import android.os.Looper
import android.os.Process
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 作为线程的管理类，可以实现工作线程和主线程的切换
 */
class BenThreadManager private constructor() {
    private var mIOThreadExecutor: Executor? = null
    private var mMainThreadHandler: Handler? = null

    init {
        init()
    }

    private fun init() {
        val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
        val KEEP_ALIVE_TIME = 1
        val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS
        val taskQueue: BlockingQueue<Runnable> = LinkedBlockingDeque()
        mIOThreadExecutor = ThreadPoolExecutor(
            NUMBER_OF_CORES,
            NUMBER_OF_CORES * 2,
            KEEP_ALIVE_TIME.toLong(),
            KEEP_ALIVE_TIME_UNIT,
            taskQueue,
            BackgroundThreadFactory(Process.THREAD_PRIORITY_BACKGROUND)
        )
        mMainThreadHandler = Handler(Looper.getMainLooper())
    }

    /**
     * 在异步线程执行
     * @param runnable
     */
    fun runOnIOThread(runnable: Runnable?) {
        mIOThreadExecutor!!.execute(runnable)
    }

    /**
     * 在UI线程执行
     * @param runnable
     */
    fun runOnMainThread(runnable: Runnable?) {
        mMainThreadHandler!!.post(runnable!!)
    }

    val isMainThread: Boolean
        /**
         * 判断是否是主线程
         * @return true is main thread
         */
        get() = Looper.getMainLooper().thread === Thread.currentThread()

    companion object {
        @Volatile
        var instance: BenThreadManager? = null
            get() {
                if (field == null) {
                    synchronized(BenThreadManager::class.java) {
                        if (field == null) {
                            field = BenThreadManager()
                        }
                    }
                }
                return field
            }
            private set
    }
}