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

package com.hardsoftstudio.androidx.movies.model.api

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse.
 */
class LiveDataCallAdapter<T>(private val responseType: Type) : CallAdapter<T, LiveData<ApiResponse<T>>> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<T>): LiveData<ApiResponse<T>> {
        return object : LiveData<ApiResponse<T>>() {
            var started = AtomicBoolean(false)

            override fun onActive() {
                super.onActive()
                if (call.isCanceled) {
                    postValue(ApiResponse.Failed(CancellationException()))
                } else if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<T> {
                        override fun onResponse(call: Call<T>, response: Response<T>) {
                            postValue(if (response.isSuccessful) {
                                ApiResponse.Completed.Success(response.body())
                            } else {
                                ApiResponse.Completed.Error(response.code(), response.message(), response.errorBody())
                            })
                        }

                        override fun onFailure(call: Call<T>, throwable: Throwable) {
                            postValue(ApiResponse.Failed(throwable))
                        }
                    })
                }
            }

            override fun onInactive() {
                call.cancel()
            }
        }
    }
}
