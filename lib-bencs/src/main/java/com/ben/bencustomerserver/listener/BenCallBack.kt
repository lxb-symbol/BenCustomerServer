package com.ben.bencustomerserver.listener

interface BenCallBack {
    /**
     * \~chinese
     * 成功时执行回调函数。
     *
     * \~english
     * Occurs when the method succeeds.
     */
    fun onSuccess()

    /**
     * \~chinese
     * 发生错误时调用的回调函数，详见 [EMError]。
     *
     * @param code           错误代码。
     * @param error          包含文本类型的错误描述。
     *
     * \~english
     * Occurs when an error occurs, see [EMError].
     *
     * @param code           The error code.
     * @param error          A description of the cause to the error.
     */
    fun onError(code: Int, error: String?)

    /**
     * \~chinese
     * 刷新进度的回调函数。
     *
     * @param progress       进度信息。
     * @param status         包含文件描述的进度信息，如果 SDK 没有提供，结果可能是 ""，或者 null。
     *
     * \~english
     * Occurs when the progress updates.
     *
     * @param progress       The progress information.
     * @param status         A description of the progress. Might be an empty string "" or null if the SDK does not return the information.
     */
    fun onProgress(progress: Int, status: String?) {}
}