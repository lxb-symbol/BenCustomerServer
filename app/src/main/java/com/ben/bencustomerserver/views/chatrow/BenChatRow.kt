package com.ben.bencustomerserver.views.chatrow

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.adapter.BenBaseAdapter
import com.ben.bencustomerserver.listener.BenCallBack
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.Direct
import com.ben.bencustomerserver.model.MessageStatus
import com.ben.bencustomerserver.utils.BenDateUtils
import com.ben.bencustomerserver.utils.MMkvTool
import com.ben.lib_picture_selector.ImageLoaderUtils
import java.util.Date

/**
 * base chat row view
 */
abstract class BenChatRow : LinearLayout {
    protected var inflater: LayoutInflater

    /**
     * ListView's adapter or RecyclerView's adapter
     */
    protected var adapter: Any? = null
    protected var message: BaseMessageModel? = null

    /**
     * message's position in list
     */
    protected var position = 0

    /**
     * timestamp
     */
    protected var timeStampView: TextView? = null

    /**
     * avatar
     */
    protected var userAvatarView: ImageView? = null

    /**
     * bubble
     */
    protected var bubbleLayout: View? = null

    /**
     * nickname
     */
    protected var usernickView: TextView? = null

    /**
     * percent
     */
    protected var percentageView: TextView? = null

    /**
     * progress
     */
    protected var progressBar: ProgressBar? = null

    /**
     * status
     */
    protected var statusView: ImageView? = null

    /**
     * if asked
     */
    protected var ackedView: TextView? = null

    /**
     * if delivered
     */
    protected var deliveredView: TextView? = null
    /**
     * 是否是发送者
     *
     * @return
     */
    /**
     * if is sender
     */
    var isSender: Boolean
        protected set

    /**
     * normal along with [.isSender]
     *  本 app 中弃用
     */
    private var showSenderType = false

    /**
     * chat message callback
     */
    protected var chatCallback: BenChatCallback? = null

    /**
     * switch to main thread
     */
    private var mainThreadHandler: Handler? = null
    protected var itemClickListener: MessageListItemClickListener? = null
    private var itemActionCallback: BenChatRowActionCallback? = null

    constructor(context: Context, isSender: Boolean) : super(context) {
        this.isSender = isSender
        inflater = LayoutInflater.from(context)
        initView()
    }

    constructor(context: Context, message: BaseMessageModel?, position: Int, adapter: Any?) : super(
        context
    ) {
        this.message = message
        isSender = message?.direct == Direct.SEND
        this.position = position
        this.adapter = adapter
        inflater = LayoutInflater.from(context)
        initView()
    }

    override fun onDetachedFromWindow() {
        itemActionCallback!!.onDetachedFromWindow()
        super.onDetachedFromWindow()
    }

    private fun initView() {
        showSenderType = isSender
        onInflateView()
        //添加reacionView
//        addReactionView()
        //添加threadView
//        addThreadView()
        timeStampView = findViewById<View>(R.id.timestamp) as TextView?
        userAvatarView = findViewById<View>(R.id.iv_userhead) as ImageView?
        bubbleLayout = findViewById<View>(R.id.bubble)
        usernickView = findViewById<View>(R.id.tv_userid) as TextView?
        progressBar = findViewById<View>(R.id.progress_bar) as ProgressBar?
        statusView = findViewById<View>(R.id.msg_status) as ImageView?
        ackedView = findViewById<View>(R.id.tv_ack) as TextView?
        deliveredView = findViewById<View>(R.id.tv_delivered) as TextView?
//        reactionView = findViewById(R.id.reaction_view)

//        setLayoutStyle();
        mainThreadHandler = Handler(Looper.getMainLooper())
        onFindViewById()
    }

//    private fun addThreadView() {
//        val view: View =
//            LayoutInflater.from(context).inflate(R.layout.circle_message_thread, this, false)
//        addView(view)
//    }

//    private fun addReactionView() {
//        orientation = VERTICAL
//        val view: View =
//            LayoutInflater.from(context).inflate(R.layout.circle_message_reaction, this, false)
//        addView(view)
//    }

