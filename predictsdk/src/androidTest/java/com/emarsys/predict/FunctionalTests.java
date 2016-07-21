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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22MULTIPLE_CALL%22,%22c%22:%22keyword%22,%22m%22:%22Multiple%20calls%20of%20keyword%20command%22%7D,%7B%22t%22:%22MULTIPLE_CALL%22,%22c%22:%22cart%22,%22m%22:%22Multiple%20calls%20of%20cart%20command%22%7D,%7B%22t%22:%22MULTIPLE_CALL%22,%22c%22:%22category%22,%22m%22:%22Multiple%20calls%20of%20category%20command%22%7D,%7B%22t%22:%22MULTIPLE_CALL%22,%22c%22:%22purchase%22,%22m%22:%22Multiple%20calls%20of%20purchase%20command%22%7D,%7B%22t%22:%22MULTIPLE_CALL%22,%22c%22:%22searchTerm%22,%22m%22:%22Multiple%20calls%20of%20searchTerm%20command%22%7D,%7B%22t%22:%22MULTIPLE_CALL%22,%22c%22:%22tag%22,%22m%22:%22Multiple%20calls%20of%20tag%20command%22%7D,%7B%22t%22:%22MULTIPLE_CALL%22,%22c%22:%22view%22,%22m%22:%22Multiple%20calls%20of%20view%20command%22%7D%5D");
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
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22MULTIPLE_CALL%22,%22c%22:%22cart%22,%22m%22:%22Multiple%20calls%20of%20cart%20command%22%7D%5D");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.category("book > literature > sci-fi");
        t.category("book > literature > horror");
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22MULTIPLE_CALL%22,%22c%22:%22category%22,%22m%22:%22Multiple%20calls%20of%20category%20command%22%7D%5D");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.keyword("sci-fi");
        t.keyword("horror");
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22MULTIPLE_CALL%22,%22c%22:%22keyword%22,%22m%22:%22Multiple%20calls%20of%20keyword%20command%22%7D%5D");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.purchase("100", items);
        t.purchase("101", items2);
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22MULTIPLE_CALL%22,%22c%22:%22purchase%22,%22m%22:%22Multiple%20calls%20of%20purchase%20command%22%7D%5D");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.searchTerm("great sci-fi classics");
        t.searchTerm("great horror classics");
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22MULTIPLE_CALL%22,%22c%22:%22searchTerm%22,%22m%22:%22Multiple%20calls%20of%20searchTerm%20command%22%7D%5D");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.tag("sci-fi");
        t.tag("horror");
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22MULTIPLE_CALL%22,%22c%22:%22tag%22,%22m%22:%22Multiple%20calls%20of%20tag%20command%22%7D%5D");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.view("112");
        t.view("172");
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22MULTIPLE_CALL%22,%22c%22:%22view%22,%22m%22:%22Multiple%20calls%20of%20view%20command%22%7D%5D");
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
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22INVALID_ARG%22,%22c%22:%22cart%22,%22m%22:%22Invalid%20argument%20in%20cart%20command:%20itemId%20should%20not%20be%20an%20empty%20string%22%7D%5D");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.category("");
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22INVALID_ARG%22,%22c%22:%22category%22,%22m%22:%22Invalid%20argument%20in%20category%20command:%20category%20should%20not%20be%20an%20empty%20string%22%7D%5D");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.keyword("");
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22INVALID_ARG%22,%22c%22:%22keyword%22,%22m%22:%22Invalid%20argument%20in%20keyword%20command:%20keyword%20should%20not%20be%20an%20empty%20string%22%7D%5D");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.purchase("", items);
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22INVALID_ARG%22,%22c%22:%22purchase%22,%22m%22:%22Invalid%20argument%20in%20purchase%20command:%20orderId%20should%20not%20be%20an%20empty%20string%22%7D%5D");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.searchTerm("");
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22INVALID_ARG%22,%22c%22:%22searchTerm%22,%22m%22:%22Invalid%20argument%20in%20searchTerm%20command:%20searchTerm%20should%20not%20be%20an%20empty%20string%22%7D%5D");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.tag("");
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22INVALID_ARG%22,%22c%22:%22tag%22,%22m%22:%22Invalid%20argument%20in%20tag%20command:%20tag%20should%20not%20be%20an%20empty%20string%22%7D%5D");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.view("");
        assertEquals(getParameterValue("error", Session.getInstance().generateGET(t)),
                "%5B%7B%22t%22:%22INVALID_ARG%22,%22c%22:%22view%22,%22m%22:%22Invalid%20argument%20in%20view%20command:%20itemId%20should%20not%20be%20an%20empty%20string%22%7D%5D");
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

}
