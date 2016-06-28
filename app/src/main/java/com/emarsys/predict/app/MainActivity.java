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

package com.emarsys.predict.app;

import com.emarsys.predict.CartItem;
import com.emarsys.predict.CompletionHandler;
import com.emarsys.predict.Error;
import com.emarsys.predict.ErrorHandler;
import com.emarsys.predict.RecommendationRequest;
import com.emarsys.predict.RecommendationResult;
import com.emarsys.predict.RecommendedItem;
import com.emarsys.predict.Session;
import com.emarsys.predict.Storage;
import com.emarsys.predict.Transaction;

import junit.framework.Assert;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init the Session
        Session.initialize(new Storage() {

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

            @Override
            public void put(String key, Object value) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("advertisingId", value.toString());
                editor.commit();
            }

            @Override
            public Object get(String key) {
                return sharedPref.getString("advertisingId", null);
            }

        });

        // These parts of our API should be implemented on ALL website pages
        Session session = Session.getInstance();
        // Identifies the merchant account (here the emarsys demo merchant 1A65B5CB868AFF1E).
        // Replace it with your own Merchant Id before running the application.
        session.setMerchantId("1A74F439823D2CB4");

        sample1();
        sample2();
        sample3();
        sample4();
        sample5();
        sample6();
        sample7();
    }

    /**
     * Port of JavaScript API sample
     * http://jsfiddle.net/0xrprjrz/1/
     */
    void sample1() {
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
    void sample2() {
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
    void sample3() {
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
    void sample4() {
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
    void sample5() {
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
    void sample6() {
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
    void sample7() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
