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

import java.util.ArrayList;
import java.util.List;

/**
 * The recommendation request.
 *
 * RecommendationRequests are not reusable.
 */
public class RecommendationRequest {

    /**
     * Creates a RecommendationRequest.
     *
     * @param logic the recommendation strategy to be used. Eg. recommend similar products
     *              (RELATED), or show personal recommendations (PERSONAL), etc.
     */
    public RecommendationRequest(@NonNull String logic) {
        if (logic == null) {
            throw new NullPointerException("The logic cannot be null");
        }
        this.logic = logic;
        limit = 5;
    }

    private final String logic;

    /**
     * Returns the recommendation logic.
     *
     * @return logic
     */
    @NonNull
    public String getLogic() {
        return logic;
    }

    private int limit;

    /**
     * Returns the number of items to recommend. Default: 5.
     *
     * @return number of items to recommend
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Sets the number of items to recommend.
     *
     * @param limit number of items to recommend
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    private List<String> baseline;

    /**
     * Returns the item IDs of the original recommendations. Only used when an A/B test is in
     * progress to compare the performance of the EmarsysPredictSDK recommendations vs. the
     * original (baseline) recommendations.
     *
     * @return baseline
     */
    @Nullable
    public List<String> getBaseline() {
        return baseline;
    }

    /**
     * Set the baseline of the original recommendations. Only used when an A/B test
     * is in progress to compare the performance of the EmarsysPredictSDK recommendations vs. the
     * original (baseline) recommendations.
     *
     * @param baseline item IDs of the original recommendations
     */
    public void setBaseline(@Nullable List<String> baseline) {
        this.baseline = baseline;
    }

    private final List<Filter> filters = new ArrayList<Filter>();

    List<Filter> getFilters() {
        return filters;
    }

    /**
     * Set exclude criteria.
     * Exclude items where catalog field value is exactly the given value.
     *
     * @param catalogField catalog field name
     * @param value        value to compare against
     */
    public void excludeItemsWhereIs(@NonNull String catalogField,
                                    @NonNull String value) {
        if (catalogField == null) {
            throw new NullPointerException("The catalogField cannot be null");
        }
        if (value == null) {
            throw new NullPointerException("The value cannot be null");
        }
        Filter f = new ExcludeCommand(value, "IS", catalogField);
        filters.add(f);
    }

    /**
     * Set exclude criteria.
     * Exclude items where catalog field value is contained in the given list of values.
     *
     * @param catalogField catalog field name
     * @param values       values to compare against
     */
    public void excludeItemsWhereIn(@NonNull String catalogField,
                                    @NonNull List<String> values) {
        if (catalogField == null) {
            throw new NullPointerException("The catalogField cannot be null");
        }
        if (values == null) {
            throw new NullPointerException("The values cannot be null");
        }
        Filter f = new ExcludeCommand(values, "IN", catalogField);
        filters.add(f);
    }

    /**
     * Set exclude criteria.
     * Exclude items where catalog field (a | separated list) contains the given value.
     *
     * @param catalogField catalog field name
     * @param value        value to compare against
     */
    public void excludeItemsWhereHas(@NonNull String catalogField,
                                     @NonNull String value) {
        if (catalogField == null) {
            throw new NullPointerException("The catalogField cannot be null");
        }
        if (value == null) {
            throw new NullPointerException("The value cannot be null");
        }
        Filter f = new ExcludeCommand(value, "HAS", catalogField);
        filters.add(f);
    }

    /**
     * Set exclude criteria.
     * Exclude items where catalog field (a | separated list) overlaps with the given list of
     * values.
     *
     * @param catalogField catalog field name
     * @param values       values to compare against
     */
    public void excludeItemsWhereOverlaps(@NonNull String catalogField,
                                          @NonNull List<String> values) {
        if (catalogField == null) {
            throw new NullPointerException("The catalogField cannot be null");
        }
        if (values == null) {
            throw new NullPointerException("The values cannot be null");
        }
        Filter f = new ExcludeCommand(values, "OVERLAPS", catalogField);
        filters.add(f);
    }

    /**
     * Set include criteria.
     * Include items where catalog field value is exactly the given value.
     *
     * @param catalogField catalog field name
     * @param value        value to compare against
     */
    public void includeItemsWhereIs(@NonNull String catalogField,
                                    @NonNull String value) {
        if (catalogField == null) {
            throw new NullPointerException("The catalogField cannot be null");
        }
        if (value == null) {
            throw new NullPointerException("The value cannot be null");
        }
        Filter f = new IncludeCommand(value, "IS", catalogField);
        filters.add(f);
    }

    /**
     * Set include criteria.
     * Include items where catalog field value is contained in the given list of values.
     *
     * @param catalogField catalog field name
     * @param values       values to compare against
     */
    public void includeItemsWhereIn(@NonNull String catalogField,
                                    @NonNull List<String> values) {
        if (catalogField == null) {
            throw new NullPointerException("The catalogField cannot be null");
        }
        if (values == null) {
            throw new NullPointerException("The values cannot be null");
        }
        Filter f = new IncludeCommand(values, "IN", catalogField);
        filters.add(f);
    }

    /**
     * Set include criteria.
     * Include items where catalog field (a | separated list) contains the given value.
     *
     * @param catalogField catalog field name
     * @param value        value to compare against
     */
    public void includeItemsWhereHas(@NonNull String catalogField,
                                     @NonNull String value) {
        if (catalogField == null) {
            throw new NullPointerException("The catalogField cannot be null");
        }
        if (value == null) {
            throw new NullPointerException("The value cannot be null");
        }
        Filter f = new IncludeCommand(value, "HAS", catalogField);
        filters.add(f);
    }

    /**
     * Set include criteria.
     * Include items where catalog field (a | separated list) overlaps with the given array of
     * values.
     *
     * @param catalogField catalog field name
     * @param values       values to compare against
     */
    public void includeItemsWhereOverlaps(@NonNull String catalogField,
                                          @NonNull List<String> values) {
        if (catalogField == null) {
            throw new NullPointerException("The catalogField cannot be null");
        }
        if (values == null) {
            throw new NullPointerException("The values cannot be null");
        }
        Filter f = new IncludeCommand(values, "OVERLAPS", catalogField);
        filters.add(f);
    }

}
