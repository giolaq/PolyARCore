// Copyright 2017 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.laquysoft.polyarcore.utils

import android.os.Handler
import android.util.Log

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Asynchronous HTTP request.
 *
 * This object sends an HTTP request asynchronously and calls the supplied callback when
 * the result of the request is available.
 */
class AsyncHttpRequest
/**
 * Creates a new AsyncHttpRequest for the given URL.
 * @param url The URL of the request.
 * @param handler The handler on which the listener should be called.
 * @param listener The listener to call when the request completes.
 */
(url: String, // The handler on which to post a call to the listener.
 private val handler: Handler, // The listener to call when the request is complete.
 private val listener: CompletionListener) {

    // The URL of the request.
    private var url: URL? = null

    // If true, the request was started.
    private var requestStarted: Boolean = false

    /**
     * Listener for HTTP request completion.
     */
    interface CompletionListener {
        /**
         * Called to indicate that the asynchronous HTTP request finished successfully.
         * @param responseBody The body of the response.
         */
        fun onHttpRequestSuccess(responseBody: ByteArray)

        /**
         * Called to indicate that there was a failure in the asynchronous HTTP request.
         * @param statusCode The status code, if a response was received. Otherwise, 0.
         * @param message The error message.
         * @param exception The exception that caused the failure, if any. Otherwise, null.
         */
        fun onHttpRequestFailure(statusCode: Int, message: String, exception: Exception?)
    }

    init {
        try {
            this.url = URL(url)
        } catch (ex: MalformedURLException) {
            Log.e(TAG, "Invalid URL: " + url)
            listener.onHttpRequestFailure(0, "Invalid URL: " + url, ex)
        }

    }

    /**
     * Sends the request.
     *
     * After the request completes, the listener specified in the constructor will be called
     * to report the result of the request. This method does not block, it returns immediately.
     */
    fun send() {
        if (requestStarted) {
            throw IllegalStateException("AsyncHttpRequest can only be sent once.")
        }
        requestStarted = true
        Thread(Runnable { backgroundMain() }).start()
    }

    // Main method for background thread.
    private fun backgroundMain() {
        var connection: HttpURLConnection? = null
        try {
            connection = url!!.openConnection() as HttpURLConnection
            val responseCode = connection.responseCode
            if (responseCode != 200) {
                postFailure(responseCode,
                        "Request to $url failed with HTTP status code $responseCode", null)
                return
            }
            val outputStream = ByteArrayOutputStream()
            connection.inputStream.copyTo(outputStream)
            postSuccess(outputStream.toByteArray())
        } catch (ex: Exception) {
            postFailure(0, "Exception while processing request to " + url!!, ex)
        } finally {
            if (connection != null) {
                connection.disconnect()
            }
        }
    }

    // Posts a failure callback to the listener.
    private fun postFailure(statusCode: Int, message: String, exception: Exception?) {
        handler.post { listener.onHttpRequestFailure(statusCode, message, exception) }
    }

    // Posts a success callback to the listener.
    private fun postSuccess(responseBody: ByteArray) {
        handler.post { listener.onHttpRequestSuccess(responseBody) }
    }

    companion object {
        private val TAG = "PolySample"
    }
}
