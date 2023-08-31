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
class EaseChatInputMenu @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr),
    IChatInputMenu, EaseChatPrimaryMenuListener,
    EaseEmojiconMenuListener, EaseChatExtendMenuItemClickListener {
    private var chatMenuContainer: LinearLayout? = null
    private var primaryMenuContainer: FrameLayout? = null
    private var extendMenuContainer: FrameLayout? = null
    private var primaryMenu: IChatPrimaryMenu? = null
    private var emojiconMenu: IChatEmojiconMenu? = null
    private var extendMenu: IChatExtendMenu? = null
    private var menuListener: ChatInputMenuListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_input_menu_container, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        chatMenuContainer = findViewById(R.id.chat_menu_container)
        primaryMenuContainer = findViewById(R.id.primary_menu_container)
        extendMenuContainer = findViewById(R.id.extend_menu_container)
        init()
    }

    private fun init() {
        showPrimaryMenu()
        if (extendMenu == null) {
            extendMenu = EaseChatExtendMenu(context)
            (extendMenu as EaseChatExtendMenu).init()
        }
        if (emojiconMenu == null) {
            emojiconMenu = EaseEmojiconMenu(context)
            (emojiconMenu as EaseEmojiconMenu).init()
        }
    }

    override fun setCustomPrimaryMenu(menu: IChatPrimaryMenu) {
        primaryMenu = menu
        showPrimaryMenu()
    }

    override fun setCustomEmojiconMenu(menu: IChatEmojiconMenu) {
        emojiconMenu = menu
    }

    override fun setCustomExtendMenu(menu: IChatExtendMenu) {
        extendMenu = menu
    }

    override fun hideExtendContainer() {
        primaryMenu!!.showNormalStatus()
        extendMenuContainer!!.visibility = GONE
    }

    override fun showEmojiconMenu(show: Boolean) {
        if (show) {
            showEmojiconMenu()
        } else {
            extendMenuContainer!!.visibility = GONE
        }
    }

    override fun showExtendMenu(show: Boolean) {
        if (show) {
            showExtendMenu()
        } else {
            extendMenuContainer!!.visibility = GONE
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

    override fun setChatInputMenuListener(listener: ChatInputMenuListener) {
        menuListener = listener
    }

    override fun getPrimaryMenu(): IChatPrimaryMenu {
        return primaryMenu!!
    }

    override fun getEmojiconMenu(): IChatEmojiconMenu {
        return emojiconMenu!!
    }

    override fun getChatExtendMenu(): IChatExtendMenu {
        return extendMenu!!
    }

    override fun onBackPressed(): Boolean {
        if (extendMenuContainer!!.visibility == VISIBLE) {
            extendMenuContainer!!.visibility = GONE
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
        if (extendMenu == null) {
            extendMenu = EaseChatExtendMenu(context)
            (extendMenu as EaseChatExtendMenu).init()
        }
        if (extendMenu is View) {
            extendMenuContainer!!.visibility = VISIBLE
            extendMenuContainer!!.removeAllViews()
            extendMenuContainer!!.addView(extendMenu as View?)
            extendMenu?.setEaseChatExtendMenuItemClickListener(this)
        }
        if (extendMenu is Fragment && context is AppCompatActivity) {
            extendMenuContainer!!.visibility = VISIBLE
            val manager = (context as AppCompatActivity).supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.extend_menu_container, (extendMenu as Fragment?)!!)
                .commitAllowingStateLoss()
            extendMenu?.setEaseChatExtendMenuItemClickListener(this)
        }
    }

    private fun showEmojiconMenu() {
        if (emojiconMenu == null) {
            emojiconMenu = EaseEmojiconMenu(context)
            (emojiconMenu as EaseEmojiconMenu).init()
        }
        if (emojiconMenu is View) {
            extendMenuContainer!!.visibility = VISIBLE
            extendMenuContainer!!.removeAllViews()
            extendMenuContainer!!.addView(emojiconMenu as View?)
            emojiconMenu?.setEmojiconMenuListener(this)
        }
        if (emojiconMenu is Fragment && context is AppCompatActivity) {
            extendMenuContainer!!.visibility = VISIBLE
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
        return if (menuListener != null) {
            menuListener!!.onPressToSpeakBtnTouch(v, event)
        } else false
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

    override fun onExpressionClicked(emojicon: Any) {
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