    fun resetViewState() {
        if (null != progressBar) {
            progressBar!!.visibility = GONE
        }
        if (null != statusView) {
            statusView!!.visibility = GONE
        }
        if (null != ackedView) {
            ackedView!!.visibility = GONE
        }
        if (null != deliveredView) {
            deliveredView!!.visibility = GONE
        }
    }
    //    protected void setLayoutStyle() {
    //        BenChatItemStyleHelper helper = getItemStyleHelper();
    //        if (helper != null) {
    //            BenChatSetStyle itemStyle = helper.getStyle(context);
    //            if (bubbleLayout != null) {
    //                try {
    //                    if (isSender()) {
    //                        Drawable senderBgDrawable = itemStyle.getSenderBgDrawable();
    //                        if (senderBgDrawable != null) {
    //                            bubbleLayout.setBackground(senderBgDrawable.getConstantState().newDrawable());
    //                        }
    //                    } else {
    //                        Drawable receiverBgDrawable = itemStyle.getReceiverBgDrawable();
    //                        if (receiverBgDrawable != null) {
    //                            bubbleLayout.setBackground(receiverBgDrawable.getConstantState().newDrawable());
    //                        }
    //                    }
    //                } catch (Exception e) {
    //                    e.printStackTrace();
    //                }
    //            }
    //            if (timeStampView != null) {
    //                if (itemStyle.getTimeBgDrawable() != null) {
    //                    timeStampView.setBackground(itemStyle.getTimeBgDrawable().getConstantState().newDrawable());
    //                }
    //                if (itemStyle.getTimeTextSize() != 0) {
    //                    timeStampView.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemStyle.getTimeTextSize());
    //                }
    //                if (itemStyle.getTimeTextColor() != 0) {
    //                    timeStampView.setTextColor(itemStyle.getTimeTextColor());
    //                }
    //            }
    //            TextView content = findViewById(R.id.tv_chatcontent);
    //            if (content != null) {
    //                if (itemStyle.getTextSize() != 0) {
    //                    content.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemStyle.getTextSize());
    //                }
    //                if (itemStyle.getTextColor() != 0) {
    //                    content.setTextColor(itemStyle.getTextColor());
    //                }
    //            }
    //        }
    //    }
    /**
     * update view
     *
     * @param msg
     */
    fun updateView(msg: BaseMessageModel) {
        if (chatCallback == null) {
            chatCallback = BenChatCallback()
        }
        msg.messageStatusCallback = chatCallback as BenChatCallback
        onViewUpdate(msg)
    }

    /**
     * set property according message and position
     * the method should be called by child
     *
     * @param message
     * @param position
     */
    fun setUpView(
        message: BaseMessageModel?, position: Int,
        itemClickListener: MessageListItemClickListener?,
        itemActionCallback: BenChatRowActionCallback?
    ) {
        this.message = message
        this.position = position
        this.itemClickListener = itemClickListener
        this.itemActionCallback = itemActionCallback
        setUpBaseView()
        onSetUpView()
        onSetUpReactionView()
        onSetupThreadView()
        //setLayoutStyle();
        setClickListener()
    }

    private fun onSetupThreadView() {
//        if (threadRegion != null) {
//            if (shouldShowThreadRegion()) {
//                threadRegion.setVisibility(VISIBLE)
//                threadRegion.setThreadInfo(message.getChatThread())
//            } else {
//                threadRegion.setVisibility(GONE)
//            }
//        }
    }

    /**
     * If need to show thread region
     * @return
     */
    fun shouldShowThreadRegion(): Boolean {
        return false
    }

