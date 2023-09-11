/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ben.bencustomerserver.utils

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Environment
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.MessageType

object EaseCommonUtils {
    private const val TAG = "CommonUtils"

    /**
     * check if network avalable
     *
     * @param context
     * @return
     */
    fun isNetWorkConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mNetworkInfo = mConnectivityManager.activeNetworkInfo
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable && mNetworkInfo.isConnected
            }
        }
        return false
    }

    val isSdcardExist: Boolean
        /**
         * check if sdcard exist
         *
         * @return
         */
        get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED


    fun createExpressionMessage(
        toChatUsername: String?,
        expressioName: String,
        identityCode: String?
    ): BaseMessageModel {
        // TODO:  待完善
        val message = BaseMessageModel(messageType = MessageType.TXT)
//        val message: BaseMessageModel = BaseMessageModel.createTxtSendMessage("[$expressioName]", toChatUsername)
//        if (identityCode != null) {
//            message.setAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, identityCode)
//        }
//        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, true)
        return message
    }

    /**
     * TODO("待放开")
     * Get digest according message type and content
     *
     * @param message
     * @param context
     * @return
     */
//    fun getMessageDigest(message: BaseMessageModel, context: Context): String {
//        var digest = ""
//        when (message.getType()) {
//            LOCATION -> if (message.direct() === BaseMessageModel.Direct.RECEIVE) {
//                digest = getString(context, R.string.location_recv)
//                val userProvider: EaseUserProfileProvider = EaseIM.getInstance().getUserProvider()
//                var from: String = message.getFrom()
//                if (userProvider != null && userProvider.getUser(from) != null) {
//                    val user: EaseUser = userProvider.getUser(from)
//                    if (user != null && !TextUtils.isEmpty(user.getNickname())) {
//                        from = user.getNickname()
//                    }
//                }
//                digest = String.format(digest, from)
//                return digest
//            } else {
//                digest = getString(context, R.string.location_prefix)
//            }
//
//            IMAGE -> digest = getString(context, R.string.picture)
//            VOICE -> digest = getString(context, R.string.voice_prefix)
//            VIDEO -> digest = getString(context, R.string.video)
//            CUSTOM -> digest = getString(context, R.string.custom)
//            TXT -> {
//                val txtBody: EMTextMessageBody = message.getBody() as EMTextMessageBody
//                if (txtBody != null) {
//                    digest = if (message.getBooleanAttribute(
//                            EaseConstant.MESSAGE_ATTR_IS_VOICE_CALL,
//                            false
//                        )
//                    ) {
//                        getString(context, R.string.voice_call) + txtBody.getMessage()
//                    } else if (message.getBooleanAttribute(
//                            EaseConstant.MESSAGE_ATTR_IS_VIDEO_CALL,
//                            false
//                        )
//                    ) {
//                        getString(context, R.string.video_call) + txtBody.getMessage()
//                    } else if (message.getBooleanAttribute(
//                            EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION,
//                            false
//                        )
//                    ) {
//                        if (!TextUtils.isEmpty(txtBody.getMessage())) {
//                            txtBody.getMessage()
//                        } else {
//                            getString(context, R.string.dynamic_expression)
//                        }
//                    } else {
//                        txtBody.getMessage()
//                    }
//                }
//            }
//
//            FILE -> digest = getString(context, R.string.file)
//            else -> {
//                Log.e(TAG, "error, unknow type")
//                return ""
//            }
//        }
//        Log.e("TAG", "message text = $digest")
//        return digest
//    }

    fun getString(context: Context, resId: Int): String {
        return context.resources.getString(resId)
    }

    /**
     * get top context
     * @param context
     * @return
     */
    fun getTopActivity(context: Context): String {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfos = manager.getRunningTasks(1)
        return if (runningTaskInfos != null) runningTaskInfos[0].topActivity!!.className else ""
    }

    /**
     * set initial letter of according user's nickname( username if no nickname)
     *
     * @param user
     */
