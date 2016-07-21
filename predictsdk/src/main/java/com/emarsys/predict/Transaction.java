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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.collections4.map.LinkedMap;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The transaction. Please send transaction instances only once.
 */
public class Transaction {

    /**
     * Holds query information for the request.
     */
    private class QueryParams extends HashMap {

        Object put(String key, int value) {
            return put(key, Integer.valueOf(value));
        }

        @Override
        public String toString() {
            List<String> l = new ArrayList<String>();
            for (Object next : keySet()) {
                l.add(next + "=" + get(next));
            }
            return StringUtil.toStringWithDelimiter(l, "&");
        }

    }

    private static final String TAG = Transaction.class.getSimpleName();

    private RecommendedItem trackedItem;

    private final List<AvailabilityZoneCommand> availabilityZones;
    private final List<CartCommand> carts;
    private final List<CategoryCommand> categories;
    private final List<KeywordCommand> keywords;
    private final List<PurchaseCommand> purchases;
    private final List<RecommendCommand> recommends;
    private final List<SearchTermCommand> searchTerms;
    private final List<TagCommand> tags;
    private final List<ViewCommand> views;

    private final List<ErrorParameter> errors;
    private final Map<String, CompletionHandler> handlers;

    /**
     * Creates a transaction.
     */
    public Transaction() {
        availabilityZones = new ArrayList<AvailabilityZoneCommand>();
        carts = new ArrayList<CartCommand>();
        categories = new ArrayList<CategoryCommand>();
        keywords = new ArrayList<KeywordCommand>();
        recommends = new ArrayList<RecommendCommand>();
        purchases = new ArrayList<PurchaseCommand>();
        searchTerms = new ArrayList<SearchTermCommand>();
        tags = new ArrayList<TagCommand>();
        views = new ArrayList<ViewCommand>();
        errors = new ArrayList<ErrorParameter>();
        handlers = new HashMap<String, CompletionHandler>();
    }

    /**
     * Creates a transaction, if the user comes from selected(clicked) a recommended item.
     *
     * @param item the previously selected item object.
     */
    public Transaction(@NonNull RecommendedItem item) {
        this();
        if (item == null) {
            throw new NullPointerException("The item cannot be null");
        }
        this.trackedItem = item;
    }

    /**
     * Set availability zone.
     * See javascript api reference for more explanation.
     *
     * @param availabilityZone ID of the availability zone.
     */
    public void availabilityZone(@NonNull String availabilityZone) {
        if (availabilityZone == null) {
            throw new NullPointerException("The availabilityZone cannot be null");
        }
        AvailabilityZoneCommand cmd = new AvailabilityZoneCommand(availabilityZone);
        availabilityZones.add(cmd);
    }

    /**
     * Report a purchase event.
     * This command should be called on the order confirmation page to report successful purchases.
     * Also make sure all purchased items are passed to the command.
     *
     * @param orderId ID of the purchase.
     * @param items   list of purchased objects.
     */
    public void purchase(@NonNull String orderId,
                         @NonNull List<CartItem> items) {
        if (orderId == null) {
            throw new NullPointerException("The orderId cannot be null");
        }
        if (items == null) {
            throw new NullPointerException("The items cannot be null");
        }
        PurchaseCommand cmd = new PurchaseCommand(orderId, items);
        purchases.add(cmd);
    }

    /**
     * Set search terms entered by visitor.
     * This call should be placed in the search results page.
     *
     * @param searchTerm search term entered by user.
     */
    public void searchTerm(@NonNull String searchTerm) {
        if (searchTerm == null) {
            throw new NullPointerException("The searchTerm cannot be null");
        }
        SearchTermCommand cmd = new SearchTermCommand(searchTerm);
        searchTerms.add(cmd);
    }

    /**
     * Report a product browsed by visitor.
     * This command should be placed in all product pages – i.e. pages showing a single item in
     * detail. Recommender features RELATED and ALSO_BOUGHT depend on this call.
     *
     * @param itemId ID of the viewed item (consistent with the item column specified in the
     *               catalog).
     */
    public void view(@NonNull String itemId) {
        if (itemId == null) {
            throw new NullPointerException("The itemId cannot be null");
        }
        ViewCommand cmd = new ViewCommand(itemId, trackedItem);
        views.add(cmd);
    }

