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

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;

/**
 * The global session object.
 */
public class Session {

    private static final String TAG = Session.class.getSimpleName();

    private static class Holder {
        private static final Session INSTANCE = new Session();
    }

    private final CookieManager cookieManager;

    private final Handler handler;

    private Session() {
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        handler = new Handler(Looper.getMainLooper());
    }

    private static Storage storage;

    Storage getStorage() {
        return storage;
    }

    /**
     * Initialization method.
     *
     * @param storage the Storage for accessing and modifying save and retrieve persistent
     *                key-value pairs
     * @throws IllegalStateException when Session is already initialized
     */
    public static void initialize(Storage storage) {
        if (Session.storage != null) {
            throw new IllegalStateException("The initialize method may only be called once");
        }
        if (storage == null) {
            throw new NullPointerException("The storage cannot be null");
        }
        Session.storage = storage;
    }

    /**
     * Returns whether the session is initialized or not.
     *
     * @return true if initialized, otherwise false
     */
    public static boolean isInitialized() {
        return Session.storage != null;
    }

    /**
     * Returns the singleton session object.
     *
     * @return session instance
     */
    public static Session getInstance() {
        if (storage == null) {
            throw new RuntimeException("Please call initialize method first");
        }
        return Holder.INSTANCE;
    }

    /**
     * Send transaction to the recommender server.
     *
     * @param transaction an Transaction instance to be send
     */
    public void sendTransaction(@NonNull final Transaction transaction) {
        sendTransaction(transaction, null, null);
    }

    /**
     * Send transaction to the recommender server.
     *
     * @param transaction  an Transaction instance to be send
     * @param errorHandler will be called if an error occurs before send the http request
     */
    public void sendTransaction(@NonNull final Transaction transaction,
                                @Nullable final ErrorHandler errorHandler) {
        sendTransaction(transaction, errorHandler, null);
    }

    /**
     * Send transaction to the recommender server.
     *
     * @param transaction       an Transaction instance to be send
     * @param completionHandler will be called after the background processing finishes
     */
    public void sendTransaction(@NonNull final Transaction transaction,
                                @Nullable final CompletionHandler completionHandler) {
        sendTransaction(transaction, null, completionHandler);
    }

