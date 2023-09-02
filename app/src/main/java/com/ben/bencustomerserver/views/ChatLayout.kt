package com.ben.bencustomerserver.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.ben.bencustomerserver.listener.ChatInputMenuListener
import com.ben.bencustomerserver.listener.IHandleMessageView
import com.ben.bencustomerserver.listener.IPopupWindow
import com.ben.bencustomerserver.listener.OnMenuChangeListener
import com.ben.bencustomerserver.model.BaseMessageModel

/**
 * 聊天的布局
 */
class ChatLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) , IHandleMessageView,

    IPopupWindow, ChatInputMenuListener{

    init {

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
        TODO("Not yet implemented")
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

    override val menuHelper: EasePopupWindowHelper?
        get() = TODO("Not yet implemented")

}