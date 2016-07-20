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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class JSApiSamples {

    private static final String TAG = JSApiSamples.class.getSimpleName();

    @BeforeClass
    public static void setUp() {
        // Init the Session
        SessionHelper.reset();
    }

    @Before
    public void setMerchantId() {
        // These parts of our API should be implemented on ALL website pages
        Session session = Session.getInstance();
        // Identifies the merchant account (here the emarsys demo merchant 1A65B5CB868AFF1E).
        // Replace it with your own Merchant Id before run.
        session.setMerchantId("1A74F439823D2CB4");
        assertEquals(session.getMerchantId(), "1A74F439823D2CB4");
        Session.getInstance().setSecure(true);
    }

    /**
     * Port of JavaScript API sample
     * http://jsfiddle.net/0xrprjrz/1/
     */
    @Test
    public void sample1() {
        // Identifying the visitor. This email is matched with contact DB.
        // As you can see in LIVE EVENTS, the email is hashed by our API
        Session.getInstance().setCustomerEmail("visitor@test-mail.com");

        // Passing on visitor's cart contents to feed cart abandonment campaigns
        List<CartItem> items = new ArrayList<CartItem>();
        items.add(new CartItem("item_1", 19.9f, 1));
        items.add(new CartItem("item_2", 29.7f, 3));
        Transaction t = new Transaction();
        t.cart(items);

        // Firing the EmarsysPredictSDKQueue. Should be the last call on the page, called only once.
        Session.getInstance().sendTransaction(t, new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                Log.d(TAG, error.toString());
            }
        });
    }

    /**
     * Port of JavaScript API sample
     * http://jsfiddle.net/px15qkk4/
     */
    @Test
    public void sample2() {
        // The usual commands to identify visitors and report cart contents.
        Session.getInstance().setCustomerEmail("visitor@test-mail.com");
        Transaction t = new Transaction();
        t.cart(Arrays.asList(
                new CartItem("item_1", 19.9f, 1),
                new CartItem("item_2", 29.7f, 3))
        );
        // Passing on item Id to report product view. Item Id should match the
        // value listed in the Product Catalog
        t.view("item_3");

        // Firing the EmarsysPredictSDKQueue. Should be the last call on the page,
        // called only once.
        Session.getInstance().sendTransaction(t, new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                Log.d(TAG, error.toString());
            }
        });
    }

    /**
     * Port of JavaScript API sample
     * http://jsfiddle.net/dwoympfa/
     */
    @Test
    public void sample3() {
        // The usual commands to identify visitors and report cart contents.
        Session.getInstance().setCustomerEmail("visitor@test-mail.com");
        Transaction t = new Transaction();
        t.cart(Arrays.asList(
                new CartItem("item_1", 19.9f, 1),
                new CartItem("item_2", 29.7f, 3))
        );
        // Passing on the category path being visited.
        // Must match the 'category' values listed in the Product Catalog
        t.category("Bikes > Road Bikes");

        // Firing the EmarsysPredictSDKQueue. Should be the last call on the page,
        // called only once.
        Session.getInstance().sendTransaction(t, new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                Log.d(TAG, error.toString());
            }
        });
    }

    /**
     * Port of JavaScript API sample
     * http://jsfiddle.net/m3720jea/
     */
    @Test
    public void sample4() {
        // The usual commands to identify visitors and report cart contents.
        Session.getInstance().setCustomerEmail("visitor@test-mail.com");
        Transaction t = new Transaction();
        t.cart(new ArrayList<CartItem>());

        // Passing on order details. The price values passed on here serve as the
        // basis of our revenue and revenue contribution reports.
        List<CartItem> items = new ArrayList<CartItem>();
        items.add(new CartItem("item_1", 19.9f, 1));
        items.add(new CartItem("item_2", 29.7f, 3));
        t.purchase("231213", items);

        // Firing the EmarsysPredictSDKQueue. Should be the last call on the page,
        // called only once.
        Session.getInstance().sendTransaction(t, new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                Log.d(TAG, error.toString());
            }
        });
    }

    /**
     * Port of JavaScript API sample
     * http://jsfiddle.net/scarabresearch/3Z3UQ/?utm_source=website&utm_medium=embed&utm_campaign=3Z3UQ
     */
    @Test
    public void sample5() {
        Transaction t = new Transaction();
        RecommendationRequest recommend = new RecommendationRequest("PERSONAL");
        t.recommend(recommend, new CompletionHandler() {
            @Override
            public void onCompletion(@NonNull RecommendationResult result) {
                // Process result
                Log.d(TAG, result.getFeatureId());
                Assert.assertEquals(result.getFeatureId(), "PERSONAL");
                for (RecommendedItem next : result.getProducts()) {
                    Log.d(TAG, next.toString());
                }
            }
        });
        // Firing the EmarsysPredictSDKQueue. Should be the last call on the page,
        // called only once.
        Session.getInstance().sendTransaction(t, new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                Log.d(TAG, error.toString());
            }
        });
    }

    /**
     * Port of JavaScript API sample
     * http://jsfiddle.net/scarabresearch/82tk48fe/?utm_source=website&utm_medium=embed&utm_campaign=82tk48fe
     */
    @Test
    public void sample6() {
        Transaction t = new Transaction();
        t.cart(new ArrayList<CartItem>());
        for (int i = 1; i < 11; i++) {
            String logic = "HOME_" + i;
            RecommendationRequest recommend = new RecommendationRequest(logic);
            recommend.setLimit(8);
            t.recommend(recommend, new CompletionHandler() {
                @Override
                public void onCompletion(@NonNull RecommendationResult result) {
                    // Process result
                    Log.d(TAG, result.getFeatureId());
                    Assert.assertEquals(result.getFeatureId().substring(0, 4), "HOME");
                    for (RecommendedItem next : result.getProducts()) {
                        Log.d(TAG, next.toString());
                    }
                }
            });
        }
        // Firing the EmarsysPredictSDKQueue. Should be the last call on the page,
        // called only once.
        Session.getInstance().sendTransaction(t, new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                Log.d(TAG, error.toString());
            }
        });
    }

    /**
     * Port of JavaScript API sample
     * http://jsfiddle.net/scarabresearch/7PZCv/?utm_source=website&utm_medium=embed&utm_campaign=7PZCv
     */
    @Test
    public void sample7() {
        Transaction t = new Transaction();
        // no filter, just get recs
        t.view("172");
        RecommendationRequest r = new RecommendationRequest("RELATED");
        r.setLimit(5);
        t.recommend(r, new CompletionHandler() {
            @Override
            public void onCompletion(@NonNull RecommendationResult result) {
                // Process result
                Log.d(TAG, result.getFeatureId());
                Assert.assertEquals(result.getFeatureId(), "RELATED");
                for (RecommendedItem next : result.getProducts()) {
                    Log.d(TAG, next.toString());
                }
            }
        });
        Session.getInstance().sendTransaction(t, new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                Log.d(TAG, error.toString());
            }
        });
        // do not recommend items '204' and '185'
        Transaction t2 = new Transaction();
        RecommendationRequest r2 = new RecommendationRequest("RELATED_2");
        r2.excludeItemsWhereIn("item", Arrays.asList("204", "185"));
        r2.setLimit(3);
        t2.recommend(r2, new CompletionHandler() {
            @Override
            public void onCompletion(@NonNull RecommendationResult result) {
                // Process result
                Log.d(TAG, result.getFeatureId());
                Assert.assertEquals(result.getFeatureId(), "RELATED_2");
                for (RecommendedItem next : result.getProducts()) {
                    Log.d(TAG, next.toString());
                }
            }
        });
        t2.view("172");
        Session.getInstance().sendTransaction(t2, new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                Log.d(TAG, error.toString());
            }
        });
        // only recommend a certain category
        Transaction t3 = new Transaction();
        RecommendationRequest r3 = new RecommendationRequest("RELATED_3");
        r3.includeItemsWhereHas("category", "Root Catalog>Handbags");
        r3.setLimit(3);
        t3.recommend(r3, new CompletionHandler() {
            @Override
            public void onCompletion(@NonNull RecommendationResult result) {
                // Process result
                Log.d(TAG, result.getFeatureId());
                Assert.assertEquals(result.getFeatureId(), "RELATED_3");
                for (RecommendedItem next : result.getProducts()) {
                    Log.d(TAG, next.toString());
                }
            }
        });
        t3.view("172");
        Session.getInstance().sendTransaction(t3, new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                Log.d(TAG, error.toString());
            }
        });
    }

}