    /**
     * Send transaction to the recommender server.
     *
     * @param transaction       an Transaction instance to be send
     * @param errorHandler      will be called if an error occurs before send the http request
     * @param completionHandler will be called after the background processing finishes
     */
    public void sendTransaction(@NonNull final Transaction transaction,
                                @Nullable final ErrorHandler errorHandler,
                                @Nullable final CompletionHandler completionHandler) {
        if (transaction == null) {
            throw new NullPointerException("The transaction cannot be null");
        }

        final String url = generateGET(transaction);

        Log.d(TAG, url);

        // for API levels lower than 16, AsyncTasks can only be created on the UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                TransactionTask task = new TransactionTask(transaction, errorHandler,
                        completionHandler);
                task.execute(url);
            }
        });
    }

    private String merchantId;

    /**
     * Returns the Merchant ID.
     *
     * @return merchant ID
     */
    @Nullable
    public String getMerchantId() {
        return merchantId;
    }

    /**
     * Sets the Merchant ID.
     *
     * @param merchantId merchant ID
     */
    public void setMerchantId(@NonNull String merchantId) {
        if (merchantId == null) {
            throw new NullPointerException("The merchantId cannot be null");
        }
        this.merchantId = merchantId;
    }

    private String customerEmail;

    /**
     * Returns the Customer email address.
     *
     * @return customer email address
     */
    @Nullable
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * Sets the Customer email address.
     *
     * @param customerEmail customer email address
     */
    public void setCustomerEmail(@Nullable String customerEmail) {
        this.customerEmail = customerEmail;
    }

    private String customerId;

    /**
     * Returns the Customer ID.
     *
     * @return customer ID
     */
    @Nullable
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the Customer ID.
     *
     * @param customerId customer ID
     */
    public void setCustomerId(@Nullable String customerId) {
        this.customerId = customerId;
    }

    /**
     * Returns the Advertising ID.
     *
     * @return advertising ID
     */
    @Nullable
    public String getAdvertisingId() {
        return IdentifierManager.getInstance().getAdvertisingIdentifier();
    }

    String session;

    String getSession() {
        return session;
    }

    String visitor;

    String getVisitor() {
        return visitor;
    }

    void handleCookies(List<HttpCookie> cookies) {
        Log.d(TAG, "Find cookie, cdv");
        for (HttpCookie cookie : cookies) {
            if ("cdv".equals(cookie.getName()) && cookie.getValue() != null) {
                Log.d(TAG, "Found cookie, " + cookie.getName() + "=" + cookie.getValue());
                IdentifierManager.getInstance().setAdvertisingIdentifier(cookie.getValue());
                return;
            }
        }
        throw new Error("Missing 'cdv' cookie", Error.ERROR_MISSING_CDV_COOKIE, null);
    }

    static boolean secure = true;
    static final String SERVER = "recommender.scarabresearch.com";

    /**
     * Set protocol to https if secure otherwise set to http.
     *
     * @param secure <tt>true</tt> if the protocol is https
     */
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    /**
     * Returns true if the communication is secured.
     *
     * @return true if the protocol is https or false if http
     */
    public boolean isSecure() {
        return secure;
    }

    String generateGET(Transaction transaction) {
        // Validate merchantId
        if (merchantId == null || merchantId.isEmpty()) {
            // The merchantId is required
            throw new Error("The merchantId is required", Error.ERROR_MISSING_MERCHANT_ID, null);
        }

        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme(secure ? "https" : "http")
                .host(SERVER)
                .addPathSegment("merchants")
                .addPathSegment(merchantId);
        // Serialize query
        transaction.serialize(builder);
        Log.d(TAG, builder.build().query());
        return builder.build().url().toString();
    }

    private class TransactionTask extends AsyncTask<String, Void, Object> {

        private final Transaction transaction;
        private final ErrorHandler errorHandler;
        private final CompletionHandler completionHandler;

        public TransactionTask(Transaction transaction, ErrorHandler errorHandler,
                               CompletionHandler completionHandler) {
            this.transaction = transaction;
            this.errorHandler = errorHandler;
            this.completionHandler = completionHandler;
        }

        @Override
        protected Object doInBackground(String... args) {
            // Invoked on the background thread
            Response response = null;
            try {
                URL url = new URL(args[0]);
                OkHttpClient client = new OkHttpClient();
                client.setCookieHandler(cookieManager);
                Request request = new Request.Builder()
                        .url(url)
                        .header("User-Agent", "EmarsysPredictSDK|osversion:"
                                + Build.VERSION.RELEASE + "|platform:android")
                        .build();
                response = client.newCall(request).execute();
                int statusCode = response.code();
                if (statusCode >= 300) {
                    return new Error("Unexpected http status code " + statusCode,
                            Error.ERROR_BAD_HTTP_STATUS, null);
                }
                // Find cdv
                handleCookies(cookieManager.getCookieStore().getCookies());
                // Get json content
                Gson gson = new Gson();
                Reader r = new InputStreamReader(response.body().byteStream());
                @SuppressWarnings("unchecked")
                Map<String, Object> json = (Map<String, Object>) gson.fromJson(r, Map.class);
                ResponseParser parser = new ResponseParser(json);
                return parser;
            } catch (Exception e) {
                if (e instanceof Error) {
                    return e;
                }
                return new Error("An unknown error has occurred", Error.ERROR_UNKNOWN, e);
            } finally {
                if (response != null) {
                    try {
                        response.body().close();
                    } catch (IOException e) {
                        Log.w(TAG, "Unable to close response body");
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            // Invoked on the UI thread after the background computation finishes
            if (o instanceof Error) {
                // Forward error
                if (errorHandler != null) {
                    errorHandler.onError((Error) o);
                }
            } else {
                // Store session and visitor
                ResponseParser parser = (ResponseParser) o;
                session = parser.getSession();
                visitor = parser.getVisitor();
                // Forward results
                transaction.handleResults(parser.getResults());
                // Completed
                if (completionHandler != null) {
                    completionHandler.onCompletion(null);
                }
            }
        }
    }
}
