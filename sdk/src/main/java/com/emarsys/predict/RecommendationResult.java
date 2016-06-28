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
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The recommendation result.
 * Contains the recommended items and their product information.
 */
public class RecommendationResult implements Serializable {

    RecommendationResult(String cohort, String featureId, String topic) {
        this.cohort = cohort;
        this.featureId = featureId;
        this.topic = topic;
        products = new ArrayList<RecommendedItem>();
    }

    void addProduct(RecommendedItem product) {
        products.add(product);
    }

    private final List<RecommendedItem> products;

    /**
     * Return the list of recommended items.
     * Each element is an object representing the recommended item with fields copied from the
     * product catalog.
     *
     * @return products
     */
    @NonNull
    public List<RecommendedItem> getProducts() {
        return products;
    }

    private final String cohort;

    /**
     * Return the cohort of the recommendations.
     * In case of A/B tests this contains the algorithm ID that served the recommendations. Eg. it
     * may contain the string "EMARSYS" if it was served by our algorithm, and "BASELINE" if it's
     * your original algorithm that we are testing against.
     *
     * @return cohort
     */
    @NonNull
    public String getCohort() {
        return cohort;
    }

    private final String topic;

    /**
     * Returns the topic of the recommended items.
     * A category path.
     *
     * @return topic
     */
    @Nullable
    public String getTopic() {
        return topic;
    }

    private final String featureId;

    /**
     * Returns the feature ID of the recommendations.
     *
     * @return feature ID
     */
    @NonNull
    public String getFeatureId() {
        return featureId;
    }

}
