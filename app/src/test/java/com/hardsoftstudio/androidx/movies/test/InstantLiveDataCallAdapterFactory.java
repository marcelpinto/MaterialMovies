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

package com.hardsoftstudio.androidx.movies.test;

import com.hardsoftstudio.androidx.movies.model.api.LiveDataCallAdapterFactory;

import java.lang.reflect.Type;

import retrofit2.CallAdapter;

public class InstantLiveDataCallAdapterFactory extends LiveDataCallAdapterFactory {

    @Override
    protected CallAdapter<?, ?> getLiveDataCallAdapter(Type bodyType) {
        return new InstantLiveDataCallAdapter<>(bodyType);
    }
}
