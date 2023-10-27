package com.ben.bencustomerserver.listener

import com.ben.bencustomerserver.model.BaseMessageModel

interface IHandleMessageView : ILoadDataView {
    /**
     * 生成视频封面失败
     * @param message
     */
    fun createThumbFileFail(message: String?)

    /**
     * 在发送消息前，添加消息属性，如设置ext等
     * @param message
     */
    fun addMsgAttrBeforeSend(message: BaseMessageModel?)

    /**
     * 发送消息失败
     * @param message
     */
    fun sendMessageFail(message: String?)

    /**
     * 完成发送消息动作
     * @param message
     */
    fun sendMessageFinish(message: BaseMessageModel?)

    /**
     * 删除本地消息
     * @param message
     */
    fun deleteLocalMessageSuccess(message: BaseMessageModel?)

    /**
     * 完成撤回消息
     * @param message
     */
    fun recallMessageFinish(message: BaseMessageModel?, notification: BaseMessageModel?)

    /**
     * 撤回消息失败
     * @param code
     * @param message
     */
    fun recallMessageFail(code: Int, message: String?)

    /**
     * message send success
     * @param message
     */
    fun onPresenterMessageSuccess(message: BaseMessageModel?)

    /**
     * message send fail
     * @param message
     * @param code
     * @param error
     */
    fun onPresenterMessageError(message: BaseMessageModel?, code: Int, error: String?)

    /**
     * message in sending progress
     * @param message
     * @param progress
     */
    fun onPresenterMessageInProgress(message: BaseMessageModel?, progress: Int)

    /**
     * 翻译消息成功
     * @param message
     */
    fun translateMessageSuccess(message: BaseMessageModel?)

    /**
     * 翻译消息失败
     * @param message
     * @param code
     * @param error
     */
    fun translateMessageFail(message: BaseMessageModel?, code: Int, error: String?)

    /**
     * add reaction success
     *
     * @param message
     */
    fun addReactionMessageSuccess(message: BaseMessageModel?)

    /**
     * add reaction fail
     *
     * @param message
     * @param code
     * @param error
     */
    fun addReactionMessageFail(message: BaseMessageModel?, code: Int, error: String?)

    /**
     * remove reaction success
     *
     * @param message
     */
    fun removeReactionMessageSuccess(message: BaseMessageModel?)

    /**
     * remove reaction fail
     *
     * @param message
     * @param code
     * @param error
     */
    fun removeReactionMessageFail(message: BaseMessageModel?, code: Int, error: String?)
}