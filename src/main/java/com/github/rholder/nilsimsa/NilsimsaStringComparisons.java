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

/*
 * Copyright (c) 2012-2014 Diffeo, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.rholder.nilsimsa;

import static java.util.Arrays.sort;

/**
 * This class provides an implementation of the Nilsimsa locality-sensitive hashing algorithm.
 */
public class NilsimsaStringComparisons {

    /**
     * Compute the difference in bits between digest1 and digest2. Returns -127
     * to 128; 128 is the same, -127 is different.
     *
     * @param blob1 first digest to compare
     * @param blob2 second digest to compare
     * @return a value between -127 and 128, from least similar to most
     */
    public static int compare(String blob1, String blob2) {
        Nilsimsa a = new Nilsimsa().update(blob1.getBytes());
        Nilsimsa b = new Nilsimsa().update(blob2.getBytes());

        return Nilsimsa.compare(a.toHexDigest(), b.toHexDigest());
    }

    public static int compareAsSet(String blob1, String blob2) {
        String sortedBlob1 = sortSet(blob1);
        String sortedBlob2 = sortSet(blob2);

        return NilsimsaStringComparisons.compare(sortedBlob1, sortedBlob2);
    }

    private static String sortSet(String blob) {
        String[] set = blob.split("\\s+");  //split on whitespace
        sort(set);
        StringBuilder ret = new StringBuilder();
        for(String str:set){
            ret.append(str);
            ret.append(" ");
        }

        return ret.toString().trim();
    }

}
