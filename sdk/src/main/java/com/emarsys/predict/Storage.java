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
 * Interface for accessing and modifying save and retrieve persistent key-value pairs.
 */
public interface Storage {

    /**
     * Set a value in the storage for the key. If the storage previously contained a mapping for
     * the
     * key, the old value is replaced by the specified value.
     *
     * @param key   the key with which the specified value is to be stored
     * @param value value to be stored with the specified key
     */
    void put(String key, Object value);

    /**
     * Returns the value to which the specified key is stored, or null if the store not contains
     * value for the key.
     *
     * @param key the key whose stored value is to be returned
     * @return the value to which the specified key is stored, or null
     */
    Object get(String key);

}