    /**
     * Report the list of items in the visitor's shopping cart.
     * This command should be called on every page. Make sure all cart items are passed to the
     * command.
     * If the visitor's cart is empty, send the empty array.
     *
     * @param items list of cart objects.
     */
    public void cart(@Nullable List<CartItem> items) {
        CartCommand c = new CartCommand(items);
        carts.add(c);
    }

    /**
     * Report the category currently browsed by visitor.
     * Should be called on all category pages. Pass a valid category path.
     *
     * @param category category path.
     */
    public void category(@NonNull String category) {
        if (category == null) {
            throw new NullPointerException("The category cannot be null");
        }
        CategoryCommand cmd = new CategoryCommand(category);
        categories.add(cmd);
    }

    /**
     * Report the keyword used by visitors to refine their searches.
     * Brands, locations, price ranges are good examples of such keywords. If your site offers such
     * features, you can pass keywords to the recommender system with this command.
     *
     * @param keyword keyword selected by user.
     */
    public void keyword(@NonNull String keyword) {
        if (keyword == null) {
            throw new NullPointerException("The keyword cannot be null");
        }
        KeywordCommand cmd = new KeywordCommand(keyword);
        keywords.add(cmd);
    }

    /**
     * Add an arbitrary tag to the current event. The tag is collected and can be accessed later
     * from other Emarsys products.
     *
     * @param tag tag selected by user.
     */
    public void tag(@NonNull String tag) {
        if (tag == null) {
            throw new NullPointerException("The tag cannot be null");
        }
        TagCommand cmd = new TagCommand(tag);
        tags.add(cmd);
    }

    /**
     * Request recommendations.
     * See usage examples and the list of available recommendation strategies.
     *
     * @param request           recommendation request instance.
     */
    public void recommend(@NonNull RecommendationRequest request) {
        recommend(request, null);
    }

    /**
     * Request recommendations.
     * See usage examples and the list of available recommendation strategies.
     *
     * @param request           recommendation request instance.
     * @param completionHandler completion handler
     */
    public void recommend(@NonNull RecommendationRequest request,
                          @Nullable CompletionHandler completionHandler) {
        if (request == null) {
            throw new NullPointerException("The request cannot be null");
        }
        RecommendCommand cmd = new RecommendCommand(request);
        recommends.add(cmd);
        String key = request.getLogic();
        handlers.put(key, completionHandler);
    }

