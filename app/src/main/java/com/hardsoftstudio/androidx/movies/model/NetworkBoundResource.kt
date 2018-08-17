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

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.hardsoftstudio.androidx.movies.model.api.ApiResponse
import java.lang.Exception

/**
 * A generic class that can provide a {@link Resource} backed by persistence storage (db or cache) and network.
 *
 * You can read more about it in the [Architecture Guide](https://developer.android.com/arch).
 *
 * @param <ResultType> the type to return the result
 * @param <RequestType> the call or saved result response type
 * @param <ErrorType> the type of the error in case of failure
 */
abstract class NetworkBoundResource<ResultType, RequestType, ErrorType>
internal constructor(private val platform: Platform) {

    private val result = NetworkMediatorLiveData()

    /**
     * Return the LiveData that will observe the fetched information, either from persistence storage or network
     * The first call to this method will trigger the loading.
     *
     * @return the LiveData containing the Resource of the defined ResultType.
     */
    fun get(): LiveData<Resource<ResultType, ErrorType>> {
        if (result.value == null) {
            val dbSource = fetchFromPersistence()
            result.addSource(dbSource) { data ->
                result.removeSource(dbSource)
                if (shouldFetch(data)) {
                    fetchFromNetwork(dbSource)
                } else {
                    observeSource(dbSource)
                }
            }
        }
        return result
    }

    /**
     * Called when there is no observer listening to this resource. Override it to handle cases where you want to
     * dispose this object or other resources.
     */
    open fun onDisposed() {}

    /**
     * Called with the Api Success from the call. Override it to handle special cases with the response.
     */
    @WorkerThread
    open fun processSuccess(response: ApiResponse.Completed.Success<RequestType>): RequestType? {
        return response.body
    }

    /**
     * Called when the call completed with an error. Return the desired error type
     */
    @WorkerThread
    protected abstract fun processError(response: ApiResponse.Completed.Error<RequestType>): ErrorType

    /**
     * Called when the call failed with an exception. Return the desired error type
     */
    @WorkerThread
    protected abstract fun processFailure(throwable: Throwable): ErrorType

    /**
     * Called when the request was successfully fetched and the result should be persisted.
     */
    @WorkerThread
    protected abstract fun saveResult(result: RequestType)

    /**
     * Called to check if the data should be fetched from network or the persisted one is valid.
     *
     * @return true to use the persisted data, false to fetch again
     */
    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    /**
     * Request from persistence storage the data.
     *
     * @return the LiveData that observe the persistence storage for the request.
     */
    @MainThread
    protected abstract fun fetchFromPersistence(): LiveData<ResultType>

    /**
     * Create the call to use when fetching from network.
     *
     * @return the LiveData that observe the ApiResponse of the created call.
     */
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

    /**
     * Create the call add fetch from network.
     *
     * Forward the result into the live data value.
     */
    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()

        // Since we don't support loading this is not needed for now
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        // result.addSource(dbSource) { newData -> result.value = Resource.loading(newData) }

        result.addSource(apiResponse) { response ->
            if (response == null) {
                return@addSource
            }
            result.removeSource(apiResponse)
            result.removeSource(dbSource)

            when (response) {
                is ApiResponse.Completed.Success -> {
                    platform.postOnDisk {
                        processSuccess(response)?.let {
                            saveResult(it)
                        }
                        platform.postOnMain {
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            observeSource(fetchFromPersistence())
                        }
                    }
                }
                is ApiResponse.Completed.Error -> result.value = Resource.Failed(processError(response), dbSource.value)
                is ApiResponse.Failed -> {
                    result.value = Resource.Failed(processFailure(response.throwable), dbSource.value)
                }
            }
        }
    }

    /**
     * Add an observer to the source, this source provides the resource result.
     */
    private fun observeSource(source: LiveData<ResultType>) {
        result.addSource(source) { newData ->
            result.value = if (newData == null) {
                Resource.Failed(processFailure(MissingResourceException()), result.value.getOrNull())
            } else {
                Resource.Completed(newData)
            }
        }
    }

    /**
     * Thrown when the resource is not found in the Database after fetching
     */
    class MissingResourceException : Exception()

    /**
     * Override the onInactive to notify the parent class that no one else is listening so it can be disposed.
     */
    private inner class NetworkMediatorLiveData : MediatorLiveData<Resource<ResultType, ErrorType>>() {
        override fun onInactive() {
            super.onInactive()
            onDisposed()
        }
    }
}
