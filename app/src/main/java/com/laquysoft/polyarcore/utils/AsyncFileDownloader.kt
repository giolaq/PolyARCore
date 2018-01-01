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
import android.support.annotation.IntDef
import android.util.Log

import java.util.ArrayList

/** Convenience class that asynchronously downloads a set of files.  */
/** Creates an AsyncFileDownloader, initially with no files.  */
class AsyncFileDownloader {

    // Current state.
    @State private var state = STATE_NOT_STARTED

    // The completion listener we call when we finish downloading all the files (or when there is
    // a failure).
    private var listener: CompletionListener? = null

    // The handler on which we call the completion listener.
    private var handler: Handler? = null

    // The files to download.
    private val entries = ArrayList<Entry>()

    /** Returns whether or not there was an error downloading the files.  */
    val isError: Boolean
        get() = state == STATE_ERROR

    /** Returns the number of files in this object.  */
    val entryCount: Int
        get() = entries.size

    // Possible states we can be in.
    @IntDef(STATE_NOT_STARTED.toLong(), STATE_DOWNLOADING.toLong(), STATE_SUCCESS.toLong(), STATE_ERROR.toLong())
    annotation class State

    /** Callback called when download is complete.  */
    interface CompletionListener {
        /**
         * Callback invoked when all downloads complete, or when there's a failure.
         * @param downloader The downloader. Use [.isError] to determine if there was
         * an error or not. If there was no error, you can access the files with
         * [.getEntry].
         */
        fun onPolyDownloadFinished(downloader: AsyncFileDownloader)
    }

    /**
     * Adds a file to download.
     *
     * Can only be called before [.start] is called.
     * @param fileName The name of the file.
     * @param url The URL to download the file from.
     */
    fun add(fileName: String, url: String) {
        if (state != STATE_NOT_STARTED) {
            throw IllegalStateException("Can't add files to AsyncFileDownloader after starting.")
        }
        Log.d(TAG, "file path " + url)

        entries.add(Entry(fileName, url))
    }

    /**
     * Starts asynchronously downloading all requested files.
     * @param handler The handler on which to call the callback.
     * @param completionListener The callback to call when download completes, or when there is
     * an error.
     */
    fun start(handler: Handler, completionListener: CompletionListener) {
        if (state != STATE_NOT_STARTED) {
            throw IllegalStateException("AsyncFileDownloader had already been started.")
        }
        this.handler = handler
        this.listener = completionListener
        state = STATE_DOWNLOADING
        // For each requested file, create an AsyncHttpRequest object to request it.
        for (entry in entries) {
            val request = AsyncHttpRequest(entry.url, handler,
                    object : AsyncHttpRequest.CompletionListener {
                        override fun onHttpRequestSuccess(responseBody: ByteArray) {
                            if (state != STATE_DOWNLOADING) return
                            Log.d(TAG, "Finished downloading " + entry.fileName + " from " + entry.url)
                            entry.contents = responseBody
                            if (areAllEntriesDone()) {
                                state = STATE_SUCCESS
                                invokeCompletionCallback()
                            }
                        }

                        override fun onHttpRequestFailure(statusCode: Int, message: String, exception: Exception?) {
                            if (state != STATE_DOWNLOADING) return
                            Log.e(TAG, "Error downloading " + entry.fileName + " from " + entry.url + ". Status " +
                                    statusCode + ", message: " + message + (exception ?: ""))
                            state = STATE_ERROR
                            invokeCompletionCallback()
                        }
                    })
            request.send()
        }
    }

    /** Returns the given file.  */
    fun getEntry(index: Int): Entry {
        return entries[index]
    }

    // Returns true if all entries have been downloaded (all entries have contents).
    private fun areAllEntriesDone(): Boolean {
        for (entry in entries) {
            if (entry.contents == null) return false
        }
        return true
    }

    // Invokes the completion callback.
    private fun invokeCompletionCallback() {
        handler!!.post { listener!!.onPolyDownloadFinished(this@AsyncFileDownloader) }
    }

    /** Represents each file entry in the downloader.  */
    class Entry(
            /** The name of the file.  */
            val fileName: String,
            /** The URL where the file is to be fetched from.  */
            val url: String) {
        /** The contents of the file, if it has already been fetched. Otherwise, null.  */
        var contents: ByteArray? = null
    }

    companion object {
        private val TAG = "PolySample"
        const val STATE_NOT_STARTED = 0
        const val STATE_DOWNLOADING = 1
        const val STATE_SUCCESS = 2
        const val STATE_ERROR = 3
    }
}
