package com.ben.bencustomerserver.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.listener.ChatInputMenuListener
import com.ben.bencustomerserver.listener.EaseChatExtendMenuItemClickListener
import com.ben.bencustomerserver.listener.EaseChatPrimaryMenuListener
import com.ben.bencustomerserver.listener.EaseEmojiconMenuListener
import com.ben.bencustomerserver.listener.IChatEmojiconMenu
import com.ben.bencustomerserver.listener.IChatExtendMenu
import com.ben.bencustomerserver.listener.IChatInputMenu
import com.ben.bencustomerserver.listener.IChatPrimaryMenu
import com.ben.bencustomerserver.utils.EaseSmileUtils.getSmiledText

/**
 * 包含 聊天输入布局，和表情
 */
class EaseChatInputMenu : LinearLayout,
    IChatInputMenu, EaseChatPrimaryMenuListener,
    EaseEmojiconMenuListener, EaseChatExtendMenuItemClickListener {

    constructor(context: Context?) : this(context!!, null)

    constructor(context: Context?, attributeSet: AttributeSet?) : this(context!!, attributeSet, 0)

    constructor(context: Context?, attributeSet: AttributeSet?, def: Int) : super(
        context,
        attributeSet,
        def
    )

    private var chatMenuContainer: LinearLayout? = null
    private var primaryMenuContainer: FrameLayout? = null
    private var chatExtendMenuContainer: FrameLayout? = null
    var menuListener: ChatInputMenuListener? = null

    /**
     * 聊天输入框
     */
    private var primaryMenu: IChatPrimaryMenu? = null

    /**
     * 聊天扩展：发送位置，图片，视频等按钮
     */
    private var chatExtendMenu: IChatExtendMenu? = null

    /**
     * 表情布局
     */
    private var emojiconMenu: IChatEmojiconMenu? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_input_menu_container, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        chatMenuContainer = findViewById(R.id.chat_menu_container)
        primaryMenuContainer = findViewById(R.id.primary_menu_container)
        chatExtendMenuContainer = findViewById(R.id.extend_menu_container)
        init()
    }

    private fun init() {
        showPrimaryMenu()
        if (chatExtendMenu == null) {
            chatExtendMenu = EaseChatExtendMenu(context)
            (chatExtendMenu as EaseChatExtendMenu).init()
        }
        if (emojiconMenu == null) {
            emojiconMenu = EaseEmojiconMenu(context)
            (emojiconMenu as EaseEmojiconMenu).init()
        }
    }

    override fun setCustomPrimaryMenu(menu: IChatPrimaryMenu?) {
        primaryMenu = menu
        showPrimaryMenu()
    }

    override fun setCustomEmojiconMenu(menu: IChatEmojiconMenu) {
        emojiconMenu = menu
    }

    override fun setCustomExtendMenu(menu: IChatExtendMenu) {
        chatExtendMenu = menu
    }

    override fun hideExtendContainer() {
        primaryMenu!!.showNormalStatus()
        chatExtendMenuContainer!!.visibility = GONE
    }

    override fun showEmojiconMenu(show: Boolean) {
        if (show) {
            showEmojiconMenu()
        } else {
            chatExtendMenuContainer!!.visibility = GONE
        }
    }

    override fun showExtendMenu(show: Boolean) {
        if (show) {
            showExtendMenu()
        } else {
            chatExtendMenuContainer!!.visibility = GONE
            if (primaryMenu != null) {
                primaryMenu!!.hideExtendStatus()
            }
        }
    }

    override fun hideSoftKeyboard() {
        if (primaryMenu != null) {
            primaryMenu!!.hideSoftKeyboard()
        }
    }

    override fun setChatInputMenuListener(listener: ChatInputMenuListener?) {
        menuListener = listener
    }

    override fun getChatPrimaryMenu(): IChatPrimaryMenu? = primaryMenu

    override fun getEmojiconMenu(): IChatEmojiconMenu? = emojiconMenu
    override fun getChatExtendMenu(): IChatExtendMenu? = chatExtendMenu


    override fun onBackPressed(): Boolean {
        if (chatExtendMenuContainer!!.visibility == VISIBLE) {
            chatExtendMenuContainer!!.visibility = GONE
            return false
        }
        return true
    }

    private fun showPrimaryMenu() {
        if (primaryMenu == null) {
            primaryMenu = EaseChatPrimaryMenu(context)
        }
        if (primaryMenu is View) {
            primaryMenuContainer!!.removeAllViews()
            primaryMenuContainer!!.addView(primaryMenu as View?)
            primaryMenu?.setEaseChatPrimaryMenuListener(this)
        }
        if (primaryMenu is Fragment && context is AppCompatActivity) {
            val manager = (context as AppCompatActivity).supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.primary_menu_container, (primaryMenu as Fragment?)!!)
                .commitAllowingStateLoss()
            primaryMenu?.setEaseChatPrimaryMenuListener(this)
        }
    }

    private fun showExtendMenu() {
        if (chatExtendMenu == null) {
            chatExtendMenu = EaseChatExtendMenu(context)
            (chatExtendMenu as EaseChatExtendMenu).init()
        }
        if (chatExtendMenu is View) {
            chatExtendMenuContainer!!.visibility = VISIBLE
            chatExtendMenuContainer!!.removeAllViews()
            chatExtendMenuContainer!!.addView(chatExtendMenu as View?)
            chatExtendMenu?.setEaseChatExtendMenuItemClickListener(this)
        }
        if (chatExtendMenu is Fragment && context is AppCompatActivity) {
            chatExtendMenuContainer!!.visibility = VISIBLE
            val manager = (context as AppCompatActivity).supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.extend_menu_container, (chatExtendMenu as Fragment?)!!)
                .commitAllowingStateLoss()
            chatExtendMenu?.setEaseChatExtendMenuItemClickListener(this)
        }
    }

    private fun showEmojiconMenu() {
        if (emojiconMenu == null) {
            emojiconMenu = EaseEmojiconMenu(context)
            (emojiconMenu as EaseEmojiconMenu).init()
        }
        if (emojiconMenu is View) {
            chatExtendMenuContainer!!.visibility = VISIBLE
            chatExtendMenuContainer!!.removeAllViews()
            chatExtendMenuContainer!!.addView(emojiconMenu as View?)
            emojiconMenu?.setEmojiconMenuListener(this)
        }
        if (emojiconMenu is Fragment && context is AppCompatActivity) {
            chatExtendMenuContainer!!.visibility = VISIBLE
            val manager = (context as AppCompatActivity).supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.extend_menu_container, (emojiconMenu as Fragment?)!!)
                .commitAllowingStateLoss()
            emojiconMenu?.setEmojiconMenuListener(this)
        }
    }

    override fun onSendBtnClicked(content: String) {
        Log.i(TAG, "onSendBtnClicked content:$content")
        if (menuListener != null) {
            menuListener!!.onSendMessage(content)
        }
    }

    override fun onTyping(s: CharSequence, start: Int, before: Int, count: Int) {
        Log.i(TAG, "onTyping: s = $s")
        if (menuListener != null) {
            menuListener!!.onTyping(s, start, before, count)
        }
    }

    override fun onPressToSpeakBtnTouch(v: View, event: MotionEvent): Boolean {
        if (menuListener == null) return false
        return menuListener?.onPressToSpeakBtnTouch(v, event) ?: false
    }

    override fun onToggleVoiceBtnClicked() {
        Log.e("TAG", "onToggleVoiceBtnClicked")
        showExtendMenu(false)
    }

    override fun onToggleTextBtnClicked() {
        Log.i(TAG, "onToggleTextBtnClicked")
        showExtendMenu(false)
    }

    override fun onToggleExtendClicked(extend: Boolean) {
        Log.i(TAG, "onToggleExtendClicked extend:$extend")
        showExtendMenu(extend)
    }

    override fun onToggleEmojiconClicked(extend: Boolean) {
        Log.i(TAG, "onToggleEmojiconClicked extend:$extend")
        showEmojiconMenu(extend)
    }

    override fun onEditTextClicked() {
        Log.i(TAG, "onEditTextClicked")
    }

    override fun onEditTextHasFocus(hasFocus: Boolean) {
        Log.i(TAG, "onEditTextHasFocus: hasFocus = $hasFocus")
    }

    override fun onExpressionClicked(emojicon: Any?) {
        Log.i(TAG, "onExpressionClicked")
        if (emojicon is EaseEmojicon) {
            val easeEmojicon = emojicon
            if (easeEmojicon.type != EaseEmojicon.Type.BIG_EXPRESSION) {
                if (easeEmojicon.emojiText != null) {
                    primaryMenu!!.onEmojiconInputEvent(
                        getSmiledText(
                            context,
                            easeEmojicon.emojiText
                        )
                    )
                }
            } else {
                if (menuListener != null) {
                    menuListener!!.onExpressionClicked(emojicon)
                }
            }
        } else {
            if (menuListener != null) {
                menuListener!!.onExpressionClicked(emojicon)
            }
        }
    }

    override fun onDeleteImageClicked() {
        Log.i(TAG, "onDeleteImageClicked")
        primaryMenu!!.onEmojiconDeleteEvent()
    }

    override fun onChatExtendMenuItemClick(itemId: Int, view: View?) {
        Log.i(TAG, "onChatExtendMenuItemClick itemId = $itemId")
        if (menuListener != null) {
            menuListener!!.onChatExtendMenuItemClick(itemId, view)
        }
    }

    companion object {
        private val TAG = EaseChatInputMenu::class.java.simpleName
    }
}