    protected fun onSetUpReactionView() {
//
//        reactionView.updateMessageInfo(message)
//        reactionView.setOnReactionItemListener(object : OnReactionItemListener() {
//            fun removeReaction(reactionEntity: BenReactionEmojiconEntity?) {
//                if (itemClickListener != null) {
//                    itemClickListener.onRemoveReaction(message, reactionEntity)
//                }
//            }
//
//            fun addReaction(reactionEntity: BenReactionEmojiconEntity?) {
//                if (itemClickListener != null) {
//                    itemClickListener.onAddReaction(message, reactionEntity)
//                }
//            }
//        })
//        reactionView.setOnItemClickListener(object : OnItemClickListener() {
//            fun onItemClick(view: View?, position: Int) {
//                if (itemClickListener != null) {
//                    itemClickListener!!.onBubbleLongClick(view, message)
//                }
//            }
//        })
    }

    /**
     * set timestamp, avatar, nickname and so on
     */
    private fun setUpBaseView() {
        val timestamp = findViewById<View>(R.id.timestamp) as TextView
        timestamp.let { setTimestamp(it) }
        setItemStyle()
        if (userAvatarView != null) {
            setAvatarAndNick()
        }
//  TODO 判断是否回应了
//        EMClient.getInstance().getOptions().getRequireDeliveryAck()
        if (true) {
            if (deliveredView != null && isSender) {
                if (message?.delivered == true) {
                    deliveredView!!.visibility = VISIBLE
                } else {
                    deliveredView!!.visibility = INVISIBLE
                }
            }
        }
//        EMClient.getInstance().getOptions().getRequireAck()
        if (true) {
            if (ackedView != null && isSender) {
                if (message?.acked == true) {
                    if (deliveredView != null) {
                        deliveredView!!.visibility = INVISIBLE
                    }
                    ackedView!!.visibility = VISIBLE
                } else {
                    ackedView!!.visibility = INVISIBLE
                }
            }
        }
    }

    /**
     * set item's style by easeMessageListItemStyle
     */
    private fun setItemStyle() {
//        val helper: BenChatItemStyleHelper = itemStyleHelper
//        if (helper != null) {
//            val itemStyle: BenChatSetStyle = helper.getStyle(context)
//            if (userAvatarView != null) {
//                setAvatarOptions(itemStyle)
//            }
//            if (usernickView != null) {
//                //如果在同一侧展示，则需要显示昵称
//                if (itemStyle.getItemShowType() === 1 || itemStyle.getItemShowType() === 2) {
//                    usernickView!!.visibility = VISIBLE
//                } else {
//                    //如果不在同一侧的话，则根据判断是否显示昵称
//                    usernickView!!.visibility =
//                        if (itemStyle.isShowNickname() && message!!.direct() === Direct.RECEIVE) VISIBLE else GONE
//                }
//            }
//            if (bubbleLayout != null) {
//                if (message.getType() === MessageType.TXT) {
//                    if (itemStyle.getItemMinHeight() !== 0) {
//                        bubbleLayout!!.minimumHeight = itemStyle.getItemMinHeight()
//                    }
//                }
//            }
//        }
    }

//    private val itemStyleHelper: BenChatItemStyleHelper
//        private get() = BenChatItemStyleHelper.getInstance()

