package com.ben.bencustomerserver.views

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.databinding.CsChatLayoutBinding
import com.ben.bencustomerserver.listener.ChatInputMenuListener
import com.ben.bencustomerserver.listener.IChatLayout
import com.ben.bencustomerserver.listener.IHandleMessageView
import com.ben.bencustomerserver.listener.IPopupWindow
import com.ben.bencustomerserver.listener.ISwitchHumenListener
import com.ben.bencustomerserver.listener.OnAddMsgAttrsBeforeSendEvent
import com.ben.bencustomerserver.listener.OnChatLayoutListener
import com.ben.bencustomerserver.listener.OnChatRecordTouchListener
import com.ben.bencustomerserver.listener.OnMenuChangeListener
import com.ben.bencustomerserver.listener.OnRecallMessageResultListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.presenter.BenHandleMessagePresenter
import com.ben.bencustomerserver.presenter.BenHandleMessagePresenterImpl
import com.ben.bencustomerserver.utils.MMkvTool
import com.ben.bencustomerserver.vm.ChatViewModel
import com.luck.picture.lib.utils.ToastUtils

/**
 * 聊天的布局
 */
class ChatLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), IHandleMessageView,
    IPopupWindow, ChatInputMenuListener, IChatLayout, ISwitchHumenListener,
    BenChatMessageListLayout.OnMessageTouchListener {
    private lateinit var mViewBinding: CsChatLayoutBinding
    private var chatInputMenu: BenChatInputMenu
    private lateinit var voiceRecordView: BenVoiceRecorderView
    var chatLayoutListener: OnChatLayoutListener? = null
    private lateinit var presenter: BenHandleMessagePresenter


    /**
     * "正在输入"功能的开关，打开后本设备发送消息将持续发送cmd类型消息通知对方"正在输入"
     */
    private var turnOnTyping = false

    /**
     * 用于处理用户是否正在输入的handler
     */
    private var typingHandler: Handler? = null

    override var menuHelper: BenPopupWindowHelper? = null

    private var clippborad: ClipboardManager

    private var listener: OnChatLayoutListener? = null

    private var isNotFirstSend: Boolean = false

    private var recordListener: OnChatRecordTouchListener? = null


    /**
     * 正在输入的心跳
     */
    val MSG_TYPING_HEARTBEAT = 0

    /**
     *正在编辑结束
     */
    val MSG_TYPING_END = 1

    /**
     * 对方正在编辑结束
     */
    val MSG_OTHER_TYPING_END = 2

    val ACTION_TYPING_BEGIN = "TypingBegin"

    val ACTION_TYPING_END = "TypingEnd"

    val TYPING_SHOW_TIME = 1000

    val OTHER_TYPING_SHOW_TIME = 5000


    init {
        mViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.cs_chat_layout,
            this,
            true
        )
        chatInputMenu = mViewBinding.layoutMenu
        menuHelper = BenPopupWindowHelper()
        mViewBinding.layoutMenu.menuListener = this
        clippborad = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        voiceRecordView = mViewBinding.voiceRecorder
        presenter = BenHandleMessagePresenterImpl()
        presenter.attachView(this)
        (presenter as BenHandleMessagePresenterImpl).addHttpListener()
        mViewBinding.chatMessageListLayout.setOnMessageTouchListener(this)
        initTypingHandler()
        chatInputMenu.switchHumanListener = this

        loadData()
    }

    fun loadData() {
        mViewBinding.chatMessageListLayout.loadDefaultData()
    }

    fun setUpViewModel(vm: ViewModel) {
        presenter.viewModel = vm
        mViewBinding.chatMessageListLayout.setupViewModel(vm)
    }


    private fun initTypingHandler() {
        typingHandler = Handler(Looper.getMainLooper()) {
            when (it.what) {
                MSG_TYPING_HEARTBEAT -> {
                    typingHandler?.let { it1 -> setTypingBeginMsg(it1) }
                    true
                }

                MSG_OTHER_TYPING_END -> {
                    typingHandler?.let { han ->
                        setTypingEndMsg(han)
                    }
                    true
                }

                MSG_TYPING_END -> {
                    typingHandler?.let { han2 -> setOtherTypingEnd(han2) }
                    true
                }

                else -> {
                    false
                }
            }
        }


    }


    private fun setOtherTypingEnd(handler: Handler) {
        if (!turnOnTyping) {
            return
        }
        // Only support single-chat type conversation.
        handler.removeMessages(MSG_OTHER_TYPING_END)
        if (listener != null) {
            listener!!.onOtherTyping(ACTION_TYPING_END)
        }
    }

    /**
     * 处理“正在输入”结束
     *
     * @param handler
     */
    private fun setTypingEndMsg(handler: Handler) {
        if (!turnOnTyping) return

        isNotFirstSend = false
        handler.removeMessages(MSG_TYPING_HEARTBEAT)
        handler.removeMessages(MSG_TYPING_END)
        // Send TYPING-END cmd msg
        presenter.sendCmdMessage(ACTION_TYPING_END)
    }


    /**
     * 处理“正在输入”开始
     *
     * @param handler
     */
    private fun setTypingBeginMsg(handler: Handler) {
        if (!turnOnTyping) return
        // Only support single-chat type conversation.
        // Send TYPING-BEGIN cmd msg
        presenter.sendCmdMessage(ACTION_TYPING_BEGIN)
        handler.sendEmptyMessageDelayed(
            MSG_TYPING_HEARTBEAT,
            TYPING_SHOW_TIME.toLong()
        )
    }


    override fun chatInputMenu(): BenChatInputMenu = chatInputMenu

    override fun inputContent(): String {
        return ""
    }

    override fun turnOnTypingMonitor(turnOn: Boolean) {
        turnOnTyping = turnOn
        if (!turnOn) isNotFirstSend = false
    }

    override fun sendTextMessage(content: String?) {
        content?.let {
            presenter.sendTextMessage(it)
        }
    }

    override fun sendTextMessage(content: String?, isNeedGroupAck: Boolean) {
        content?.let {
            presenter.sendTextMessage(it)
        }
    }

    override fun sendAtMessage(content: String?) {

    }

    override fun sendBigExpressionMessage(name: String?, identityCode: String?) {
        presenter.sendBigExpressionMessage(name, identityCode)
    }

    override fun sendVoiceMessage(filePath: String?, length: Int) {
        Log.e("TAG", "filePath: $filePath")
        val uri = Uri.parse(filePath)
        presenter.sendVoiceMessage(uri, length)
    }

    override fun sendVoiceMessage(filePath: Uri?, length: Int) {
        presenter.sendVoiceMessage(filePath, length)
    }

    override fun sendImageMessage(imageUri: Uri?) {
        presenter.sendImageMessage(imageUri)
    }

    override fun sendImageMessage(imageUri: Uri?, sendOriginalImage: Boolean) {
        presenter.sendImageMessage(imageUri)
    }

    override fun sendLocationMessage(
        latitude: Double,
        longitude: Double,
        locationAddress: String?,
        buildingName: String?
    ) {
        presenter.sendLocationMessage(latitude, longitude, locationAddress, buildingName)
    }

    override fun sendVideoMessage(videoUri: Uri?, videoLength: Int) {
        presenter.sendVideoMessage(videoUri, videoLength)
    }

    override fun sendFileMessage(fileUri: Uri?) {
        Log.e("symbol -->", "发送文件消息待完善")
        presenter.sendFileMessage(fileUri)
    }

    override fun addMessageAttributes(message: BaseMessageModel?) {

    }

    override fun sendMessage(message: BaseMessageModel?) {
        presenter.sendMessage(message)
    }

    override fun resendMessage(message: BaseMessageModel?) {
        presenter.resendMessage(message)
    }

    override fun deleteMessage(message: BaseMessageModel?) {
        mViewBinding.chatMessageListLayout.removeMessage(message)
    }

    override fun recallMessage(message: BaseMessageModel?) {
    }

    override fun translateMessage(message: BaseMessageModel?, isTranslate: Boolean) {
    }

    override fun hideTranslate(message: BaseMessageModel?) {
    }

    override fun setOnChatLayoutListener(listener: OnChatLayoutListener?) {
        this.listener = listener
    }

    override fun setOnChatRecordTouchListener(voiceTouchListener: OnChatRecordTouchListener?) {
        this.recordListener = voiceTouchListener
    }

    override fun setOnRecallMessageResultListener(listener: OnRecallMessageResultListener?) {
    }

    override fun setOnAddMsgAttrsBeforeSendEvent(sendMsgEvent: OnAddMsgAttrsBeforeSendEvent?) {
    }


    override fun onTyping(s: CharSequence, start: Int, before: Int, count: Int) {
        listener?.onTextChanged(s, start, before, count)
        if (turnOnTyping) {
            typingHandler?.let {
                if (!isNotFirstSend) {
                    isNotFirstSend = true
                    it.sendEmptyMessage(MSG_TYPING_HEARTBEAT)
                }
                it.removeMessages(MSG_TYPING_END)
                it.sendEmptyMessageDelayed(
                    MSG_TYPING_END,
                    TYPING_SHOW_TIME.toLong()
                )
            }

        }
    }

    override fun onSendMessage(content: String) {
        Log.e("chatLayout onSendMessage  ", "$content")
        presenter.sendTextMessage(content)
    }

    override fun onExpressionClicked(emojicon: Any?) {
        //发送表情
        //todo 发送 gif 表情
        emojicon?.let {
            ToastUtils.showToast(context, it as String)
        }

    }

    override fun onPressToSpeakBtnTouch(v: View, event: MotionEvent): Boolean {
        val onRecordTouch: Boolean = recordListener?.onRecordTouch(v, event) ?: false
        if (!onRecordTouch) {
            return false
        }
        return voiceRecordView.onPressToSpeakBtnTouch(
            v,
            event,
            object : BenVoiceRecorderView.BenVoiceRecorderCallback {
                override fun onVoiceRecordComplete(voiceFilePath: String?, voiceTimeLength: Int) {
                    sendVoiceMessage(voiceFilePath, voiceTimeLength)
                }

            })
    }

    override fun onChatExtendMenuItemClick(itemId: Int, view: View?) {
        chatLayoutListener?.onChatExtendMenuItemClick(view, itemId)
    }

    override fun createThumbFileFail(message: String?) {
        listener?.onChatError(-1, message)
    }

    override fun addMsgAttrBeforeSend(message: BaseMessageModel?) {
    }

    override fun sendMessageFail(message: String?) {
        listener?.onChatError(-1, message)
    }

    override fun sendMessageFinish(message: BaseMessageModel?) {
        mViewBinding.chatMessageListLayout.refreshToLatest()
    }

    override fun deleteLocalMessageSuccess(message: BaseMessageModel?) {
        mViewBinding.chatMessageListLayout.removeMessage(message)
    }

    override fun recallMessageFinish(message: BaseMessageModel?, notification: BaseMessageModel?) {

    }

    override fun recallMessageFail(code: Int, message: String?) {
        TODO("Not yet implemented")
    }

    override fun onPresenterMessageSuccess(message: BaseMessageModel?) {
        listener?.onChatSuccess(message)
    }

    override fun onPresenterMessageError(message: BaseMessageModel?, code: Int, error: String?) {
        listener?.onChatError(code, error)
    }

    override fun onPresenterMessageInProgress(message: BaseMessageModel?, progress: Int) {
        Log.i("symbol-6", "onPresenterMessageInProgress")
    }

    override fun translateMessageSuccess(message: BaseMessageModel?) {
    }

    override fun translateMessageFail(message: BaseMessageModel?, code: Int, error: String?) {
    }

    override fun addReactionMessageSuccess(message: BaseMessageModel?) {
    }

    override fun addReactionMessageFail(message: BaseMessageModel?, code: Int, error: String?) {
    }

    override fun removeReactionMessageSuccess(message: BaseMessageModel?) {
    }

    override fun removeReactionMessageFail(message: BaseMessageModel?, code: Int, error: String?) {
    }

    override fun context(): Context? {
        return context
    }

    override fun showItemDefaultMenu(showDefault: Boolean) {
    }

    override fun clearMenu() {
    }

    override fun addItemMenu(item: MenuItemBean?) {
    }

    override fun addItemMenu(groupId: Int, itemId: Int, order: Int, title: String?) {
    }

    override fun findItem(id: Int): MenuItemBean? {
        return menuHelper?.findItem(id)
    }

    override fun findItemVisible(id: Int, visible: Boolean) {
        menuHelper?.findItemVisible(id, visible)
    }

    override fun setOnPopupWindowItemClickListener(listener: OnMenuChangeListener?) {
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        typingHandler?.removeCallbacksAndMessages(null)
    }

    override fun onTouchItemOutside(v: View?, position: Int) {
        mViewBinding.layoutMenu.hideSoftKeyboard()
        mViewBinding.layoutMenu.showExtendMenu(false)
    }

    override fun onViewDragging() {
        mViewBinding.layoutMenu.hideSoftKeyboard()
        mViewBinding.layoutMenu.showExtendMenu(false)
    }

    override fun onReachBottom() {
    }

    override fun switch(isHume: Boolean) {
        MMkvTool.putIsHuman(isHume)

        presenter.viewModel?.let {
            val mv = it as ChatViewModel
            mv.getHumanTak().postValue(isHume)
        }
        if (isHume){
            // 转人工的信息
            presenter.sendSwitchHumeMessage()
        }
    }


}