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

import okhttp3.ResponseBody

/**
 * Common class used by API responses.
 *
 * @param <T> the result type of the response. Normally the body of the http response
 */
sealed class ApiResponse<out R> {

    sealed class Completed<out R> : ApiResponse<R>() {
        data class Success<out R>(val body: R?) : Completed<R>()
        data class Error<out R>(val code: Int, val errorMessage: String, val errorBody: ResponseBody?) : Completed<R>()
    }

    data class Failed<out R>(val throwable: Throwable) : ApiResponse<R>()
}
