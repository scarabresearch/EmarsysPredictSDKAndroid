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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The result item.
 */
public class RecommendedItem implements Serializable {

    private final Map<String, Object> data;
    private final RecommendationResult result;

    RecommendedItem(RecommendationResult result) {
        this.result = result;
        data = new HashMap<String, Object>();
    }

    void addField(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Return the recommended item record. Keys are fields copied from the product catalog.
     *
     * @return data
     */
    @NonNull
    public Map<String, Object> getData() {
        return data;
    }

    RecommendationResult getResult() {
        return result;
    }

    @Override
    public String toString() {
        List<String> l = new ArrayList<String>();
        for (String key : data.keySet()) {
            l.add(key + " = " + data.get(key));
        }
        return l.isEmpty() ? super.toString() : StringUtil.toStringWithDelimiter(l, ", ");
    }

}