    /**
     * set avatar options
     *
     * @param itemStyle
     */
//    protected fun setAvatarOptions(itemStyle: BenChatSetStyle) {
//        if (itemStyle.isShowAvatar()) {
//            userAvatarView!!.visibility = VISIBLE
//            if (userAvatarView is BenImageView) {
//                val avatarView: BenImageView? = userAvatarView as BenImageView?
//                if (itemStyle.getAvatarDefaultSrc() != null) {
//                    avatarView.setImageDrawable(itemStyle.getAvatarDefaultSrc())
//                }
//                avatarView.setShapeType(itemStyle.getShapeType())
//                if (itemStyle.getAvatarSize() !== 0) {
//                    val params: ViewGroup.LayoutParams = avatarView.getLayoutParams()
//                    params.width = itemStyle.getAvatarSize()
//                    params.height = itemStyle.getAvatarSize()
//                }
//                if (itemStyle.getBorderWidth() !== 0) {
//                    avatarView.setBorderWidth(itemStyle.getBorderWidth() as Int)
//                }
//                if (itemStyle.getBorderColor() !== 0) {
//                    avatarView.setBorderColor(itemStyle.getBorderColor())
//                }
//                if (itemStyle.getAvatarRadius() !== 0) {
//                    avatarView.setRadius(itemStyle.getAvatarRadius() as Int)
//                }
//            }
//            val avatarOptions: BenAvatarOptions = provideAvatarOptions()
//            if (avatarOptions != null && userAvatarView is BenImageView) {
//                val avatarView: BenImageView? = userAvatarView as BenImageView?
//                if (avatarOptions.getAvatarShape() !== 0) avatarView.setShapeType(avatarOptions.getAvatarShape())
//                if (avatarOptions.getAvatarBorderWidth() !== 0) avatarView.setBorderWidth(
//                    avatarOptions.getAvatarBorderWidth()
//                )
//                if (avatarOptions.getAvatarBorderColor() !== 0) avatarView.setBorderColor(
//                    avatarOptions.getAvatarBorderColor()
//                )
//                if (avatarOptions.getAvatarRadius() !== 0) avatarView.setRadius(avatarOptions.getAvatarRadius())
//            }
//        } else {
//            userAvatarView!!.visibility = GONE
//        }
//    }

    /**
     * @return
     */
//    protected fun provideAvatarOptions(): BenAvatarOptions {
//        return BenIM.getInstance().getAvatarOptions()
//    }

    /**
     * set avatar and nickname
     */
    protected fun setAvatarAndNick() {
        Log.i("symbol-7", "sendAvatarAndNick isSender: $isSender")
        if (!MMkvTool.getIsHuman()) {// 机器人聊天
            if (isSender) {// 发送方
                ImageLoaderUtils.load(context, userAvatarView, message?.from_avatar)
            } else {
                ImageLoaderUtils.load(context, userAvatarView, R.drawable.icon_head_bolt)
            }
        } else {
            if (isSender) {
                ImageLoaderUtils.load(context, userAvatarView, message?.from_avatar)
            } else {
                ImageLoaderUtils.load(context, userAvatarView, message?.to_avatar)
            }
        }
    }

    /**
     * set timestamp
     *
     * @param timestamp
     */
    protected fun setTimestamp(timestamp: TextView) {
        if (adapter != null) {
            timestamp.text = message?.msgTime?.let { Date(it) }?.let {
                BenDateUtils.getTimestampString(
                    context,
                    it
                )
            }
            timestamp.visibility = VISIBLE
            // show time stamp if interval with last message is > 30 seconds
            var prevMessage: BaseMessageModel? = null
            if (adapter is BaseAdapter) {
                prevMessage = (adapter as BaseAdapter).getItem(position - 1) as BaseMessageModel
            }
            if (adapter is BenBaseAdapter<*>) {
                prevMessage =
                    (adapter as BenBaseAdapter<*>).getItem(position - 1) as BaseMessageModel
            }

        }
    }

    fun setTimestamp(preMessage: BaseMessageModel?) {
        timeStampView?.text = message?.let { Date(it.msgTime) }?.let {
            BenDateUtils.getTimestampString(
                context,
                it
            )
        }
        timeStampView!!.visibility = VISIBLE
    }