//    fun setUserInitialLetter(user: EaseUser) {
//        val DefaultLetter = "#"
//        var letter = DefaultLetter
//
//        class GetInitialLetter {
//            fun getLetter(name: String): String {
//                if (TextUtils.isEmpty(name)) {
//                    return DefaultLetter
//                }
//                val char0 = name.lowercase(Locale.getDefault())[0]
//                if (Character.isDigit(char0)) {
//                    return DefaultLetter
//                }
//                val pinyin: String = HanziToPinyin.getPinyin(name)
//                if (!TextUtils.isEmpty(pinyin)) {
//                    val letter = pinyin.substring(0, 1).uppercase(Locale.getDefault())
//                    val c = letter[0]
//                    return if (c < 'A' || c > 'Z') {
//                        DefaultLetter
//                    } else letter
//                }
//                return DefaultLetter
//            }
//        }
//        if (!TextUtils.isEmpty(user.getNickname())) {
//            letter = GetInitialLetter().getLetter(user.getNickname())
//            user.setInitialLetter(letter)
//            return
//        }
//        if (letter == DefaultLetter && !TextUtils.isEmpty(user.getUsername())) {
//            letter = GetInitialLetter().getLetter(user.getUsername())
//        }
//        user.setInitialLetter(letter)
//    }


    // TODO:
    /**
     * change the chat type to EMConversationType
     * @param chatType
     * @return
     */
//    fun getConversationType(chatType: Int): EMConversationType {
//        return if (chatType == EaseConstant.CHATTYPE_SINGLE) {
//            EMConversationType.Chat
//        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
//            EMConversationType.GroupChat
//        } else {
//            EMConversationType.ChatRoom
//        }
//    }

    /**
     * TODO("回话")
     * get chat type by conversation type
     * @param conversation
     * @return
     */
//    fun getChatType(conversation: EMConversation): Int {
//        return if (conversation.isGroup()) {
//            if (conversation.getType() === EMConversationType.ChatRoom) {
//                EaseConstant.CHATTYPE_CHATROOM
//            } else {
//                EaseConstant.CHATTYPE_GROUP
//            }
//        } else {
//            EaseConstant.CHATTYPE_SINGLE
//        }
//    }

    /**
     * \~chinese
     * 判断是否是免打扰的消息,如果是app中应该不要给用户提示新消息
     * @param message
     * return
     *
     * \~english
     * check if the message is kind of slient message, if that's it, app should not play tone or vibrate
     *
     * @param message
     * @return
     */
//    fun isSilentMessage(message: BaseMessageModel): Boolean {
//        return message.getBooleanAttribute(EM_IGNORE_NOTIFICATION, false)
//    }

    /**
     * 获取屏幕的基本信息
     * @param context
     * @return
     */
    fun getScreenInfo(context: Context): FloatArray {
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val info = FloatArray(5)
        if (manager != null) {
            val dm = DisplayMetrics()
            manager.defaultDisplay.getMetrics(dm)
            info[0] = dm.widthPixels.toFloat()
            info[1] = dm.heightPixels.toFloat()
            info[2] = dm.densityDpi.toFloat()
            info[3] = dm.density
            info[4] = dm.scaledDensity
        }
        return info
    }

    /**
     * dip to px
     * @param context
     * @param value
     * @return
     */
    fun dip2px(context: Context, value: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            context.resources.displayMetrics
        )
    }

    /**
     * sp to px
     * @param context
     * @param value
     * @return
     */
    fun sp2px(context: Context, value: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            value,
            context.resources.displayMetrics
        )
    }

    /**
     * 判断是否是时间戳
     * @param time
     * @return
     */
    fun isTimestamp(time: String): Boolean {
        if (TextUtils.isEmpty(time)) {
            return false
        }
        var timestamp = 0L
        try {
            timestamp = time.toLong()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return timestamp > 0
    }

    /**
     * 获取首字母
     * @param name
     * @return
     */
//    fun getLetter(name: String): String {
//        return GetInitialLetter().getLetter(name)
//    }

    /**
     * Used to handle message unread
     * @param count
     * @return
     */
    fun handleBigNum(count: Int): String {
        return if (count <= 99) {
            count.toString()
        } else {
            "99+"
        }
    }

//    private class GetInitialLetter {
//        private val defaultLetter = "#"
//
//        /**
//         * 获取首字母
//         * @param name
//         * @return
//         */
//        fun getLetter(name: String): String {
//            if (TextUtils.isEmpty(name)) {
//                return defaultLetter
//            }
//            val char0 = name.lowercase(Locale.getDefault())[0]
//            if (Character.isDigit(char0)) {
//                return defaultLetter
//            }
//            val pinyin: String = HanziToPinyin.getPinyin(name)
//            if (!TextUtils.isEmpty(pinyin)) {
//                val letter = pinyin.substring(0, 1).uppercase(Locale.getDefault())
//                val c = letter[0]
//                return if (c < 'A' || c > 'Z') {
//                    defaultLetter
//                } else letter
//            }
//            return defaultLetter
//        }
//    }
}