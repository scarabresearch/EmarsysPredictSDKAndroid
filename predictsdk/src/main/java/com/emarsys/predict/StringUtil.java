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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Provides some commonly used methods.
 */
class StringUtil {

    /**
     * Constructs a new String from the elements of input list separated elements with delimiter.
     *
     * @param l         items for the construction
     * @param delimiter the delimiter
     * @return the constructed string
     */
    static String toStringWithDelimiter(Collection<?> l, String delimiter) {
        String ret = "";
        if (l != null && !l.isEmpty()) {
            Iterator<?> i = l.iterator();
            while (i.hasNext()) {
                ret += i.next() + delimiter;
            }
            ret = ret.substring(0, ret.length() - delimiter.length());
        }
        return ret;
    }

    /**
     * Calculates the SHA-1 digest and returns the value as String
     *
     * @param s data to digest
     * @return SHA-1 digest
     */
    static String sha1(String s) {
        return new String(Hex.encodeHex(DigestUtils.sha(s)));
    }

}
