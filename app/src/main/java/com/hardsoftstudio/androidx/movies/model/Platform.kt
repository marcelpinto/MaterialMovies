/*
 * Copyright (c) 2016 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hardsoftstudio.androidx.movies.model

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.os.postDelayed
import java.util.concurrent.Executors


interface Platform {
    //fun getClock(): Clock

    //fun currentTimeMs(): Long = getClock().millis()

    fun postOnMain(delayMs: Long = 0, tag: Any? = null, action: () -> Unit)

    fun removeFromMain(tag: Any?)

    fun postOnDisk(action: () -> Unit)

    fun showToast(message: String, short: Boolean = true) {}
}

class AndroidPlatform(val context: Context) : Platform {

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val diskExecutor = Executors.newSingleThreadExecutor()

    override fun postOnMain(delayMs: Long, tag: Any?, action: () -> Unit) {
        mainThreadHandler.postDelayed(delayMs, tag, action)
    }

    override fun removeFromMain(tag: Any?) {
        mainThreadHandler.removeCallbacksAndMessages(tag)
    }

    override fun postOnDisk(action: () -> Unit) {
        diskExecutor.execute(action)
    }

    override fun showToast(message: String, short: Boolean) {
        Toast.makeText(context, message, if (short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()
    }
}

