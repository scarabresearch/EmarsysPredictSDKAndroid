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

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class NullabilityTests {

    @BeforeClass
    public static void setUp() {
        // Init the Session
        SessionHelper.reset();
        // These parts of our API should be implemented on ALL website pages
        Session session = Session.getInstance();
        // Identifies the merchant account (here the emarsys demo merchant 1A65B5CB868AFF1E).
        // Replace it with your own Merchant Id before run.
        session.setMerchantId("1A74F439823D2CB4");
        assertEquals(session.getMerchantId(), "1A74F439823D2CB4");
        Session.getInstance().setSecure(true);
    }

    @Test(expected = NullPointerException.class)
    public void testCartItem() {
        new CartItem(null, 80f, 2);
    }

    @Test(expected = NullPointerException.class)
    public void testRecommendationRequest() {
        new RecommendationRequest(null);
    }

    @Test(expected = NullPointerException.class)
    public void testSendTransaction() {
        Session.getInstance().sendTransaction(null);
    }

    @Test(expected = NullPointerException.class)
    public void testMerchantId() {
        Session.getInstance().setMerchantId(null);
    }

    @Test(expected = NullPointerException.class)
    public void testAvailabilityZone() {
        new Transaction().availabilityZone(null);
    }

    @Test(expected = NullPointerException.class)
    public void testCategory() {
        new Transaction().category(null);
    }

    @Test(expected = NullPointerException.class)
    public void testKeyword() {
        new Transaction().keyword(null);
    }

    @Test(expected = NullPointerException.class)
    public void testTag() {
        new Transaction().tag(null);
    }

    @Test(expected = NullPointerException.class)
    public void testPurchaseOrderId() {
        new Transaction().purchase(null, new ArrayList<CartItem>());
    }

    @Test(expected = NullPointerException.class)
    public void testPurchaseItems() {
        new Transaction().purchase("1", null);
    }

    @Test(expected = NullPointerException.class)
    public void testRecommend() {
        new Transaction().recommend(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testSearchTerm() {
        new Transaction().searchTerm(null);
    }

    @Test(expected = NullPointerException.class)
    public void testTransaction() {
        new Transaction(null);
    }

    @Test(expected = NullPointerException.class)
    public void testView() {
        new Transaction().view(null);
    }

}
