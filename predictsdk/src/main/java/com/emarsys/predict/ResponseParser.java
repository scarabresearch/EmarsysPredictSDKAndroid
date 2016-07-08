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

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes the server json response.
 */
class ResponseParser {

    private static final String TAG = ResponseParser.class.getSimpleName();

    private final String cohort;
    private final String visitor;
    private final String session;
    private final List<RecommendationResult> results;

    private String valueForKey(Map<String, Object> json, String key) {
        if (json.containsKey(key)) {
            Object o = json.get(key);
            if (o instanceof String) {
                Log.d(TAG, "Found " + key + " " + o);
                return (String) o;
            }
        }
        throw new Error("Missing '" + key + "' parameter",
                Error.ERROR_MISSING_JSON_PARAMETER, null);
    }

    ResponseParser(Map<String, Object> json) {
        Log.d(TAG, "Parse json");
        Log.d(TAG, json.toString());
        // Get cohort
        cohort = valueForKey(json, "cohort");
        // Get visitor
        visitor = valueForKey(json, "visitor");
        // Get session
        session = valueForKey(json, "session");
        // Parse features
        Map<String, Object> features = (Map<String, Object>) json.get("features");
        Log.d(TAG, "Found " + features.size() + " elements in the features");
        // Create results
        results = new ArrayList<RecommendationResult>();
        // Get schema
        List<String> schema = new ArrayList<String>();
        if (json.containsKey("schema")) {
            schema = (List<String>) json.get("schema");
        }
        Log.d(TAG, "Found " + schema.size() + " elements in the schema");
        // Found schema, read products
        Map<String, Object> products = new HashMap<String, Object>();
        if (json.containsKey("products")) {
            products = (Map<String, Object>) json.get("products");
        }
        Log.d(TAG, "Found " + products.size() + " elements in the products");
        // Iterate on the all features
        for (String key : features.keySet()) {
            Map<String, Object> obj = (Map<String, Object>) features.get(key);
            // Create next result
            RecommendationResult result = new RecommendationResult(cohort,
                    key,
                    (String) obj.get("topicLabel")
            );
            // Map items
            List<Map<String, Object>> itemIndexes = (List<Map<String, Object>>) obj.get("items");
            // Iterate on the all indexes
            for (Map<String, Object> itemIndex : itemIndexes) {
                Object itemIndexValue = itemIndex.get("id");
                // Get values for the index
                List<String> itemValues = (List<String>) products.get(itemIndexValue);
                RecommendedItem item = new RecommendedItem(result);
                // Iterate on the all keys
                for (int i = 0; i < schema.size(); i++) {
                    item.addField(schema.get(i), itemValues.get(i));
                }
                result.addProduct(item);
            }
            Log.d(TAG, "Created " + result.getProducts().size() + " results in the feature "
                    + result.getFeatureId());
            results.add(result);
        }
        Log.d(TAG, "Created " + results.size() + " features");

    }

    String getSession() {
        return session;
    }

    String getVisitor() {
        return visitor;
    }

    List<RecommendationResult> getResults() {
        return results;
    }

}
