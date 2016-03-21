/*
 * Copyright 2015 Ray Holder
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

package com.github.rholder.nilsimsa;

import org.junit.Assert;
import org.junit.Test;

public class NilsimsaSortedTest {

    public static final String SIMILAR_SET1 = "The plains in Spain in the falls rain mainly";
    public static final String SIMILAR_SET2 = "The rain in Spain falls mainly in the plains";

    @Test
    public void test2EquivalentStringsWhenSorted() {
        System.out.println(NilsimsaStringComparisons.compareAsSet(SIMILAR_SET1, SIMILAR_SET2));
        Assert.assertEquals(128, NilsimsaStringComparisons.compareAsSet(SIMILAR_SET1, SIMILAR_SET2));
    }

    @Test
    public void test2JumbledStrings() {
        System.out.println(NilsimsaStringComparisons.compare(SIMILAR_SET1, SIMILAR_SET2));
        Assert.assertEquals(84, NilsimsaStringComparisons.compare(SIMILAR_SET1, SIMILAR_SET2));
    }

}
