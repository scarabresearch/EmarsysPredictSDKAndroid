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
import com.google.gson.reflect.TypeToken;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class FunctionalTests {

    private static final String TAG = FunctionalTests.class.getSimpleName();

    static final long TIMEOUT_SMALL = 2;
    static final long TIMEOUT_LARGE = 8;

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

    class ErrorHolder {
        Exception error;
    }

    @Test
    public void testERROR_MULTIPLE_CALL() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        final ErrorHolder e = new ErrorHolder();
        Transaction t = new Transaction();
        List<CartItem> items = Arrays.asList(new CartItem("112", 80f, 2));
        List<CartItem> items2 = Arrays.asList(new CartItem("172", 159f, 1));
        t.cart(items);
        t.cart(items2);
        t.category("book > literature > sci-fi");
        t.category("book > literature > horror");
        t.keyword("sci-fi");
        t.keyword("horror");
        t.purchase("100", items);
        t.purchase("101", items2);
        t.searchTerm("great sci-fi classics");
        t.searchTerm("great horror classics");
        t.tag("sci-fi");
        t.tag("horror");
        t.view("112");
        t.view("172");
        final List<Map<String, String>> qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "MULTIPLE_CALL");
            qmap.put("c", "keyword");
            qmap.put("m", "Multiple calls of keyword command");
            qlist.add(qmap);
        }
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "MULTIPLE_CALL");
            qmap.put("c", "cart");
            qmap.put("m", "Multiple calls of cart command");
            qlist.add(qmap);
        }
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "MULTIPLE_CALL");
            qmap.put("c", "category");
            qmap.put("m", "Multiple calls of category command");
            qlist.add(qmap);
        }
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "MULTIPLE_CALL");
            qmap.put("c", "purchase");
            qmap.put("m", "Multiple calls of purchase command");
            qlist.add(qmap);
        }
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "MULTIPLE_CALL");
            qmap.put("c", "searchTerm");
            qmap.put("m", "Multiple calls of searchTerm command");
            qlist.add(qmap);
        }
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "MULTIPLE_CALL");
            qmap.put("c", "tag");
            qmap.put("m", "Multiple calls of tag command");
            qlist.add(qmap);
        }
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "MULTIPLE_CALL");
            qmap.put("c", "view");
            qmap.put("m", "Multiple calls of view command");
            qlist.add(qmap);
        }

        testError(Session.getInstance().generateGET(t), qlist);

        Session.getInstance().sendTransaction(t, new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                e.error = error;
                signal.countDown();
            }
        }, new CompletionHandler() {
            @Override
            public void onCompletion(@Nullable RecommendationResult recommendationResult) {
                signal.countDown();
            }
        });
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertNull(e.error);
    }

    @Test
    public void testERROR_MULTIPLE_CALL_separated() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);

        ErrorHandler errorHandler = new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                Log.d(TAG, error.toString());
                signal.countDown();
            }
        };

        Transaction t = new Transaction();
        List<CartItem> items = Arrays.asList(new CartItem("112", 80f, 2));
        List<CartItem> items2 = Arrays.asList(new CartItem("172", 159f, 1));
        t.cart(items);
        t.cart(items2);
        List<Map<String, String>> qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "MULTIPLE_CALL");
            qmap.put("c", "cart");
            qmap.put("m", "Multiple calls of cart command");
            qlist.add(qmap);
        }
        testError(Session.getInstance().generateGET(t), qlist);

        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.category("book > literature > sci-fi");
        t.category("book > literature > horror");
        qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "MULTIPLE_CALL");
            qmap.put("c", "category");
            qmap.put("m", "Multiple calls of category command");
            qlist.add(qmap);
        }
        testError(Session.getInstance().generateGET(t), qlist);
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.keyword("sci-fi");
        t.keyword("horror");
        qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "MULTIPLE_CALL");
            qmap.put("c", "keyword");
            qmap.put("m", "Multiple calls of keyword command");
            qlist.add(qmap);
        }
        testError(Session.getInstance().generateGET(t), qlist);
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.purchase("100", items);
        t.purchase("101", items2);
        qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "MULTIPLE_CALL");
            qmap.put("c", "purchase");
            qmap.put("m", "Multiple calls of purchase command");
            qlist.add(qmap);
        }
        testError(Session.getInstance().generateGET(t), qlist);
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.searchTerm("great sci-fi classics");
        t.searchTerm("great horror classics");
        qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "MULTIPLE_CALL");
            qmap.put("c", "searchTerm");
            qmap.put("m", "Multiple calls of searchTerm command");
            qlist.add(qmap);
        }
        testError(Session.getInstance().generateGET(t), qlist);
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.tag("sci-fi");
        t.tag("horror");
        qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "MULTIPLE_CALL");
            qmap.put("c", "tag");
            qmap.put("m", "Multiple calls of tag command");
            qlist.add(qmap);
        }
        testError(Session.getInstance().generateGET(t), qlist);
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.view("112");
        t.view("172");
        qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "MULTIPLE_CALL");
            qmap.put("c", "view");
            qmap.put("m", "Multiple calls of view command");
            qlist.add(qmap);
        }
        testError(Session.getInstance().generateGET(t), qlist);
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());
    }

    @Test
    public void testERROR_INVALID_ARG_empty_string() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);

        ErrorHandler errorHandler = new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                Log.d(TAG, error.toString());
                signal.countDown();
            }
        };

        Transaction t = new Transaction();
        List<CartItem> items = Arrays.asList(new CartItem("", 80f, 2));
        t.cart(items);
        List<Map<String, String>> qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "INVALID_ARG");
            qmap.put("c", "cart");
            qmap.put("m", "Invalid argument in cart command: itemId should not be an empty string");
            qlist.add(qmap);
        }
        testError(Session.getInstance().generateGET(t), qlist);
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.category("");
        qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "INVALID_ARG");
            qmap.put("c", "category");
            qmap.put("m", "Invalid argument in category command: category should not be an empty string");
            qlist.add(qmap);
        }
        testError(Session.getInstance().generateGET(t), qlist);
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.keyword("");
        qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "INVALID_ARG");
            qmap.put("c", "keyword");
            qmap.put("m", "Invalid argument in keyword command: keyword should not be an empty string");
            qlist.add(qmap);
        }
        testError(Session.getInstance().generateGET(t), qlist);
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.purchase("", items);
        qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "INVALID_ARG");
            qmap.put("c", "purchase");
            qmap.put("m", "Invalid argument in purchase command: orderId should not be an empty string");
            qlist.add(qmap);
        }
        testError(Session.getInstance().generateGET(t), qlist);
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.searchTerm("");
        qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "INVALID_ARG");
            qmap.put("c", "searchTerm");
            qmap.put("m", "Invalid argument in searchTerm command: searchTerm should not be an empty string");
            qlist.add(qmap);
        }
        testError(Session.getInstance().generateGET(t), qlist);
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.tag("");
        qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "INVALID_ARG");
            qmap.put("c", "tag");
            qmap.put("m", "Invalid argument in tag command: tag should not be an empty string");
            qlist.add(qmap);
        }
        testError(Session.getInstance().generateGET(t), qlist);
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.view("");
        qlist = new ArrayList<Map<String, String>>();
        {
            final Map<String, String> qmap = new HashMap<String, String>();
            qmap.put("t", "INVALID_ARG");
            qmap.put("c", "view");
            qmap.put("m", "Invalid argument in view command: itemId should not be an empty string");
            qlist.add(qmap);
        }
        testError(Session.getInstance().generateGET(t), qlist);
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());
    }

    @Test
    public void testInvalidMerchantId() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        Exception e = null;
        Session.getInstance().setMerchantId("");
        try {
            Session.getInstance().sendTransaction(new Transaction(), new ErrorHandler() {
                @Override
                public void onError(@NonNull Error error) {
                    signal.countDown();
                }
            });
            fail();
        } catch (Exception ee) {
            e = ee;
            assertEquals(e.getMessage(), "The merchantId is required");
        }
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());
        assertNotNull(e);

        e = null;
        try {
            Transaction t = new Transaction();
            RecommendationRequest r = new RecommendationRequest("PERSONAL");
            t.recommend(r, new CompletionHandler() {
                @Override
                public void onCompletion(@NonNull RecommendationResult result) {
                    signal.countDown();
                }
            });
            Session.getInstance().sendTransaction(new Transaction(), new ErrorHandler() {
                @Override
                public void onError(@NonNull Error error) {
                    signal.countDown();
                }
            });
            fail();
        } catch (Exception ee) {
            e = ee;
            assertEquals(e.getMessage(), "The merchantId is required");
        }
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());
        assertNotNull(e);
    }

    @Test
    public void testNonUniqueLogic() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        Exception e = null;
        Transaction t = new Transaction();
        t.cart(new ArrayList<CartItem>());
        for (int i = 1; i < 11; i++) {
            RecommendationRequest r = new RecommendationRequest("HOME_1");
            t.recommend(r, new CompletionHandler() {
                @Override
                public void onCompletion(@NonNull RecommendationResult result) {
                    signal.countDown();
                }
            });
        }
        try {
            Session.getInstance().sendTransaction(t, new ErrorHandler() {
                @Override
                public void onError(@NonNull Error error) {
                    signal.countDown();
                }
            });
            fail();
        } catch (Exception ee) {
            e = ee;
            assertEquals(e.getMessage(), "The recommend logic must be unique inner transaction");
        }
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());
        assertNotNull(e);
    }

    @Test
    public void testTRACKING() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        final ErrorHolder e = new ErrorHolder();
        Transaction t = new Transaction();
        // no filter, just get recs
        t.view("172");
        RecommendationRequest r = new RecommendationRequest("RELATED");
        r.setLimit(5);
        t.recommend(r, new CompletionHandler() {
            @Override
            public void onCompletion(@NonNull RecommendationResult result) {
                Log.d(TAG, result.getFeatureId());
                assertEquals(result.getFeatureId(), "RELATED");
                if (result.getProducts().size() > 0) {
                    // Click the first item, open details page
                    RecommendedItem tracked = result.getProducts().get(0);
                    // Add view
                    Transaction t2 = new Transaction(tracked);
                    t2.view((String) tracked.getData().get("item"));
                    // Add recommend
                    RecommendationRequest r2 = new RecommendationRequest("ALSO_BOUGHT");
                    t2.recommend(r2, new CompletionHandler() {
                        @Override
                        public void onCompletion(@NonNull RecommendationResult result) {
                            // Process result
                            Log.d(TAG, result.getFeatureId());
                            assertEquals(result.getFeatureId(), "ALSO_BOUGHT");
                            for (RecommendedItem next : result.getProducts()) {
                                Log.d(TAG, next.toString());
                            }
                            signal.countDown();
                        }
                    });
                    try {
                        Session.getInstance().sendTransaction(t2, new ErrorHandler() {
                            @Override
                            public void onError(@NonNull Error error) {
                                Log.e(TAG, error.toString());
                                e.error = error;
                                signal.countDown();
                            }
                        });
                    } catch (Exception ee) {
                        e.error = ee;
                        signal.countDown();
                    }
                } else {
                    signal.countDown();
                }
            }
        });
        Session.getInstance().sendTransaction(t, new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                Log.e(TAG, error.toString());
                e.error = error;
                signal.countDown();
            }
        });
        signal.await(TIMEOUT_LARGE, TimeUnit.SECONDS);
        assertNull(e.error);
    }

    @Test
    public void testCATEGORY() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        final ErrorHolder e = new ErrorHolder();
        Transaction t = new Transaction();
        t.cart(new ArrayList<CartItem>());
        t.category("Accessories");
        RecommendationRequest r = new RecommendationRequest("CATEGORY");
        t.recommend(r, new CompletionHandler() {
            @Override
            public void onCompletion(@NonNull RecommendationResult result) {
                // Process result
                Log.d(TAG, result.getFeatureId());
                assertEquals(result.getFeatureId(), "CATEGORY");
                for (RecommendedItem next : result.getProducts()) {
                    Log.d(TAG, next.toString());
                }
                signal.countDown();
            }
        });
        Session.getInstance().sendTransaction(new Transaction(), new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                e.error = error;
                signal.countDown();
            }
        });
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertNull(e.error);
    }

    @Test
    public void testAdvertisingIdentifier() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        final List<String> tmp = new ArrayList<String>();
        Session.getInstance().sendTransaction(new Transaction(), new ErrorHandler() {
            @Override
            public void onError(@NonNull Error error) {
                signal.countDown();
            }
        }, new CompletionHandler() {
            @Override
            public void onCompletion(@Nullable RecommendationResult recommendationResult) {
                tmp.add(Session.getInstance().getAdvertisingId());
                Session.getInstance().sendTransaction(new Transaction(), new ErrorHandler() {
                    @Override
                    public void onError(@NonNull Error error) {
                        signal.countDown();
                    }
                }, new CompletionHandler() {
                    @Override
                    public void onCompletion(@Nullable RecommendationResult recommendationResult) {
                        tmp.add(Session.getInstance().getAdvertisingId());
                        signal.countDown();
                    }
                });
            }
        });
        signal.await(TIMEOUT_LARGE, TimeUnit.SECONDS);
        assertEquals(2, tmp.size());
        assertNotNull(tmp.get(0));
        assertEquals(tmp.get(0), tmp.get(1));
    }

    @Test
    public void testCustomerId() {
        Session.getInstance().setCustomerId("sample-customer-id");
        String url = Session.getInstance().generateGET(new Transaction());
        assertEquals(getParameterValue("ci", url), "sample-customer-id");
    }

    @Test
    public void testEmailHash() throws MalformedURLException {
        Session.getInstance().setCustomerEmail(" CUSTOMER@TEST-mail.com ");
        String url = Session.getInstance().generateGET(new Transaction());
        assertEquals(getParameterValue("eh", url), "19d0b2cccd0b49e81");
    }

    String getParameterValue(String parameterName, String url) {
        final int a = url.indexOf(parameterName);
        url = url.substring(a);
        final int b = url.indexOf("&");
        String val = b == -1 ? url.substring(parameterName.length() + 1, url.length()) : url
                .substring(parameterName.length() + 1, b);
        return val;
    }

    @Test
    public void testSecure() throws MalformedURLException {
        Session.getInstance().setSecure(true);
        String url = Session.getInstance().generateGET(new Transaction());
        assertTrue(url.startsWith("https://"));
    }

    @Test
    public void testInSecure() throws MalformedURLException {
        Session.getInstance().setSecure(false);
        String url = Session.getInstance().generateGET(new Transaction());
        assertTrue(url.startsWith("http://"));
    }

    public static Map<String, String> getQueryMap(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {

        }

        if (url != null) {
            String[] params = url.getQuery().split("&");
            Map<String, String> map = new HashMap<String, String>();
            for (String param : params) {
                String keyAndValue[] = param.split("=");
                String name = keyAndValue[0];
                String value = "";
                if (keyAndValue.length > 1) {
                    value =  keyAndValue[1];
                }
                map.put(name, value);
            }
            return map;
        }

        return null;
    }

    private List<Map<String, String>> jsonStringToMap(String source) {
        source = source.replaceAll("%20", " ");
        source = source.replaceAll("%22", "\"");
        source = source.replaceAll("%5B", "[");
        source = source.replaceAll("%5D", "]");
        source = source.replaceAll("%7B", "{");
        source = source.replaceAll("%7D", "}");
        Type mapType = new TypeToken<List<Map<String, String>>>(){}.getType();
        return new Gson().fromJson(source, mapType);
    }

    private void testError(String urlString, List<Map<String, String>> qlist) {
        Map<String, String> resultMap = getQueryMap(urlString);
        String resultError = resultMap.get("error");
        List<Map<String, String>> parsedError = jsonStringToMap(resultError);

        assertEquals(qlist.size(), parsedError.size());

        for (int i=0; i<qlist.size(); i++) {
            Map<String, String> expected = qlist.get(i);
            Map<String, String> actual = parsedError.get(i);

            for (String key : expected.keySet()) {
                assertEquals(expected.get(key), actual.get(key));
            }
        }
    }

}