    /**
     * set click listener
     */
    private fun setClickListener() {
        chatCallback = BenChatCallback()
        if (bubbleLayout != null) {
            bubbleLayout!!.setOnClickListener(OnClickListener {
                if (itemClickListener != null && itemClickListener!!.onBubbleClick(message)) {
                    return@OnClickListener
                }
                if (itemActionCallback != null) {
                    itemActionCallback!!.onBubbleClick(message)
                }
            })
            bubbleLayout!!.setOnLongClickListener { v ->
                if (itemClickListener != null) {
                    itemClickListener!!.onBubbleLongClick(v, message)
                } else false
            }
        }
        if (statusView != null) {
            statusView!!.setOnClickListener(OnClickListener {
                if (itemClickListener != null && itemClickListener!!.onResendClick(message)) {
                    return@OnClickListener
                }
                if (itemActionCallback != null) {
                    itemActionCallback!!.onResendClick(message)
                }
            })
        }
        if (userAvatarView != null) {
            userAvatarView!!.setOnClickListener {
//                if (itemClickListener != null) {
//                    if (message!!.direct === Direct.SEND) {
//                        itemClickListener!!.onUserAvatarClick(
//                            // TODO 返回用户名字
//                            "symbol"
//                        )
//                    } else {
//                        itemClickListener!!.onUserAvatarClick(message?.from_id)
//                    }
//                }
            }
            userAvatarView!!.setOnLongClickListener(OnLongClickListener {
//                if (itemClickListener != null) {
//                    if (message!!.direct === Direct.SEND) {
//                        itemClickListener!!.onUserAvatarLongClick(
////                            todo
////                            EMClient.getInstance().getCurrentUser()
//                            "symbol"
//                        )
//                    } else {
//                        itemClickListener!!.onUserAvatarLongClick(message?.from_id)
//                    }
//                    return@OnLongClickListener true
//                }
                false
            })
        }
    }

    /**
     * refresh view when message status change
     */
    protected open fun onViewUpdate(msg: BaseMessageModel) {
        when (msg.status) {
            MessageStatus.CREATE -> {
                onMessageCreate()
                if (itemClickListener != null) {
                    itemClickListener!!.onMessageCreate(msg)
                }
            }

            MessageStatus.SUCCESS -> onMessageSuccess()
            MessageStatus.FAIL -> onMessageError()
            MessageStatus.INPROGRESS -> onMessageInProgress()
            else -> Log.i(TAG, "default")
        }
    }

    inner class BenChatCallback : BenCallBack {
        override fun onSuccess() {
            mainThreadHandler!!.post {
                onMessageSuccess()
                if (itemClickListener != null) {
                    itemClickListener!!.onMessageSuccess(message)
                }
            }
        }

        override fun onError(code: Int, error: String?) {
            mainThreadHandler!!.post {
                onMessageError()
                if (itemClickListener != null) {
                    itemClickListener!!.onMessageError(message, code, error)
                }
            }
        }

        override fun onProgress(progress: Int, status: String?) {
            mainThreadHandler!!.post {
                onMessageInProgress()
                if (itemClickListener != null) {
                    itemClickListener!!.onMessageInProgress(message, progress)
                }
            }
        }
    }

    /**
     * message create status
     */
    protected open fun onMessageCreate() {
        Log.i(TAG, "onMessageCreate")
    }

    /**
     * message success status
     */
    protected open fun onMessageSuccess() {
        Log.i(TAG, "onMessageSuccess")
    }

    /**
     * message fail status
     */
    protected open fun onMessageError() {
        if (ackedView != null) {
            ackedView!!.visibility = INVISIBLE
        }
        if (deliveredView != null) {
            deliveredView!!.visibility = INVISIBLE
        }
        Log.e(TAG, "onMessageError")
    }

    /**
     * message in progress status
     */
    protected open fun onMessageInProgress() {
        Log.i(TAG, "onMessageInProgress")
    }

    /**
     * inflate view, child should implement it
     */
    protected abstract fun onInflateView()

    /**
     * find view by id
     */
    protected abstract fun onFindViewById()

    /**
     * setup view
     */
    protected abstract fun onSetUpView()

    /**
     * row action call back
     */
    interface BenChatRowActionCallback {
        /**
         * click resend action
         *
         * @param message
         */
        fun onResendClick(message: BaseMessageModel?)

        /**
         * click bubble layout
         *
         * @param message
         */
        fun onBubbleClick(message: BaseMessageModel?)

        /**
         * when view detach from window
         */
        fun onDetachedFromWindow()
    }

    companion object {
        protected val TAG = BenChatRow::class.java.simpleName
    }
}