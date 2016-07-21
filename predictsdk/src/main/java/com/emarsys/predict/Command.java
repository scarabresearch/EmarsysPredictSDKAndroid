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
import java.util.Arrays;
import java.util.List;

/**
 * Base wrapper class for the commands.
 */
abstract class Command {

    List<ErrorParameter> validate() {
        return new ArrayList<ErrorParameter>();
    }

    ErrorParameter createEmptyStringErrorParameter(String command, String field) {
        String msg = "Invalid argument in " + command + " command: " + field
                + " should not be an empty string";
        Log.d(getClass().getSimpleName(), msg);
        return new ErrorParameter("INVALID_ARG", command, msg);
    }

}

/**
 * Base wrapper class for the rules.
 */
abstract class Filter extends Command {

    String catalogField;
    String rule;
    List<String> values;

    Filter(List<String> values, String rule, String catalogField) {
        this.values = values;
        this.rule = rule;
        this.catalogField = catalogField;
    }

    Filter(String value, String rule, String catalogField) {
        this(Arrays.asList(value), rule, catalogField);
    }

}

/**
 * Wraps the exclude rules.
 */
class ExcludeCommand extends Filter {

    ExcludeCommand(List<String> values, String rule, String catalogField) {
        super(values, rule, catalogField);
    }

    ExcludeCommand(String value, String rule, String catalogField) {
        super(value, rule, catalogField);
    }

    @Override
    List<ErrorParameter> validate() {
        List<ErrorParameter> ret = new ArrayList<ErrorParameter>();
        for (String next : values) {
            if (next.isEmpty()) {
                ret.add(createEmptyStringErrorParameter("exclude", catalogField));
            }
        }
        return ret;
    }

}

/**
 * Wraps the include rules.
 */
class IncludeCommand extends Filter {

    IncludeCommand(List<String> values, String rule, String catalogField) {
        super(values, rule, catalogField);
    }

    IncludeCommand(String value, String rule, String catalogField) {
        super(value, rule, catalogField);
    }

    @Override
    List<ErrorParameter> validate() {
        List<ErrorParameter> ret = new ArrayList<ErrorParameter>();
        for (String next : values) {
            if (next.isEmpty()) {
                ret.add(createEmptyStringErrorParameter("include", catalogField));
            }
        }
        return ret;
    }

}

/**
 * Wraps the cart command.
 */
class CartCommand extends Command {

    private final List<CartItem> items;

    CartCommand(List<CartItem> items) {
        this.items = items;
    }

