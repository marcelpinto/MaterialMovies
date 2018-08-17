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

/**
 * A generic class that wraps the loading of a resource.
 *
 * @param <ResourceType> the type of the data that contains this resource
 * @param <ErrorType> the type of the error in case the resource failed
 */
sealed class Resource<out ResourceType, out ErrorType> {

    data class Completed<out R, out E>(val data: R) : Resource<R, E>()
    data class Failed<out R, out E>(val error: E, val cachedData: R? = null) : Resource<R, E>()
}

fun <R> Resource<R, *>?.getOrNull() = (this as? Resource.Completed<R, *>)?.data
