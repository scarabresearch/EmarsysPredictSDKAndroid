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

import android.support.annotation.NonNull;

/**
 * Interface definition of a callback to be invoked when there has been an error during in the
 * asynchronous operation (other errors will throw exceptions at method call time).
 */
public interface ErrorHandler {

    /**
     * Invoked on the UI thread when the background processing failed.
     * The error is passed as a parameter.
     *
     * @param error error that occurred
     */
    void onError(@NonNull Error error);

}