    String serialize() {
        errors.clear();

        // Validate commands
        validateCommands();

        QueryParams params = new QueryParams();

        // Handle customerId
        Session session = Session.getInstance();
        String customerId = session.getCustomerId();
        if (customerId != null) {
            if (customerId.isEmpty()) {
                ErrorParameter e = new ErrorParameter("INVALId_ARG",
                        "customer",
                        "Invalid argument in customer command: customer should not be an empty string");
                Log.d(TAG, e.toString());
                errors.add(e);
            }
            params.put("ci", customerId);
        }

        // Handle customerEmail
        String customerEmail = session.getCustomerEmail();
        if (customerEmail != null) {
            if (customerEmail.isEmpty()) {
                ErrorParameter e = new ErrorParameter("INVALId_ARG",
                        "email",
                        "Invalid argument in email command: email should not be an empty string");
                Log.d(TAG, e.toString());
                errors.add(e);
            }
            String sha1 = StringUtil.sha1(customerEmail.trim().toLowerCase());
            params.put("eh", sha1.toLowerCase().substring(0, 16) + "1");
        }

        // Handle keywords
        if (!keywords.isEmpty()) {
            KeywordCommand cmd = keywords.get(keywords.size() - 1);
            params.put("k", cmd.toString());
        }

        // Handle tags
        if (!tags.isEmpty()) {
            TagCommand cmd = tags.get(tags.size() - 1);
            params.put("t", cmd.toString());
        }

        // Handle availabilityZones
        if (!availabilityZones.isEmpty()) {
            AvailabilityZoneCommand cmd = availabilityZones.get(availabilityZones.size() - 1);
            params.put("az", cmd.toString());
        }

        // Handle carts
        if (!carts.isEmpty()) {
            params.put("cv", 1);
            CartCommand cmd = carts.get(carts.size() - 1);
            params.put("ca", cmd.toString());
        }

        // Handle categories
        if (!categories.isEmpty()) {
            CategoryCommand cmd = categories.get(categories.size() - 1);
            params.put("vc", cmd.toString());
        }

        // Handle purchases
        if (!purchases.isEmpty()) {
            PurchaseCommand cmd = purchases.get(purchases.size() - 1);
            params.put("co", cmd.toString());
            params.put("oi", cmd.getOrderId());
        }

        // Handle recommends
        List<String> features = new ArrayList<String>();
        List<String> baselines = new ArrayList<String>();
        List<Filter> filters = new ArrayList<Filter>();
        for (RecommendCommand next : recommends) {
            RecommendationRequest req = next.getRecommendationRequest();
            // Accumulate features
            features.add(next.toString());
            // Accumulate baselines
            if (req.getBaseline() != null) {
                String pi = req.getLogic() +
                        StringUtil.toStringWithDelimiter(req.getBaseline(), "|");
                baselines.add(pi);
            }
            // Accumulate filters
            filters.addAll(req.getFilters());
        }
        if (!features.isEmpty()) {
            // Append features
            params.put("f", StringUtil.toStringWithDelimiter(features, "|"));
        }
        if (!baselines.isEmpty()) {
            // Append baselines
            for (String next : baselines) {
                params.put("pi", next);
            }
        }
        if (!filters.isEmpty()) {
            // Append filters
            List<Map<String, String>> l = new ArrayList<Map<String, String>>();
            for (Filter next : filters) {
                Map<String, String> m = new HashMap<String, String>();
                m.put("f", next.catalogField);
                m.put("r", next.rule);
                m.put("v", StringUtil.toStringWithDelimiter(next.values, "|"));
                m.put("n", (next instanceof ExcludeCommand) ? "false" : "true");
                l.add(m);
            }
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            params.put("ex", gson.toJson(l));
        }

        // Handle searchTerms
        if (!searchTerms.isEmpty()) {
            SearchTermCommand cmd = searchTerms.get(searchTerms.size() - 1);
            params.put("q", cmd.toString());
        }

        // Handle views
        if (!views.isEmpty()) {
            ViewCommand cmd = views.get(views.size() - 1);
            params.put("v", cmd.toString());
        }

        // MAGIC
        params.put("cp", 1);

        // Handle advertiserId
        String advertisingIdentifier = IdentifierManager.getInstance().getAdvertisingIdentifier();
        if (advertisingIdentifier != null) {
            params.put("vi", advertisingIdentifier);
        }

        // Handle session
        String sessionId = session.getSession();
        if (sessionId != null) {
            params.put("s", sessionId);
        }

        // Append errors
        if (!errors.isEmpty()) {
            List<Map<String, String>> l = new ArrayList<Map<String, String>>();
            for (ErrorParameter next : errors) {
                Map<String, String> m = new LinkedMap<String, String>();
                m.put("t", next.type);
                m.put("c", next.command);
                m.put("m", next.message);
                l.add(m);
            }
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            params.put("error", gson.toJson(l));
        }

        return params.toString();
    }

    private void validateCommands() {
        // This commands must be unique
        validateCommandArray(availabilityZones, "availabilityZone");
        validateCommandArray(keywords, "keyword");
        validateCommandArray(carts, "cart");
        validateCommandArray(categories, "category");
        validateCommandArray(purchases, "purchase");
        validateCommandArray(searchTerms, "searchTerm");
        validateCommandArray(tags, "tag");
        validateCommandArray(views, "view");
        // Validate recommends array
        if (recommends.size() != handlers.size()) {
            // The logic is not unique
            throw new Error("The recommend logic must be unique inner transaction",
                    Error.ERROR_NON_UNIQUE_RECOMMENDATION_LOGIC, null);
        }
        for (RecommendCommand next : recommends) {
            errors.addAll(next.validate());
        }
    }

    private void validateCommandArray(List<?> commands, String command) {
        // Validate multiple calls
        if (commands.size() > 1) {
            ErrorParameter e = new ErrorParameter("MULTIPLE_CALL", command,
                    "Multiple calls of " + command + " command");
            errors.add(e);
        }
        // Validate all commands in the array
        for (Object next : commands) {
            errors.addAll(((Command) next).validate());
        }
    }

    void handleResults(List<RecommendationResult> results) {
        for (RecommendationResult next : results) {
            String key = next.getFeatureId();
            if (key != null) {
                if (handlers.containsKey(key)) {
                    Log.d(TAG, "Found handler for result " + key);
                    handlers.get(key).onCompletion(next);
                } else {
                    Log.e(TAG, "Completion handler is missing for the feature " + key +
                            ", drop result");
                }
            } else {
                Log.d(TAG, "Transaction sent without recommend drop this result");
            }
        }
    }

}
