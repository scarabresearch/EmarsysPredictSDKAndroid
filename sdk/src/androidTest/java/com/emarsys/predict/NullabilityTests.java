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

import android.test.AndroidTestCase;

public class NullabilityTests extends AndroidTestCase {

    private static final String TAG = NullabilityTests.class.getSimpleName();

    public void setUp() throws Exception {
        super.setUp();

        // Init the Session
        SessionHelper.reset();
    }

    public void testCartItem() {
        try {
            new CartItem(null, 80f, 2);
            fail();
        } catch (NullPointerException e) {

        }
    }

    public void testRecommendationRequest() {
        try {
            new RecommendationRequest(null);
            fail();
        } catch (NullPointerException e) {

        }
    }

    public void testSendTransaction() {
        try {
            Session.getInstance().sendTransaction(null);
            fail();
        } catch (NullPointerException e) {

        }
    }

    public void testMerchantId() {
        try {
            Session.getInstance().setMerchantId(null);
            fail();
        } catch (NullPointerException e) {

        }
    }

    public void testAvailabilityZone() {
        try {
            new Transaction().availabilityZone(null);
            fail();
        } catch (NullPointerException e) {

        }
    }

    public void testCategory() {
        try {
            new Transaction().category(null);
            fail();
        } catch (NullPointerException e) {

        }
    }

    public void testKeyword() {
        try {
            new Transaction().keyword(null);
            fail();
        } catch (NullPointerException e) {

        }
    }

    public void testPurchase() {
        try {
            new Transaction().purchase(null, null);
            fail();
        } catch (NullPointerException e) {

        }
    }

    public void testRecommend() {
        try {
            new Transaction().recommend(null, null);
            fail();
        } catch (NullPointerException e) {

        }
    }

    public void testSearchTerm() {
        try {
            new Transaction().searchTerm(null);
            fail();
        } catch (NullPointerException e) {

        }
    }

    public void testTransaction() {
        try {
            new Transaction(null);
            fail();
        } catch (NullPointerException e) {

        }
    }

    public void testView() {
        try {
            new Transaction().view(null);
            fail();
        } catch (NullPointerException e) {

        }
    }

}
