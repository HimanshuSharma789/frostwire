/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011-2016, FrostWire(R). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.frostwire.android.offers;

import com.frostwire.android.core.Constants;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * CODE STYLE NOMENCLATURE: @aldenml names abstract classes which aren't meant to be extended
 * by anybody "*Base" classes, API users should use the concrete implementations.
 * <p>
 * If an abstract class is meant to be extended, then it's named *Abstract.
 *
 * @author gubatron
 * @author aldenml
 */
abstract class StoreBase implements Store {

    protected Map<String, Product> products;

    protected StoreBase() {
        this.products = Collections.emptyMap();
    }

    @Override
    public Map<String, Product> products() {
        return Collections.unmodifiableMap(products);
    }

    @Override
    public Product product(String sku) {
        return products.get(sku);
    }

    @Override
    public boolean enabled(String code) {
        // the value by default is true if it's BASIC, since the local cache
        // of the play store can be empty or out of sync
        // NOTE: no lint fix here for clarity
        boolean isBasic = Constants.IS_GOOGLE_PLAY_DISTRIBUTION;
        boolean isDevelopment = Constants.IS_BASIC_AND_DEBUG;
        boolean r = isBasic && !isDevelopment;
        Iterator<Product> it = products.values().iterator();

        while (!r && it.hasNext()) {
            Product p = it.next();
            // protection for faulty implementations of products
            if (p.subscription()) {
                r = p.purchased() && p.enabled(code);
            } else {
                r = !p.available() && p.purchased() && p.enabled(code);
            }
        }

        return r;
    }
}
