package com.ben.bencustomerserver.views

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.listener.EaseChatPrimaryMenuListener
import com.ben.bencustomerserver.listener.IChatPrimaryMenu
import com.ben.bencustomerserver.views.EaseInputEditText.OnEditTextChangeListener

/**
 * 包含对话框，发送按钮，语音按钮的具体实现
 *
 */
class EaseChatPrimaryMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr),
    IChatPrimaryMenu, View.OnClickListener,
    OnEditTextChangeListener, TextWatcher {
    private var rlBottom: LinearLayout? = null
    private var buttonSetModeVoice: ImageView? = null
    private var buttonSetModeKeyboard: ImageView? = null
    private var buttonPressToSpeak: FrameLayout? = null
    private var edittextLayout: FrameLayout? = null
    override var editText: EaseInputEditText? = null


    private var faceLayout: RelativeLayout? = null
    private var faceNormal: ImageView? = null
    private var faceChecked: ImageView? = null
    private var buttonMore: CheckBox? = null
    private var buttonSend: TextView? = null
    private var listener: EaseChatPrimaryMenuListener? = null
    private var menuType = EaseInputMenuStyle.All//菜单展示形式
    private var inputManager: InputMethodManager
    private var activity: Activity

    init {
        LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_primary_menu, this)
        activity = context as Activity
        inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        initViews()
    }

    private fun initViews() {
        rlBottom = findViewById(R.id.rl_bottom)
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice)
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard)
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak)
        edittextLayout = findViewById(R.id.edittext_layout)
        editText = findViewById(R.id.et_sendmessage)
        faceLayout = findViewById(R.id.rl_face)
        faceNormal = findViewById(R.id.iv_face_normal)
        faceChecked = findViewById(R.id.iv_face_checked)
        buttonMore = findViewById(R.id.btn_more)
        buttonSend = findViewById(R.id.btn_send)
        editText?.requestFocus()
        showNormalStatus()
        initListener()
    }

    private fun initListener() {
        buttonSend!!.setOnClickListener(this)
        buttonSetModeKeyboard!!.setOnClickListener(this)
        buttonSetModeVoice!!.setOnClickListener(this)
        buttonMore!!.setOnClickListener(this)
        faceLayout!!.setOnClickListener(this)
        editText!!.setOnClickListener(this)
        editText!!.setOnEditTextChangeListener(this)
        editText!!.addTextChangedListener(this)

        buttonPressToSpeak!!.setOnTouchListener { v: View, event: MotionEvent ->
            listener?.onPressToSpeakBtnTouch(v, event) ?: false
        }
    }

    private fun checkSendButton() {
        if (TextUtils.isEmpty(editText!!.text.toString().trim { it <= ' ' })) {
            buttonMore!!.visibility = VISIBLE
            buttonSend!!.visibility = GONE
        } else {
            buttonMore!!.visibility = GONE
            buttonSend!!.visibility = VISIBLE
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        editText!!.removeTextChangedListener(this)
    }

    override fun setMenuShowType(style: EaseInputMenuStyle?) {
        menuType = style!!
        checkMenuType()
    }


    override fun showNormalStatus() {
        hideSoftKeyboard()
        buttonSetModeVoice!!.visibility = VISIBLE
        buttonSetModeKeyboard!!.visibility = GONE
        edittextLayout!!.visibility = VISIBLE
        buttonPressToSpeak!!.visibility = GONE
        hideExtendStatus()
        checkSendButton()
        checkMenuType()
    }

    override fun showTextStatus() {
        buttonSetModeVoice!!.visibility = VISIBLE
        buttonSetModeKeyboard!!.visibility = GONE
        edittextLayout!!.visibility = VISIBLE
        buttonPressToSpeak!!.visibility = GONE
        hideExtendStatus()
        showSoftKeyboard(editText)
        checkSendButton()
        checkMenuType()
        if (listener != null) {
            listener!!.onToggleTextBtnClicked()
        }
    }

    override fun showVoiceStatus() {
        hideSoftKeyboard()
        buttonSetModeVoice!!.visibility = GONE
        buttonSetModeKeyboard!!.visibility = VISIBLE
        edittextLayout!!.visibility = GONE
        buttonPressToSpeak!!.visibility = VISIBLE
        hideExtendStatus()
        checkMenuType()
        if (listener != null) {
            listener!!.onToggleVoiceBtnClicked()
        }
    }

    override fun showEmojiconStatus() {
        buttonSetModeVoice!!.visibility = VISIBLE
        buttonSetModeKeyboard!!.visibility = GONE
        edittextLayout!!.visibility = VISIBLE
        buttonPressToSpeak!!.visibility = GONE
        buttonMore!!.isChecked = false
        if (faceNormal!!.visibility == VISIBLE) {
            hideSoftKeyboard()
            showSelectedFaceImage()
        } else {
            showSoftKeyboard(editText)
            showNormalFaceImage()
        }
        checkMenuType()
        if (listener != null) {
            listener!!.onToggleEmojiconClicked(faceChecked!!.visibility == VISIBLE)
        }
    }

    override fun showMoreStatus() {
        if (buttonMore!!.isChecked) {
            hideSoftKeyboard()
            buttonSetModeVoice!!.visibility = VISIBLE
            buttonSetModeKeyboard!!.visibility = GONE
            edittextLayout!!.visibility = VISIBLE
            buttonPressToSpeak!!.visibility = GONE
            showNormalFaceImage()
        } else {
            showTextStatus()
        }
        checkMenuType()
        if (listener != null) {
            listener!!.onToggleExtendClicked(buttonMore!!.isChecked)
        }
    }

    override fun hideExtendStatus() {
        buttonMore!!.isChecked = false
        showNormalFaceImage()
    }

    override fun onEmojiconInputEvent(emojiContent: CharSequence?) {
        editText!!.append(emojiContent)
    }

    override fun onEmojiconDeleteEvent() {
        if (!TextUtils.isEmpty(editText!!.text)) {
            val event =
                KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL)
            editText!!.dispatchKeyEvent(event)
        }
    }


    override fun onTextInsert(text: CharSequence?) {
        val start = editText!!.selectionStart
        val editable = editText!!.editableText
        editable.insert(start, text)
        showTextStatus()
    }


    override fun setMenuBackground(bg: Drawable?) {
        rlBottom!!.background = bg
    }

    override fun setSendButtonBackground(bg: Drawable?) {
        buttonSend!!.background = bg
    }

    override fun setEaseChatPrimaryMenuListener(listener: EaseChatPrimaryMenuListener?) {
        this.listener = listener
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.btn_send) { //发送
            if (listener != null) {
                val s = editText!!.text.toString()
                editText!!.setText("")
                listener!!.onSendBtnClicked(s)
            }
        } else if (id == R.id.btn_set_mode_voice) { //切换到语音模式
            showVoiceStatus()
        } else if (id == R.id.btn_set_mode_keyboard) { //切换到文本模式
            showTextStatus()
        } else if (id == R.id.btn_more) { //切换到更多模式
            showMoreStatus()
        } else if (id == R.id.et_sendmessage) { //切换到文本模式
            showTextStatus()
        } else if (id == R.id.rl_face) { //切换到表情模式
            showEmojiconStatus()
        }
    }

    override fun onClickKeyboardSendBtn(content: String) {
        if (listener != null) {
            listener!!.onSendBtnClicked(content)
        }
    }


    override fun onEditTextHasFocus(hasFocus: Boolean) {
        if (listener != null) {
            listener!!.onEditTextHasFocus(hasFocus)
        }
    }

    private fun checkMenuType() {
        when (menuType) {
            EaseInputMenuStyle.DISABLE_VOICE -> {
                buttonSetModeVoice!!.visibility = GONE
                buttonSetModeKeyboard!!.visibility = GONE
                buttonPressToSpeak!!.visibility = GONE
            }
            EaseInputMenuStyle.DISABLE_EMOJICON -> {
                faceLayout!!.visibility = GONE
            }
            EaseInputMenuStyle.DISABLE_VOICE_EMOJICON -> {
                buttonSetModeVoice!!.visibility = GONE
                buttonSetModeKeyboard!!.visibility = GONE
                buttonPressToSpeak!!.visibility = GONE
                faceLayout!!.visibility = GONE
            }
            EaseInputMenuStyle.ONLY_TEXT -> {
                buttonSetModeVoice!!.visibility = GONE
                buttonSetModeKeyboard!!.visibility = GONE
                buttonPressToSpeak!!.visibility = GONE
                faceLayout!!.visibility = GONE
                buttonMore!!.visibility = GONE
            }

            else -> {}
        }
    }

    private fun showSendButton(s: CharSequence) {
        if (!TextUtils.isEmpty(s)) {
            buttonMore!!.visibility = GONE
            buttonSend!!.visibility = VISIBLE
        } else {
            buttonMore!!.visibility = VISIBLE
            buttonSend!!.visibility = GONE
        }
        checkMenuType()
    }

    private fun showNormalFaceImage() {
        faceNormal!!.visibility = VISIBLE
        faceChecked!!.visibility = INVISIBLE
    }

    private fun showSelectedFaceImage() {
        faceNormal!!.visibility = INVISIBLE
        faceChecked!!.visibility = VISIBLE
    }

    /**
     * hide soft keyboard
     */
    override fun hideSoftKeyboard() {
        if (editText == null) {
            return
        }
        editText!!.requestFocus()
        if (activity.window.attributes.softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.currentFocus != null) inputManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }


    /**
     * show soft keyboard
     * @param et
     */
    private fun showSoftKeyboard(et: EditText?) {
        if (et == null) {
            return
        }
        et.requestFocus()
        inputManager.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
    }


    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        Log.e("TAG", this.javaClass.simpleName + " onTextChanged s:" + s)
        showSendButton(s)
        if (listener != null) {
            listener!!.onTyping(s, start, before, count)
        }
    }

    override fun afterTextChanged(s: Editable) {
        Log.e("TAG", this.javaClass.simpleName + " afterTextChanged s:" + s)
    }
}