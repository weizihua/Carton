package com.study.carton.http

import android.text.TextUtils
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException



class ExceptionHandle {
    companion object {
        private const val UNAUTHORIZED = 401
        private const val FORBIDDEN = 403
        private const val NOT_FOUND = 404
        private const val REQUEST_TIMEOUT = 408
        private const val INTERNAL_SERVER_ERROR = 500
        private const val BAD_GATEWAY = 502
        private const val SERVICE_UNAVAILABLE = 503
        private const val GATEWAY_TIMEOUT = 504

        fun handleException(e: Throwable): Throwable? {
            when (e) {
                is HttpException -> {
                    when (e.code()) {
                        UNAUTHORIZED, FORBIDDEN, NOT_FOUND, REQUEST_TIMEOUT, GATEWAY_TIMEOUT, INTERNAL_SERVER_ERROR, SERVICE_UNAVAILABLE ->
                            return Throwable("网络异常，请检查网络设置")
                        BAD_GATEWAY -> {
                            return Throwable("服务器异常")
                        }
                    }
                }
                is ConnectException -> {
                    return Throwable("网络异常，请检查网络设置")
                }
                is java.net.SocketTimeoutException -> {
                    return Throwable("网络超时，请检查网络设置")
                }
                is UnknownHostException -> {
                    return Throwable("网络异常，请检查网络设置")
                }
                is IOException -> {
                    return Throwable("网络异常，请检查网络设置")
                }
                is SocketException -> {
                    return Throwable("网络异常，请检查网络设置")
                }
            }

            if(!TextUtils.isEmpty(e.message) && e.message!!.contains("Exception")) {
                return Throwable("网络异常，请检查网络设置")
            }

            return if(e.message == null) null else Throwable(e.message)
        }
    }
}