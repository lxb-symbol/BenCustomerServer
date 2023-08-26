package com.symbol.lib_net.exception


class ResultException(var errCode: String?, private var msg: String?) : Exception(msg)
