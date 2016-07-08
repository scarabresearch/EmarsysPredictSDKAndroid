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

import java.util.HashMap;
import java.util.Map;

public class SessionHelper {
    private static final Map<String, Object> MAP;

    static {
        MAP = new HashMap<String, Object>();
        Session.initialize(new Storage() {

            @Override
            public void put(String key, Object value) {
                MAP.put(key, value);
            }

            @Override
            public Object get(String key) {
                return MAP.get(key);
            }

        });
    }

    public static void reset() {
        MAP.clear();
    }
}
