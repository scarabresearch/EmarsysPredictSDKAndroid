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
import android.test.AndroidTestCase;
import android.util.Log;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FunctionalTests extends AndroidTestCase {

    private static final String TAG = FunctionalTests.class.getSimpleName();

    static final long TIMEOUT_SMALL = 2;
    static final long TIMEOUT_LARGE = 8;

    public void setUp() throws Exception {
        super.setUp();

        // Init the Session
        SessionHelper.reset();

        // These parts of our API should be implemented on ALL website pages
        Session session = Session.getInstance();
        // Identifies the merchant account (here the emarsys demo merchant 1A65B5CB868AFF1E).
        // Replace it with your own Merchant Id before run.
        session.setMerchantId("1A74F439823D2CB4");
        assertEquals(session.getMerchantId(), "1A74F439823D2CB4");
    }

    class ErrorHolder {
        Exception error;
    }

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
        t.view("112");
        t.view("172");
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
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.category("book > literature > sci-fi");
        t.category("book > literature > horror");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.keyword("sci-fi");
        t.keyword("horror");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.purchase("100", items);
        t.purchase("101", items2);
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.searchTerm("great sci-fi classics");
        t.searchTerm("great horror classics");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());

        t = new Transaction();
        t.view("112");
        t.view("172");
        Session.getInstance().sendTransaction(t, errorHandler);
        signal.await(TIMEOUT_SMALL, TimeUnit.SECONDS);
        assertEquals(1, signal.getCount());
    }

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

    public void testEmailHash() throws MalformedURLException {
        Session.getInstance().setCustomerEmail("john@doe.com");
        String url = Session.getInstance().generateGET(new Transaction());
        final int ehStart = url.indexOf("eh=");
        url = url.substring(ehStart);
        final int ehEnd = url.indexOf("&");
        String hash = ehEnd == -1 ? url.substring(3, url.length()) : url.substring(3, ehEnd);
        assertEquals(hash, "fd9c796f4269b3481");
    }

}
