package com.ben.bencustomerserver.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.adapter.BenBaseDelegateAdapter
import com.ben.bencustomerserver.adapter.BenMessageAdapter
import com.ben.bencustomerserver.connnect.RecieveMessageManager
import com.ben.bencustomerserver.listener.IChatMessageItemSet
import com.ben.bencustomerserver.listener.IChatMessageListLayout
import com.ben.bencustomerserver.listener.IChatMessageListView
import com.ben.bencustomerserver.listener.IRecyclerViewHandle
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.listener.OnItemClickListener
import com.ben.bencustomerserver.manager.BenMessageTypeSetManager
import com.ben.bencustomerserver.manager.BenThreadManager.Companion.instance
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.BenReactionEmojiconEntity
import com.ben.bencustomerserver.model.SearchDirection
import com.ben.bencustomerserver.presenter.BenChatMessagePresenter
import com.ben.bencustomerserver.presenter.BenChatMessagePresenterImpl

class BenChatMessageListLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), IChatMessageListView, IRecyclerViewHandle,
    IChatMessageItemSet, IChatMessageListLayout {
    private var presenter: BenChatMessagePresenter?
    override var messageAdapter: BenMessageAdapter? = null
        private set
    private var baseAdapter: ConcatAdapter? = null

    /**
     * 加载数据的方式，目前有三种，常规模式（从本地加载），漫游模式，查询历史消息模式（通过数据库搜索）
     */
    private var loadDataType: LoadDataType? = null

    /**
     * 消息id，一般是搜索历史消息时会用到这个参数
     */
    private var msgId: String? = null
    private var pageSize = DEFAULT_PAGE_SIZE
    private lateinit var rvList: RecyclerView
    private lateinit var srlRefresh: SwipeRefreshLayout
    private lateinit var layoutManager: LinearLayoutManager

    /**
     * 另一侧的id
     */
    private var username: String? = null
    private var canUseRefresh = true
    private var loadMoreStatus: LoadMoreStatus? = null
    private var messageTouchListener: OnMessageTouchListener? = null
    private var errorListener: OnChatErrorListener? = null

    /**
     * 上一次控件的高度
     */
    private var recyclerViewLastHeight = 0

    /**
     * 条目具体控件的点击事件
     */
    private var messageListItemClickListener: MessageListItemClickListener? = null
    private var isChannel = false

    /**
     * When is thread conversation, whether thread message list has reached the latest message
     */
    private var isReachedLatestThreadMessage = false
    private var messageCursor: String? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.ben_chat_message_list, this)
        presenter = BenChatMessagePresenterImpl()
        if (context is AppCompatActivity) {
            context.lifecycle.addObserver(presenter!!)
        }
        initAttrs(context, attrs)
        initViews()
    }

    var viewModel: ViewModel? = null

    fun setupViewModel(vm: ViewModel) {
        viewModel = vm
        presenter?.setupViewModel(vm)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.BenChatMessageListLayout)
            val textSize = a.getDimension(
                R.styleable.BenChatMessageListLayout_ben_chat_item_text_size, 0f
            )
            val textColorRes =
                a.getResourceId(R.styleable.BenChatMessageListLayout_ben_chat_item_text_color, -1)
            var textColor: Int
            textColor = if (textColorRes != -1) {
                ContextCompat.getColor(context, textColorRes)
            } else {
                a.getColor(R.styleable.BenChatMessageListLayout_ben_chat_item_text_color, 0)
            }
            val itemMinHeight =
                a.getDimension(R.styleable.BenChatMessageListLayout_ben_chat_item_min_height, 0f)
            val timeTextSize = a.getDimension(
                R.styleable.BenChatMessageListLayout_ben_chat_item_time_text_size,
                0f
            )
            val timeTextColorRes = a.getResourceId(
                R.styleable.BenChatMessageListLayout_ben_chat_item_time_text_color,
                -1
            )
            val timeTextColor: Int
            timeTextColor = if (timeTextColorRes != -1) {
                ContextCompat.getColor(context, textColorRes)
            } else {
                a.getColor(R.styleable.BenChatMessageListLayout_ben_chat_item_time_text_color, 0)
            }
            val avatarDefaultDrawable =
                a.getDrawable(R.styleable.BenChatMessageListLayout_ben_chat_item_avatar_default_src)
            val shapeType = a.getInteger(
                R.styleable.BenChatMessageListLayout_ben_chat_item_avatar_shape_type,
                0
            )
            a.recycle()
        }
    }

    private fun initViews() {
        presenter!!.attachView(this)
        rvList = findViewById(R.id.message_list)
        srlRefresh = findViewById(R.id.srl_refresh)
        srlRefresh.isEnabled = (canUseRefresh)
        layoutManager = LinearLayoutManager(context)
        rvList.layoutManager = layoutManager
        baseAdapter = ConcatAdapter()
        messageAdapter = BenMessageAdapter()
        baseAdapter!!.addAdapter(messageAdapter!!)
        rvList.adapter = baseAdapter
        registerChatType()
        initListener()
    }

    private fun registerChatType() {
        messageAdapter?.let {
            BenMessageTypeSetManager.instance?.registerMessageType(it as BenBaseDelegateAdapter<Any>)
        }
    }

    fun init(loadDataType: LoadDataType?, username: String?) {
        this.username = username
        this.loadDataType = loadDataType
        this.loadDataType = LoadDataType.ROAM
        // If it is thread conversation, should not use refresh animator
        if (this.loadDataType == LoadDataType.THREAD) {
            srlRefresh.isEnabled = false
        }
    }

    fun init(username: String?) {
        init(LoadDataType.ROAM, username)
    }

    fun loadDefaultData() {
        loadData(pageSize, null)
    }

    fun loadData(msgId: String?) {
        loadData(pageSize, msgId)
    }

    fun loadData(pageSize: Int, msgId: String?) {
        this.pageSize = pageSize
        this.msgId = msgId
        checkConType()
    }

    private fun checkConType() {
        loadData()
    }

    private fun loadData() {
        if (loadDataType == LoadDataType.ROAM) {
            presenter!!.loadServerMessages(pageSize)
        }
    }

    fun onRefreshData() {
        if (loadDataType != LoadDataType.THREAD) {
            loadMorePreviousData()
        }
    }

    /**
     * 加载更多的更早一些的数据，下拉加载更多
     */
    fun loadMorePreviousData() {
        val msgId = listFirstMessageId
        if (loadDataType == LoadDataType.ROAM) {
            presenter!!.loadMoreServerMessages(msgId, pageSize)
        } else {
            presenter!!.loadMoreLocalMessages(msgId, pageSize)
        }
    }

    /**
     * 专用于加载更多的更新一些的数据，上拉加载更多时使用
     */
    private fun loadMoreHistoryData() {
        val msgId = listLastMessageId
        if (loadDataType == LoadDataType.HISTORY) {
            loadMoreStatus = LoadMoreStatus.HAS_MORE

        }
    }

    private val listFirstMessageId: String?
        /**
         * 获取列表最上面的一条消息的id
         *
         * @return
         */
        get() {
            var message: BaseMessageModel? = null
            try {
                message = messageAdapter!!.data!![0]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return message?.msgId
        }
    private val listLastMessageId: String?
        /**
         * 获取列表最下面的一条消息的id
         *
         * @return
         */
        get() {
            var message: BaseMessageModel? = null
            try {
                message = messageAdapter!!.data!![messageAdapter!!.data!!.size - 1]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return if (message == null) null else message.msgId
        }
    val isChatRoomCon: Boolean
        get() = false
    val isGroupChat: Boolean
        get() = false
    private val isSingleChat: Boolean
        private get() = true

    private fun initListener() {
        srlRefresh.setOnRefreshListener { onRefreshData() }
        rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //判断状态及是否还有更多数据
                    if (!rvList.canScrollVertically(1)) {
                        if (messageTouchListener != null) {
                            messageTouchListener!!.onReachBottom()
                        }
                    }
                    if (loadMoreStatus == LoadMoreStatus.HAS_MORE && layoutManager.findLastVisibleItemPosition() != 0 && layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount - 1) {
                        loadMoreData()
                    }
                } else {
                    //if recyclerView not idle should hide keyboard
                    if (messageTouchListener != null) {
                        messageTouchListener!!.onViewDragging()
                    }
                }
            }
        })

        //用于监听RecyclerView高度的变化，从而刷新列表
        rvList.viewTreeObserver.addOnGlobalLayoutListener {
            val height = rvList.height
            if (recyclerViewLastHeight == 0) {
                recyclerViewLastHeight = height
            }
            if (recyclerViewLastHeight != height) {
                //RecyclerView高度发生变化，刷新页面
                if (messageAdapter!!.data != null && !messageAdapter!!.data!!.isEmpty()) {
                    post { smoothSeekToPosition(messageAdapter!!.data!!.size - 1) }
                }
            }
            recyclerViewLastHeight = height
        }
        messageAdapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                if (messageTouchListener != null) {
                    messageTouchListener!!.onTouchItemOutside(view, position)
                }
            }
        })
        messageAdapter!!.setListItemClickListener(object : MessageListItemClickListener {
            override fun onBubbleClick(message: BaseMessageModel?): Boolean {
                return if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onBubbleClick(message)
                } else false
            }

            override fun onResendClick(message: BaseMessageModel?): Boolean {
                return if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onResendClick(message)
                } else false
            }

            override fun onBubbleLongClick(v: View?, message: BaseMessageModel?): Boolean {
                return if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onBubbleLongClick(v, message)
                } else false
            }

            override fun onUserAvatarClick(username: String?) {
                if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onUserAvatarClick(username)
                }
            }

            override fun onUserAvatarLongClick(username: String?) {
                if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onUserAvatarLongClick(username)
                }
            }

            override fun onMessageCreate(message: BaseMessageModel?) {
                if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onMessageCreate(message)
                }
            }

            override fun onMessageSuccess(message: BaseMessageModel?) {
                if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onMessageSuccess(message)
                }
            }

            override fun onMessageError(message: BaseMessageModel?, code: Int, error: String?) {
                if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onMessageError(message, code, error)
                }
            }

            override fun onMessageInProgress(message: BaseMessageModel?, progress: Int) {
                if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onMessageInProgress(message, progress)
                }
            }

            override fun onThreadClick(
                messageId: String?,
                threadId: String?,
                parentId: String?
            ): Boolean {
                return if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onThreadClick(messageId, threadId, parentId)
                } else super.onThreadClick(
                    messageId,
                    threadId,
                    parentId
                )
            }

            override fun onThreadLongClick(
                v: View?,
                messageId: String?,
                threadId: String?,
                parentId: String?
            ): Boolean {
                return if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onThreadLongClick(
                        v,
                        messageId,
                        threadId,
                        parentId
                    )
                } else super.onThreadLongClick(
                    v,
                    messageId,
                    threadId,
                    parentId
                )
            }

            fun onAddReaction(
                message: BaseMessageModel?,
                reactionEntity: BenReactionEmojiconEntity?
            ) {
//                if (messageListItemClickListener != null) {
//                    messageListItemClickListener.onAddReaction(message, reactionEntity)
//                }
            }

            fun onRemoveReaction(
                message: BaseMessageModel?,
                reactionEntity: BenReactionEmojiconEntity?
            ) {
//                if (messageListItemClickListener != null) {
//                    messageListItemClickListener.onRemoveReaction(message, reactionEntity)
//                }
            }
        })
    }

    private fun loadMoreData() {
        if (loadDataType == LoadDataType.HISTORY) {
            loadMoreHistoryData()
        } else if (loadDataType == LoadDataType.THREAD) {
            loadMoreThreadMessages()
        }
    }

    fun loadMoreThreadMessages() {
        presenter!!.loadMoreServerMessages(
            messageCursor,
            pageSize
        )
    }

    /**
     * 停止下拉动画
     */
    private fun finishRefresh() {
        if (presenter!!.isActive) {
            runOnUi {
                if (srlRefresh != null) {
                    srlRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun notifyDataSetChanged() {
        messageAdapter!!.notifyDataSetChanged()
    }

    /**
     * 设置数据
     *
     * @param data
     */
    fun setData(data: List<BaseMessageModel>) {
        messageAdapter!!.setData(data.toMutableList())
    }

    /**
     * 添加数据
     *
     * @param data
     */
    fun addData(data: List<BaseMessageModel>?) {

        messageAdapter?.addData(data?.toMutableList())
    }

    override fun context(): Context? {
        return context
    }


    override fun loadMsgFail(error: Int, message: String?) {
        finishRefresh()
        if (errorListener != null) {
            errorListener!!.onChatError(error, message)
        }
    }

    override fun loadLocalMsgSuccess(data: List<BaseMessageModel>?) {
        refreshToLatest()
    }

    override fun loadNoLocalMsg() {}


    override fun loadMoreLocalMsgSuccess(data: List<BaseMessageModel>?) {
        finishRefresh()
        presenter!!.refreshCurrentConversation()
        post { smoothSeekToPosition(data?.size?.minus(1) ?: 0) }
    }

    override fun loadNoMoreLocalMsg() {
        finishRefresh()
    }

    override fun loadMoreLocalHistoryMsgSuccess(
        data: List<BaseMessageModel>?,
        direction: SearchDirection?
    ) {

    }

    fun loadMoreLocalHistoryMsgSuccess(
        data: List<BaseMessageModel>,
    ) {

    }

    override fun loadNoMoreLocalHistoryMsg() {
        finishRefresh()
    }


    override fun loadServerMsgSuccess(data: List<BaseMessageModel>?, cursor: String?) {
        messageCursor = cursor
        if (loadDataType == LoadDataType.THREAD) {
            loadMoreStatus = if (data?.size!! >= pageSize || !TextUtils.isEmpty(cursor)) {
                LoadMoreStatus.HAS_MORE
            } else {
                LoadMoreStatus.NO_MORE_DATA
            }
            presenter!!.refreshCurrentConversation()
        } else {
            presenter!!.refreshToLatest()
        }
    }

    override fun loadMoreServerMsgSuccess(data: List<BaseMessageModel>?, cursor: String?) {
        messageCursor = cursor
        finishRefresh()
        presenter!!.refreshCurrentConversation()
        if (loadDataType == LoadDataType.THREAD) {
            loadMoreStatus = if (data?.size!! >= pageSize || !TextUtils.isEmpty(cursor)) {
                LoadMoreStatus.HAS_MORE
            } else {
                LoadMoreStatus.NO_MORE_DATA
            }
        } else {
            post { smoothSeekToPosition(data?.size?.minus(1) ?: 0) }
        }
    }

    override fun refreshCurrentConSuccess(data: List<BaseMessageModel>?, toLatest: Boolean) {
        data?.toMutableList()?.let { messageAdapter!!.setData(it) }

        if (toLatest) {
            seekToPosition(data?.size?.minus(1) ?: 0)
        }
    }

    override fun insertMessageToLast(message: BaseMessageModel?) {
        message?.let {
            messageAdapter?.addData(it)
        }

        seekToPosition(messageAdapter!!.data!!.size - 1)
    }

    override fun reachedLatestThreadMessage() {
        isReachedLatestThreadMessage = true
    }

    override fun canUseDefaultRefresh(canUseRefresh: Boolean) {
        this.canUseRefresh = canUseRefresh
        srlRefresh.isEnabled = canUseRefresh
    }

    override fun refreshMessages() {
        presenter!!.refreshCurrentConversation()
    }

    override fun refreshToLatest() {
        presenter!!.refreshToLatest()
    }

    override fun refreshMessage(message: BaseMessageModel?) {
        val position = messageAdapter!!.data!!.lastIndexOf(message)
        if (position != -1) {
            runOnUi { messageAdapter!!.notifyItemChanged(position) }
        }
    }

    override fun refreshMessage(messageId: String?) {
        if (TextUtils.isEmpty(messageId)) {
            return
        }
        val message: BaseMessageModel? = RecieveMessageManager.msgs.find { it.msgId == messageId }
        if (message != null) {
            val position = messageAdapter!!.data!!.lastIndexOf(message)
            if (position != -1) {
                runOnUi { messageAdapter!!.notifyItemChanged(position) }
            }
        }
    }

    override fun removeMessage(message: BaseMessageModel?) {
        if (message == null || messageAdapter!!.data == null) {
            return
        }
        //symbol是否需要调用接口删除消息
        runOnUi {
            if (presenter!!.isActive) {
                val messages: MutableList<BaseMessageModel>? = messageAdapter?.data?.toMutableList()
                RecieveMessageManager.msgs.remove(message)
                val position = messages!!.lastIndexOf(message)
                if (position != -1) {
                    //需要保证条目从集合中删除
                    messages.removeAt(position)
                    //通知适配器删除条目
                    messageAdapter?.notifyItemRemoved(position)
                    //通知刷新下一条消息
                    messageAdapter?.notifyItemChanged(position)
                }
            }
        }
    }

    override fun moveToPosition(position: Int) {
        seekToPosition(position)
    }

    override fun lastMsgScrollToBottom(message: BaseMessageModel?) {
        val messages: List<BaseMessageModel>? = messageAdapter!!.data
        val position = messages!!.lastIndexOf(message)
        if (position != -1) {
            messageAdapter!!.notifyItemChanged(position)
            val isNoBottom = rvList.canScrollVertically(1)
            if (!isNoBottom) {
                val oldView = rvList.layoutManager!!
                    .findViewByPosition(messageAdapter!!.itemCount - 1)
                var oldHeight = 0
                if (oldView != null) {
                    oldHeight = oldView.measuredHeight
                }
                val finalOldHeight = oldHeight
                rvList.postDelayed({
                    val v = rvList.layoutManager!!
                        .findViewByPosition(messageAdapter!!.itemCount - 1)
                    var height = 0
                    if (v != null) {
                        height = v.measuredHeight
                    }
                    rvList.smoothScrollBy(0, height - finalOldHeight)
                }, 500)
            }
        }
    }

    override fun showNickname(showNickname: Boolean) {
        notifyDataSetChanged()
    }

    override fun setItemSenderBackground(bgDrawable: Drawable?) {
        notifyDataSetChanged()
    }

    override fun setItemReceiverBackground(bgDrawable: Drawable?) {
        notifyDataSetChanged()
    }

    override fun setItemTextSize(textSize: Int) {
        notifyDataSetChanged()
    }

    override fun setItemTextColor(textColor: Int) {
        notifyDataSetChanged()
    }

    override fun setTimeTextSize(textSize: Int) {
        notifyDataSetChanged()
    }

    override fun setTimeTextColor(textColor: Int) {
        notifyDataSetChanged()
    }

    override fun setTimeBackground(bgDrawable: Drawable?) {
        notifyDataSetChanged()
    }

    override fun setItemShowType(type: ShowType?) {
        if (!isSingleChat) {
            notifyDataSetChanged()
        }
    }

    override fun setAvatarDefaultSrc(src: Drawable?) {
        notifyDataSetChanged()
    }

    override fun setAvatarShapeType(shapeType: Int) {
        notifyDataSetChanged()
    }

    override fun addHeaderAdapter(adapter: RecyclerView.Adapter<*>?) {
        baseAdapter!!.addAdapter(0, adapter!!)
    }

    override fun addFooterAdapter(adapter: RecyclerView.Adapter<*>?) {
        baseAdapter!!.addAdapter(adapter!!)
    }

    override fun removeAdapter(adapter: RecyclerView.Adapter<*>?) {
        baseAdapter!!.removeAdapter(adapter!!)
    }

    override fun addRVItemDecoration(decor: RecyclerView.ItemDecoration) {
        rvList.addItemDecoration(decor)
    }

    override fun removeRVItemDecoration(decor: RecyclerView.ItemDecoration) {
        rvList.removeItemDecoration(decor)
    }

    /**
     * 是否有新的消息
     * 判断依据为：数据库中最新的一条数据的时间戳是否大于页面上的最新一条数据的时间戳
     *
     * @return
     */
    fun haveNewMessages(): Boolean {
        return if (messageAdapter == null || messageAdapter!!.data == null || messageAdapter!!.data!!.isEmpty()) {
            false
        } else false

        //TODO 判断是否有新消息
    }

    /**
     * 移动到指定位置
     *
     * @param position
     */
    private fun seekToPosition(position: Int) {
        var position = position
        if (presenter!!.isDestroy) {
            return
        }
        if (position < 0) {
            position = 0
        }
        val manager = rvList.layoutManager
        if (manager is LinearLayoutManager) {
            manager.scrollToPositionWithOffset(position, 0)
        }
    }

    /**
     * 移动到指定位置
     *
     * @param position
     */
    private fun smoothSeekToPosition(position: Int) {
        var position = position
        if (presenter!!.isDestroy) {
            return
        }
        if (position < 0) {
            position = 0
        }
        val manager = rvList.layoutManager
        if (manager is LinearLayoutManager) {
            manager.scrollToPositionWithOffset(position, 0)
            setMoveAnimation(manager, position)
        }
    }

    private fun setMoveAnimation(manager: RecyclerView.LayoutManager, position: Int) {
        val prePosition: Int = if (position > 0) {
            position - 1
        } else {
            position
        }
        val view = manager.findViewByPosition(0)
        val height: Int = view?.height ?: 200
        val animator = ValueAnimator.ofInt(-height, 0)
        animator.addUpdateListener { animation: ValueAnimator ->
            val value = animation.animatedValue as Int
            (manager as LinearLayoutManager).scrollToPositionWithOffset(prePosition, value)
        }
        animator.duration = 800
        animator.start()
    }

    override fun setPresenter(presenter: BenChatMessagePresenter?) {
        this.presenter = presenter
        if (context is AppCompatActivity) {
            (context as AppCompatActivity).lifecycle.addObserver(presenter!!)
        }
        this.presenter!!.attachView(this)
        // 和 conversation 进行关联缺少
    }

    override fun setOnMessageTouchListener(listener: OnMessageTouchListener?) {
        messageTouchListener = listener
    }

    override fun setOnChatErrorListener(listener: OnChatErrorListener?) {
        errorListener = listener
    }

    override fun setMessageListItemClickListener(listener: MessageListItemClickListener?) {
        messageListItemClickListener = listener
    }

    fun runOnUi(runnable: Runnable?) {
        instance!!.runOnMainThread(runnable)
    }

    fun setIsChannel(isChannel: Boolean) {
        this.isChannel = isChannel
    }

    /**
     * 三种数据加载模式，local是从本地数据库加载，Roam是开启消息漫游，History是搜索本地消息
     */
    enum class LoadDataType {
        LOCAL, ROAM, HISTORY, THREAD
    }

    /**
     * 加载更多的状态
     */
    enum class LoadMoreStatus {
        IS_LOADING, HAS_MORE, NO_MORE_DATA
    }

    /**
     * 条目的展示方式
     * normal区分发送方和接收方
     * left发送方和接收方在左侧
     * right发送方和接收方在右侧
     */
    enum class ShowType {
        NORMAL, LEFT /*, RIGHT*/
    }

    /**
     * 消息列表接口
     */
    interface OnMessageTouchListener {
        /**
         * touch事件
         *
         * @param v
         * @param position
         */
        fun onTouchItemOutside(v: View?, position: Int)

        /**
         * 控件正在被拖拽
         */
        fun onViewDragging()

        /**
         * RecyclerView scroll to bottom
         */
        fun onReachBottom()
    }

    interface OnChatErrorListener {
        /**
         * 聊天中错误信息
         *
         * @param code
         * @param errorMsg
         */
        fun onChatError(code: Int, errorMsg: String?)
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 10
        private val TAG = BenChatMessageListLayout::class.java.simpleName

        /**
         * 是否滑动到底部
         *
         * @param recyclerView
         * @return
         */
        fun isVisibleBottom(recyclerView: RecyclerView): Boolean {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
            //屏幕中最后一个可见子项的position
            val lastVisibleItemPosition = layoutManager!!.findLastVisibleItemPosition()
            //当前屏幕所看到的子项个数
            val visibleItemCount = layoutManager.childCount
            //当前RecyclerView的所有子项个数
            val totalItemCount = layoutManager.itemCount
            //RecyclerView的滑动状态
            val state = recyclerView.scrollState
            return visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == RecyclerView.SCROLL_STATE_IDLE
        }
    }
}