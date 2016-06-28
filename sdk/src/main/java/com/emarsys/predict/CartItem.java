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

/**
 * The cart item.
 */
public class CartItem {

    /**
     * Creates an item.
     *
     * @param itemId   ID of cart item (consistent with the item column specified in the catalog)
     * @param price    sum total payable for the item, taking into consideration the quantity
     *                 ordered, and any discounts
     * @param quantity quantity in cart
     */
    public CartItem(@NonNull String itemId, float price, int quantity) {
        if (itemId == null) {
            throw new NullPointerException("The itemId cannot be null");
        }
        this.itemId = itemId;
        this.price = price;
        this.quantity = quantity;
    }

    private final String itemId;

    /**
     * Returns the ID of cart item (consistent with the item column specified in the catalog).
     *
     * @return item ID
     */
    @NonNull
    public String getItemId() {
        return itemId;
    }

    private final float price;

    /**
     * Returns the sum total payable for the item, taking into consideration the quantity ordered,
     * and any discounts.
     *
     * @return price
     */
    public float getPrice() {
        return price;
    }

    private final int quantity;

    /**
     * Returns the quantity in cart.
     *
     * @return quantity
     */
    public int getQuantity() {
        return quantity;
    }

}
