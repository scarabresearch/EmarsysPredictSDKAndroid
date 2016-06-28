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

/**
 * Errors
 * The errors are passed as a parameter in the ErrorHandler when the background processing failed.
 * Other errors will throw this exceptions at method call time.
 */
public class Error extends RuntimeException {

    /**
     * An unknown error has occurred.
     */
    public static final int ERROR_UNKNOWN = -1;
    /**
     * CDV cookie was not present in the HTTP response.
     */
    public static final int ERROR_MISSING_CDV_COOKIE = -995;
    /**
     * HTTP response returned bigger than code 300.
     */
    public static final int ERROR_BAD_HTTP_STATUS = -996;
    /**
     * HTTP response is missing an expected JSON key.
     */
    public static final int ERROR_MISSING_JSON_PARAMETER = -997;
    /**
     * Merchant ID was not set.
     */
    public static final int ERROR_MISSING_MERCHANT_ID = -998;
    /**
     * Non-unique recommendation logic was used inside a transaction.
     */
    public static final int ERROR_NON_UNIQUE_RECOMMENDATION_LOGIC = -999;

    private final int errorCode;

    Error(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code
     *
     * @return errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }

}
