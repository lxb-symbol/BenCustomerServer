package com.ben.bencustomerserver.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.listener.ChatInputMenuListener
import com.ben.bencustomerserver.listener.BenChatExtendMenuItemClickListener
import com.ben.bencustomerserver.listener.BenChatPrimaryMenuListener
import com.ben.bencustomerserver.listener.BenEmojiconMenuListener
import com.ben.bencustomerserver.listener.IChatEmojiconMenu
import com.ben.bencustomerserver.listener.IChatExtendMenu
import com.ben.bencustomerserver.listener.IChatInputMenu
import com.ben.bencustomerserver.listener.IChatPrimaryMenu
import com.ben.bencustomerserver.listener.ISwitchHumenListener
import com.ben.bencustomerserver.model.BenEmojiEntity
import com.ben.bencustomerserver.utils.BenEmojiUtil
import com.ben.bencustomerserver.utils.BenSmileUtils.getSmiledText

/**
 * 包含 聊天输入布局，和表情
 */
class BenChatInputMenu : LinearLayout,
    IChatInputMenu, BenChatPrimaryMenuListener,
    BenEmojiconMenuListener, BenChatExtendMenuItemClickListener {

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
    private lateinit var mTvHuman: TextView
    var menuListener: ChatInputMenuListener? = null
    open var switchHumanListener: ISwitchHumenListener? = null

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

    private var human = false

    init {
        LayoutInflater.from(context).inflate(R.layout.ben_widget_chat_input_menu_container, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mTvHuman = findViewById(R.id.tv_human)
        chatMenuContainer = findViewById(R.id.chat_menu_container)
        primaryMenuContainer = findViewById(R.id.primary_menu_container)
        chatExtendMenuContainer = findViewById(R.id.extend_menu_container)
        mTvHuman.setOnClickListener {
            mTvHuman.isEnabled = !human
            switchHumanListener?.let {
                it.switch(human)
            }
        }
        init()
    }

    private fun init() {
        showPrimaryMenu()
        if (chatExtendMenu == null) {
            chatExtendMenu = BenChatExtendMenu(context)
            (chatExtendMenu as BenChatExtendMenu).init()
        }
        if (emojiconMenu == null) {
            emojiconMenu = BenEmojiconMenu(context)
            (emojiconMenu as BenEmojiconMenu).init()
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
            primaryMenu = BenChatPrimaryMenu(context)
        }
        if (primaryMenu is View) {
            primaryMenuContainer!!.removeAllViews()
            primaryMenuContainer!!.addView(primaryMenu as View?)
            primaryMenu?.setBenChatPrimaryMenuListener(this)
        }
        if (primaryMenu is Fragment && context is AppCompatActivity) {
            val manager = (context as AppCompatActivity).supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.primary_menu_container, (primaryMenu as Fragment?)!!)
                .commitAllowingStateLoss()
            primaryMenu?.setBenChatPrimaryMenuListener(this)
        }
    }

    private fun showExtendMenu() {
        if (chatExtendMenu == null) {
            chatExtendMenu = BenChatExtendMenu(context)
            (chatExtendMenu as BenChatExtendMenu).init()
        }
        if (chatExtendMenu is View) {
            chatExtendMenuContainer!!.visibility = VISIBLE
            chatExtendMenuContainer!!.removeAllViews()
            chatExtendMenuContainer!!.addView(chatExtendMenu as View?)
            chatExtendMenu?.setBenChatExtendMenuItemClickListener(this)
        }
        if (chatExtendMenu is Fragment && context is AppCompatActivity) {
            chatExtendMenuContainer!!.visibility = VISIBLE
            val manager = (context as AppCompatActivity).supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.extend_menu_container, (chatExtendMenu as Fragment?)!!)
                .commitAllowingStateLoss()
            chatExtendMenu?.setBenChatExtendMenuItemClickListener(this)
        }
    }

    private fun showEmojiconMenu() {
        if (emojiconMenu == null) {
            emojiconMenu = BenEmojiconMenu(context)
            (emojiconMenu as BenEmojiconMenu).init()
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
        if (emojicon is BenEmojicon) {
            val easeEmojicon = emojicon
            if (easeEmojicon.type != BenEmojicon.Type.BIG_EXPRESSION) {
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
        } else if (emojicon is BenEmojiEntity) {
            primaryMenu!!.onEmojiconInputEvent(
                BenEmojiUtil.getSmiledText(
                    context,
                    emojicon.name
                )
            )
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
        private val TAG = BenChatInputMenu::class.java.simpleName
    }


}