package com.ben.bencustomerserver.listener

import android.view.View
import com.ben.bencustomerserver.model.BaseMessageModel

/**
 * 消息列表中的点击事件
 */
interface MessageListItemClickListener {
    /**
     * there is default handling when bubble is clicked, if you want handle it, return true
     * another way is you implement in onBubbleClick() of chat row
     * @param message
     * @return
     */
    fun onBubbleClick(message: BaseMessageModel?): Boolean

    /**
     * click resend view
     * @param message
     * @return
     */
    fun onResendClick(message: BaseMessageModel?): Boolean

    /**
     * on long click for bubble
     * @param v
     * @param message
     */
    fun onBubbleLongClick(v: View?, message: BaseMessageModel?): Boolean

    /**
     * click the user avatar
     * @param username
     */
    fun onUserAvatarClick(username: String?)

    /**
     * long click for user avatar
     * @param username
     */
    fun onUserAvatarLongClick(username: String?)

    /**
     * message is create status
     * @param message
     */
    fun onMessageCreate(message: BaseMessageModel?)

    /**
     * message send success
     * @param message
     */
    fun onMessageSuccess(message: BaseMessageModel?)

    /**
     * message send fail
     * @param message
     * @param code
     * @param error
     */
    fun onMessageError(message: BaseMessageModel?, code: Int, error: String?)

    /**
     * message in sending progress
     * @param message
     * @param progress
     */
    fun onMessageInProgress(message: BaseMessageModel?, progress: Int)


    //	/** TODO(" 屏蔽")
    //	 * remove reaction
    //	 *
    //	 * @param message
    //	 * @param reactionEntity
    //	 */
    //	default void onRemoveReaction(BaseMessageModel message, BenReactionEmojiconEntity reactionEntity) {
    //	}
    //
    //	/**
    //TODO(" 屏蔽")
    //	 * add reaction
    //	 *
    //	 * @param message
    //	 * @param reactionEntity
    //	 */
    //	default void onAddReaction(BaseMessageModel message, BenReactionEmojiconEntity reactionEntity) {
    //	}


    /**
     * Click thread region
     * @param messageId
     * @param threadId
     * @param parentId
     */
    fun onThreadClick(messageId: String?, threadId: String?, parentId: String?): Boolean {
        return false
    }

    /**
     * Long click thread region
     * @param messageId
     * @param threadId
     * @param parentId
     */
    fun onThreadLongClick(
        v: View?,
        messageId: String?,
        threadId: String?,
        parentId: String?
    ): Boolean {
        return false
    }
}