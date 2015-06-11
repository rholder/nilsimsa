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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NilsimsaHashTest {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    public static final String SMALL_VALUE = "abcdefgh";
    public static final String LONG_VALUE = "This is a much more ridiculous test because of 21347597.";

    public static final String SIMILAR_1 = "The rain in Spain falls mostly in the plains.";
    public static final String SIMILAR_2 = "The rain in Spain falls mainly in the plains.";

    @Test
    public void happyPathDefault() {
        check(new Nilsimsa(), "14c8118000000000030800000004042004189020001308014088003280000078", SMALL_VALUE);
        check(new Nilsimsa(), "5d9c6a6b22384bcd524a8d414d82237777433fc1a07a02c3e06985d96ecdf8fb", LONG_VALUE);
        check(new Nilsimsa(), "039020eb1050188be400091130981860648e39f5b1246d8c3c3c7623801186ac", SIMILAR_1);
        check(new Nilsimsa(), "23b000e908501883c408019410d83a60c48f1977a3246ccc3cbc7213c81104bc", SIMILAR_2);
    }

    @Test
    public void happyPath17() {
        int[] tran17 = Nilsimsa.generateTransitions(17);
        check(new Nilsimsa(tran17), "001210201001000200470001180808120104800100186080000a044020020500", SMALL_VALUE);
        check(new Nilsimsa(tran17), "55c40c9aac438bf1b698a3a9ca3632b4d52f4cedc4f596b66fb1e0704e08aa01", LONG_VALUE);
        check(new Nilsimsa(tran17), "d004808d0a50fe2b2148c43594002505ae24aca01956900620a470cf53449c72", SIMILAR_1);
        check(new Nilsimsa(tran17), "d084c09d0800fa2b234a443896122515ae040ce01956944621b0708647461cf2", SIMILAR_2);
    }

    @Test
    public void defaultAnd17() {
        // default is 53
        Nilsimsa a = new Nilsimsa().update(SMALL_VALUE.getBytes(UTF8));
        Nilsimsa b = new Nilsimsa().update(LONG_VALUE.getBytes(UTF8));

        Assert.assertEquals(4, Nilsimsa.compare(a.digest(), b.digest()));
        Assert.assertEquals(4, Nilsimsa.compare(b.digest(), a.digest()));

        int[] tran = Nilsimsa.generateTransitions(17);
        Nilsimsa c = new Nilsimsa(tran).update(SMALL_VALUE.getBytes(UTF8));
        Nilsimsa d = new Nilsimsa(tran).update(LONG_VALUE.getBytes(UTF8));

        Assert.assertEquals(-10, Nilsimsa.compare(c.digest(), d.digest()));
        Assert.assertEquals(-10, Nilsimsa.compare(d.digest(), c.digest()));
    }

    @Test
    public void findMinMaxTransitionTable() {
        // generate 256 digests of two similar Strings
        List<int[]> nilsimsas1 = new ArrayList<int[]>();
        List<int[]> nilsimsas2 = new ArrayList<int[]>();
        for (int i = 0; i < 256; i++) {
            int[] table = Nilsimsa.generateTransitions(i);

            nilsimsas1.add(new Nilsimsa(table).update(SIMILAR_1.getBytes(UTF8)).digest());
            nilsimsas2.add(new Nilsimsa(table).update(SIMILAR_2.getBytes(UTF8)).digest());
        }

        int max = -1;
        int min = 129;

        int minIndex = -1;
        int maxIndex = -1;
        for (int i = 0; i < 256; i++) {
            int[] a = nilsimsas1.get(i);
            int[] b = nilsimsas2.get(i);
            int value = Nilsimsa.compare(a, b);
            if(value > max) {
                maxIndex = i;
                max = value;
            }
            if(value < min) {
                minIndex = i;
                min = value;
            }
        }
        System.out.println("index = " + maxIndex + ", max = " + max);
        System.out.println("index = " + minIndex + ", min = " + min);
        Assert.assertEquals(109, max);
        Assert.assertEquals(85, min);
    }

    private void check(Nilsimsa n, String hash, String value) {
        n.update(value.getBytes(UTF8));
        Assert.assertEquals("Unexpected hash value", hash, n.toHexDigest());
    }
}