    @Override
    List<ErrorParameter> validate() {
        List<ErrorParameter> ret = new ArrayList<ErrorParameter>();
        for (CartItem next : items) {
            if (next.getItemId().isEmpty()) {
                ret.add(createEmptyStringErrorParameter("cart", "itemId"));
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        List<String> l = new ArrayList<String>();
        for (CartItem next : items) {
            String s = "";
            s += "i:";
            s += next.getItemId();
            s += ",p:";
            s += next.getPrice();
            s += ",q:";
            s += next.getQuantity();
            l.add(s);
        }
        return StringUtil.toStringWithDelimiter(l, "|");
    }

}

/**
 * Base wrapper class for the simple string commands.
 */
class StringCommand extends Command {

    String value;

    StringCommand(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}

/**
 * Wraps the availabilityZone command.
 */
class AvailabilityZoneCommand extends StringCommand {

    AvailabilityZoneCommand(String value) {
        super(value);
    }

    @Override
    List<ErrorParameter> validate() {
        List<ErrorParameter> ret = new ArrayList<ErrorParameter>();
        if (value.isEmpty()) {
            ret.add(createEmptyStringErrorParameter("availabilityZone", "availabilityZone"));
        }
        return ret;
    }

}

/**
 * Wraps the category command.
 */
class CategoryCommand extends StringCommand {

    CategoryCommand(String value) {
        super(value);
    }

    @Override
    List<ErrorParameter> validate() {
        List<ErrorParameter> ret = new ArrayList<ErrorParameter>();
        if (value.isEmpty()) {
            ret.add(createEmptyStringErrorParameter("category", "category"));
        }
        return ret;
    }

}

/**
 * Wraps the keyword command.
 */
class KeywordCommand extends StringCommand {

    KeywordCommand(String value) {
        super(value);
    }

    @Override
    List<ErrorParameter> validate() {
        List<ErrorParameter> ret = new ArrayList<ErrorParameter>();
        if (value.isEmpty()) {
            ret.add(createEmptyStringErrorParameter("keyword", "keyword"));
        }
        return ret;
    }

}

/**
 * Wraps the tag command.
 */
class TagCommand extends StringCommand {

    TagCommand(String value) {
        super(value);
    }

    @Override
    List<ErrorParameter> validate() {
        List<ErrorParameter> ret = new ArrayList<ErrorParameter>();
        if (value.isEmpty()) {
            ret.add(createEmptyStringErrorParameter("tag", "tag"));
        }
        return ret;
    }

}

/**
 * Wraps the purchase command.
 */
class PurchaseCommand extends CartCommand {

    PurchaseCommand(String orderId, List<CartItem> items) {
        super(items);
        this.orderId = orderId;
    }

    private final String orderId;

    String getOrderId() {
        return orderId;
    }

    @Override
    List<ErrorParameter> validate() {
        List<ErrorParameter> ret = new ArrayList<ErrorParameter>();
        if (orderId.isEmpty()) {
            ret.add(createEmptyStringErrorParameter("purchase", "orderId"));
        }
        return ret;
    }

}

/**
 * Wraps the searchTerm command.
 */
class SearchTermCommand extends StringCommand {

    SearchTermCommand(String value) {
        super(value);
    }

    @Override
    List<ErrorParameter> validate() {
        List<ErrorParameter> ret = new ArrayList<ErrorParameter>();
        if (value.isEmpty()) {
            ret.add(createEmptyStringErrorParameter("searchTerm", "searchTerm"));
        }
        return ret;
    }

}

/**
 * Wraps the view command.
 */
class ViewCommand extends Command {

    private final String itemId;
    private final RecommendedItem trackedItem;

    ViewCommand(String itemId, RecommendedItem trackedItem) {
        this.itemId = itemId;
        this.trackedItem = trackedItem;
    }

    @Override
    List<ErrorParameter> validate() {
        List<ErrorParameter> ret = new ArrayList<ErrorParameter>();
        if (itemId.isEmpty()) {
            ret.add(createEmptyStringErrorParameter("view", "itemId"));
        }
        return ret;
    }

    @Override
    public String toString() {
        String ret = "";
        ret += "i:";
        ret += itemId;
        if (trackedItem != null) {
            ret += ",t:";
            ret += trackedItem.getResult().getFeatureId();
            ret += ",c:";
            ret += trackedItem.getResult().getCohort();
        }
        return ret;
    }

}

/**
 * Wraps the recommend command.
 */
class RecommendCommand extends Command {

    RecommendCommand(RecommendationRequest recommendationRequest) {
        this.recommendationRequest = recommendationRequest;
    }

    private final RecommendationRequest recommendationRequest;

    RecommendationRequest getRecommendationRequest() {
        return recommendationRequest;
    }

    @Override
    List<ErrorParameter> validate() {
        List<ErrorParameter> ret = new ArrayList<ErrorParameter>();
        // Validate logic
        if (recommendationRequest.getLogic().isEmpty()) {
            ret.add(createEmptyStringErrorParameter("recommend", "logic"));
        }
        // Validate all baselines
        if (recommendationRequest.getBaseline() != null) {
            for (String next : recommendationRequest.getBaseline()) {
                if (next.isEmpty()) {
                    ret.add(createEmptyStringErrorParameter("recommend", "baseline"));
                }
            }
        }
        // Validate all filters
        for (Filter next : recommendationRequest.getFilters()) {
            if (next.catalogField.isEmpty()) {
                ret.add(createEmptyStringErrorParameter(
                        (next instanceof IncludeCommand) ? "include" : "exclude", "catalogField")
                );
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        String ret = "";
        ret += "f:";
        ret += recommendationRequest.getLogic();
        ret += ",l:";
        ret += recommendationRequest.getLimit();
        ret += ",o:";
        ret += 0;
        return ret;
    }

}
