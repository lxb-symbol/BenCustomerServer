/*
 *  * EaseMob CONFIDENTIAL
 * __________________
 * Copyright (C) 2017 EaseMob Technologies. All rights reserved.
 *
 * NOTICE: All information contained herein is, and remains
 * the property of EaseMob Technologies.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from EaseMob Technologies.
 */
package com.ben.bencustomerserver.listener

/**
 * \~chinese
 * 带参数的回调函数。
 * 不带参数的回调函数见 [EMCallBack]。
 *
 * @param <T> 参数类型。
 *
 */
interface EMValueCallBack<T> {
    /**
     * \~chinese
     * 回调函数成功执行，返回参数的值。
     *
     * @param value     value 的 class 类型是 T。
     *
     * \~english
     * Occurs when the callback function executes successfully with a value returned.
     *
     * @param value     The class type of value is T.
     */
    fun onSuccess(value: T)

    /**
     * \~chinese
     * 请求失败时的回调函数。
     *
     * @param error     错误代码，详见 [EMError]。
     * @param errorMsg  错误信息。
     *
     * \~english
     * Occurs when the request fails.
     *
     * @param error     The error code. See [EMError].
     * @param errorMsg  A description of the issue that caused this call to fail.
     */
    fun onError(error: Int, errorMsg: String?)
}