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

public class NilsimsaPortingTest {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    public static final String STRING1 = "abcdefgh";
    public static final String DIGEST1 = "14c8118000000000030800000004042004189020001308014088003280000078";

    public static final String STRING2_1ST = "abcd";
    public static final String STRING2_2ND = "efgh";

    public static final String STRING3 = "ijk";

    public static final String DIGEST2 = "14c811840010000c0328200108040630041890200217582d4098103280000078";

    @Test
    public void digestComparison() {

        Nilsimsa n1 = new Nilsimsa().update(STRING1.getBytes(UTF8));
        Assert.assertEquals("Unexpected digest", DIGEST1, n1.toHexDigest());

        Nilsimsa n2 = new Nilsimsa()
                .update(STRING2_1ST.getBytes(UTF8))
                .update(STRING2_2ND.getBytes(UTF8));

        Assert.assertEquals("Unexpected digest", DIGEST1, n2.toHexDigest());
        Assert.assertTrue("Digests do not match", n1.toHexDigest().equals(n2.toHexDigest()));

        n1.update(STRING3.getBytes(UTF8));
        Assert.assertEquals("Unexpected digest", DIGEST2, n1.toHexDigest());

        Assert.assertEquals("Unexpected bit difference", 109, Nilsimsa.compare(n1.digest(), n2.digest()));
        Assert.assertEquals("Unexpected hex difference", 109, Nilsimsa.compare(n1.toHexDigest(), n2.toHexDigest()));
    }
}
