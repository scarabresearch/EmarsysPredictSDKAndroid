/*
 * Copyright 2016 Scarab Research Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.emarsys.predict;

import android.support.annotation.Nullable;

/**
 * Interface definition for a callback to be invoked when the background processing finishes.
 */
public interface CompletionHandler {

    /**
     * Invoked on the UI thread after the background processing finishes.
     * The result of the background processing is passed as a parameter if the handler passed a
     * transaction.
     *
     * @param recommendationResult if the handler passed a transaction otherwise null
     */
    void onCompletion(@Nullable RecommendationResult recommendationResult);

}
