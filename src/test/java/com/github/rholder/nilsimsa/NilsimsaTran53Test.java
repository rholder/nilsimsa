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

public class NilsimsaTran53Test {

    @Test
    public void verifyTransitionTable53() {
        int[] tran53 = Nilsimsa.generateTransitions(53);
        for (int i = 0; i < 256; i++) {
            Assert.assertEquals(tran53[i], Nilsimsa.TRAN53[i]);
        }
    }

    @Test
    public void transitionTableXORIsZero() {
        for(int i = 0; i < 256; i++) {
            int[] tran = Nilsimsa.generateTransitions(i);

            // bitwise XOR every value together
            int value = tran[0];
            for (int j = 1; j < tran.length; j++) {
                value = value ^ tran[j];
            }
            Assert.assertEquals("Value for complete XOR of transition table " + i + " was not 0", 0, value);
        }
    }
}
