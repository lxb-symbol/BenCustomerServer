package com.ben.bencustomerserver.views

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.databinding.CsChatLayoutBinding
import com.ben.bencustomerserver.listener.ChatInputMenuListener
import com.ben.bencustomerserver.listener.IChatLayout
import com.ben.bencustomerserver.listener.IHandleMessageView
import com.ben.bencustomerserver.listener.IPopupWindow
import com.ben.bencustomerserver.listener.OnAddMsgAttrsBeforeSendEvent
import com.ben.bencustomerserver.listener.OnChatLayoutListener
import com.ben.bencustomerserver.listener.OnChatRecordTouchListener
import com.ben.bencustomerserver.listener.OnMenuChangeListener
import com.ben.bencustomerserver.listener.OnRecallMessageResultListener
import com.ben.bencustomerserver.model.BaseMessageModel

/**
 * 聊天的布局
 */
class ChatLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), IHandleMessageView,
    IPopupWindow, ChatInputMenuListener, IChatLayout {
    private lateinit var mViewBinding: CsChatLayoutBinding
    lateinit var chatInputMenu: EaseChatInputMenu
    override fun chatInputMenu(): EaseChatInputMenu = chatInputMenu

    override fun inputContent(): String? {
        TODO("Not yet implemented")
    }

    override fun turnOnTypingMonitor(turnOn: Boolean) {
        TODO("Not yet implemented")
    }

    override fun sendTextMessage(content: String?) {
        TODO("Not yet implemented")
    }

    override fun sendTextMessage(content: String?, isNeedGroupAck: Boolean) {
        TODO("Not yet implemented")
    }

    override fun sendAtMessage(content: String?) {
        TODO("Not yet implemented")
    }

    override fun sendBigExpressionMessage(name: String?, identityCode: String?) {
        TODO("Not yet implemented")
    }

    override fun sendVoiceMessage(filePath: String?, length: Int) {
        TODO("Not yet implemented")
    }

    override fun sendVoiceMessage(filePath: Uri?, length: Int) {
        TODO("Not yet implemented")
    }

    override fun sendImageMessage(imageUri: Uri?) {
        TODO("Not yet implemented")
    }

    override fun sendImageMessage(imageUri: Uri?, sendOriginalImage: Boolean) {
        TODO("Not yet implemented")
    }

    override fun sendLocationMessage(
        latitude: Double,
        longitude: Double,
        locationAddress: String?,
        buildingName: String?
    ) {
        TODO("Not yet implemented")
    }

    override fun sendVideoMessage(videoUri: Uri?, videoLength: Int) {
        TODO("Not yet implemented")
    }

    override fun sendFileMessage(fileUri: Uri?) {
        TODO("Not yet implemented")
    }

    override fun addMessageAttributes(message: BaseMessageModel?) {
        TODO("Not yet implemented")
    }

    override fun sendMessage(message: BaseMessageModel?) {
        TODO("Not yet implemented")
    }

    override fun resendMessage(message: BaseMessageModel?) {
        TODO("Not yet implemented")
    }

    override fun deleteMessage(message: BaseMessageModel?) {
        TODO("Not yet implemented")
    }

    override fun recallMessage(message: BaseMessageModel?) {
        TODO("Not yet implemented")
    }

    override fun translateMessage(message: BaseMessageModel?, isTranslate: Boolean) {
        TODO("Not yet implemented")
    }

    override fun hideTranslate(message: BaseMessageModel?) {
        TODO("Not yet implemented")
    }

    override fun setOnChatLayoutListener(listener: OnChatLayoutListener?) {
        TODO("Not yet implemented")
    }

    override fun setOnChatRecordTouchListener(voiceTouchListener: OnChatRecordTouchListener?) {
        TODO("Not yet implemented")
    }

    override fun setOnRecallMessageResultListener(listener: OnRecallMessageResultListener?) {
        TODO("Not yet implemented")
    }

    override fun setOnAddMsgAttrsBeforeSendEvent(sendMsgEvent: OnAddMsgAttrsBeforeSendEvent?) {
        TODO("Not yet implemented")
    }

    lateinit var voiceRecordView: EaseVoiceRecorderView
    var chatLayoutListener: OnChatLayoutListener? = null

    companion object {
        /**
         * 正在输入的心跳
         */
        const val MSG_TYPING_HEARTBEAT = 0

        /**
         *正在编辑结束
         */
        const val MSG_TYPING_END = 1

        /**
         * 对方正在编辑结束
         */
        const val MSG_OTHER_TYPING_END = 2

        const val ACTION_TYPING_BEGIN = "TypingBegin"

        const val ACTION_TYPING_END = "TypingEnd"

        const val TYPING_SHOW_TIME = 1000

        const val OTHER_TYPING_SHOW_TIME = 5000

    }


    init {
        mViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.cs_chat_layout,
            this,
            true
        )
        chatInputMenu = mViewBinding.layoutMenu
        voiceRecordView = mViewBinding.voiceRecorder


    }


    override fun onTyping(s: CharSequence, start: Int, before: Int, count: Int) {
        TODO("Not yet implemented")
    }

    override fun onSendMessage(content: String) {
        TODO("Not yet implemented")
    }

    override fun onExpressionClicked(emojicon: Any?) {
        TODO("Not yet implemented")
    }

    override fun onPressToSpeakBtnTouch(v: View, event: MotionEvent): Boolean {
        TODO("Not yet implemented")
    }

    override fun onChatExtendMenuItemClick(itemId: Int, view: View?) {
        chatLayoutListener?.onChatExtendMenuItemClick(view, itemId)
    }

    override fun createThumbFileFail(message: String?) {
        TODO("Not yet implemented")
    }

    override fun addMsgAttrBeforeSend(message: BaseMessageModel?) {
        TODO("Not yet implemented")
    }

    override fun sendMessageFail(message: String?) {
        TODO("Not yet implemented")
    }

    override fun sendMessageFinish(message: BaseMessageModel?) {
        TODO("Not yet implemented")
    }

    override fun deleteLocalMessageSuccess(message: BaseMessageModel?) {
        TODO("Not yet implemented")
    }

    override fun recallMessageFinish(message: BaseMessageModel?, notification: BaseMessageModel?) {
        TODO("Not yet implemented")
    }

    override fun recallMessageFail(code: Int, message: String?) {
        TODO("Not yet implemented")
    }

    override fun onPresenterMessageSuccess(message: BaseMessageModel?) {
        TODO("Not yet implemented")
    }

    override fun onPresenterMessageError(message: BaseMessageModel?, code: Int, error: String?) {
        TODO("Not yet implemented")
    }

    override fun onPresenterMessageInProgress(message: BaseMessageModel?, progress: Int) {
        TODO("Not yet implemented")
    }

    override fun translateMessageSuccess(message: BaseMessageModel?) {
        TODO("Not yet implemented")
    }

    override fun translateMessageFail(message: BaseMessageModel?, code: Int, error: String?) {
        TODO("Not yet implemented")
    }

    override fun addReactionMessageSuccess(message: BaseMessageModel?) {
        TODO("Not yet implemented")
    }

    override fun addReactionMessageFail(message: BaseMessageModel?, code: Int, error: String?) {
        TODO("Not yet implemented")
    }

    override fun removeReactionMessageSuccess(message: BaseMessageModel?) {
        TODO("Not yet implemented")
    }

    override fun removeReactionMessageFail(message: BaseMessageModel?, code: Int, error: String?) {
        TODO("Not yet implemented")
    }

    override fun context(): Context? {
        TODO("Not yet implemented")
    }

    override fun showItemDefaultMenu(showDefault: Boolean) {
        TODO("Not yet implemented")
    }

    override fun clearMenu() {
        TODO("Not yet implemented")
    }

    override fun addItemMenu(item: MenuItemBean?) {
        TODO("Not yet implemented")
    }

    override fun addItemMenu(groupId: Int, itemId: Int, order: Int, title: String?) {
        TODO("Not yet implemented")
    }

    override fun findItem(id: Int): MenuItemBean? {
        TODO("Not yet implemented")
    }

    override fun findItemVisible(id: Int, visible: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setOnPopupWindowItemClickListener(listener: OnMenuChangeListener?) {
        TODO("Not yet implemented")
    }

    override val menuHelper: EasePopupWindowHelper
        get() = TODO("Not yet implemented")

}