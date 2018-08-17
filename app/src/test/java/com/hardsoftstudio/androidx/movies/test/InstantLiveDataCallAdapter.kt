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

package com.hardsoftstudio.androidx.movies.test

import androidx.lifecycle.LiveData
import com.hardsoftstudio.androidx.movies.model.api.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse
 * executing it on the same caller thread.
 */
class InstantLiveDataCallAdapter<T>(private val responseType: Type) : CallAdapter<T, LiveData<ApiResponse<T>>> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<T>): LiveData<ApiResponse<T>> {
        return object : LiveData<ApiResponse<T>>() {
            var started = AtomicBoolean(false)

            override fun onActive() {
                super.onActive()
                if (call.isCanceled) {
                    value = ApiResponse.Failed(CancellationException())
                } else if (started.compareAndSet(false, true)) {
                    value = try {
                        val response = call.execute()
                        if (response.isSuccessful) {
                            ApiResponse.Completed.Success(response.body())
                        } else {
                            ApiResponse.Completed.Error(response.code(), response.message(), response.errorBody())
                        }
                    } catch (e: Exception) {
                        ApiResponse.Failed(e)
                    }
                }
            }

            override fun onInactive() {
                call.cancel()
            }
        }
    }
}
